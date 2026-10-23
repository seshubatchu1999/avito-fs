package com.avito.logistics.security;

import com.avito.logistics.model.entity.UserEntity;
import com.avito.logistics.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        if (user.isCanInvoiceProcessing()) {
            authorities.add(new SimpleGrantedAuthority("PERMISSION_INVOICE"));
        }
        if (user.isCanMblProcessing()) {
            authorities.add(new SimpleGrantedAuthority("PERMISSION_MBL"));
        }

        return new User(user.getUsername(), user.getPasswordHash(), authorities);
    }
}
