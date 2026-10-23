package com.avito.logistics.controller;

import com.avito.logistics.model.dto.UpdatePermissionsRequestDto;
import com.avito.logistics.model.dto.UserProfileDto;
import com.avito.logistics.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final AuthService authService;

    @Value("${app.auth.enforced:false}")
    private boolean authEnforced;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PatchMapping("/{username}/permissions")
    public ResponseEntity<UserProfileDto> updatePermissions(
            @PathVariable("username") String username,
            @RequestBody UpdatePermissionsRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (authEnforced && userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        String actor = userDetails != null ? userDetails.getUsername() : null;
        UserProfileDto updated = authService.updatePermissions(username, request, actor);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> listUsers(@AuthenticationPrincipal UserDetails userDetails) {
        if (authEnforced && userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        String actor = userDetails != null ? userDetails.getUsername() : null;
        List<UserProfileDto> users = authService.listAllUsers(actor);
        return ResponseEntity.ok(users);
    }
}