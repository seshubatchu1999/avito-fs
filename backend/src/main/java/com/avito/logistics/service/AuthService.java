package com.avito.logistics.service;

import com.avito.logistics.model.dto.*;
import com.avito.logistics.model.entity.UserEntity;
import com.avito.logistics.repository.UserRepository;
import com.avito.logistics.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.auth.enforced:false}")
    private boolean authEnforced;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public UserProfileDto register(UserRegisterRequestDto request) {
        String cleanUsername = request.getUsername().trim().toLowerCase();
        if (cleanUsername.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username must not be empty.");
        }

        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username '" + cleanUsername + "' is already registered.");
        }

        long totalUsers = userRepository.count();
        boolean isAdmin = (totalUsers == 0) || Boolean.TRUE.equals(request.getIsAdmin());
        boolean canInvoice = isAdmin || Boolean.TRUE.equals(request.getCanInvoiceProcessing());
        boolean canMbl = isAdmin || Boolean.TRUE.equals(request.getCanMblProcessing());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity user = new UserEntity(cleanUsername, encodedPassword, isAdmin, canInvoice, canMbl);
        user.setCreatedAt(Instant.now().toString());

        UserEntity saved = userRepository.save(user);
        return toProfileDto(saved);
    }

    public TokenResponseDto login(UserLoginRequestDto request) {
        String cleanUsername = request.getUsername().trim().toLowerCase();
        UserEntity user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password.");
        }

        if (Boolean.TRUE.equals(request.getAsAdmin()) && !user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: User '" + cleanUsername + "' does not possess administrator privileges.");
        }

        String token = tokenProvider.generateToken(user.getUsername());
        return new TokenResponseDto(token, "bearer", toProfileDto(user));
    }

    public UserProfileDto getProfile(String username) {
        String cleanUsername = username.trim().toLowerCase();
        UserEntity user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        return toProfileDto(user);
    }

    @Transactional
    public UserProfileDto updatePermissions(String targetUsername, UpdatePermissionsRequestDto request, String actorUsername) {
        if (actorUsername != null) {
            UserEntity actor = userRepository.findByUsername(actorUsername.trim().toLowerCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required."));
            if (!actor.isAdmin() && authEnforced) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrative privileges required to update user permissions.");
            }
        }

        String cleanTarget = targetUsername.trim().toLowerCase();
        UserEntity target = userRepository.findByUsername(cleanTarget)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + cleanTarget + "' was not found."));

        if (request.getCanInvoiceProcessing() != null) {
            target.setCanInvoiceProcessing(request.getCanInvoiceProcessing());
        }
        if (request.getCanMblProcessing() != null) {
            target.setCanMblProcessing(request.getCanMblProcessing());
        }
        if (request.getIsAdmin() != null) {
            target.setAdmin(request.getIsAdmin());
            if (request.getIsAdmin()) {
                target.setCanInvoiceProcessing(true);
                target.setCanMblProcessing(true);
            }
        }

        UserEntity saved = userRepository.save(target);
        return toProfileDto(saved);
    }

    public List<UserProfileDto> listAllUsers(String actorUsername) {
        if (actorUsername != null && authEnforced) {
            UserEntity actor = userRepository.findByUsername(actorUsername.trim().toLowerCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required."));
            if (!actor.isAdmin()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrative privileges required to view all users.");
            }
        }
        return userRepository.findAll().stream()
                .map(this::toProfileDto)
                .collect(Collectors.toList());
    }

    public void checkInvoicePermission(String username) {
        if (!authEnforced && username == null) {
            return;
        }
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        UserEntity user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));
        if (!user.isAdmin() && !user.isCanInvoiceProcessing()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission for invoice processing. Contact an admin.");
        }
    }

    public void checkMblPermission(String username) {
        if (!authEnforced && username == null) {
            return;
        }
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        UserEntity user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));
        if (!user.isAdmin() && !user.isCanMblProcessing()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission for MBL processing. Contact an admin.");
        }
    }

    private UserProfileDto toProfileDto(UserEntity user) {
        return new UserProfileDto(
                user.getUsername(),
                user.isAdmin(),
                user.isCanInvoiceProcessing(),
                user.isCanMblProcessing(),
                user.getCreatedAt()
        );
    }
}