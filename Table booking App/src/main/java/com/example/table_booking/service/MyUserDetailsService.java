package com.example.table_booking.service;

import com.example.table_booking.model.User;
import com.example.table_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {
        List<User> users = userRepository.findByEmail(value);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        if (users.size() > 1) {
            throw new RuntimeException("Duplicate users found for email: " + value);
        }
        User user = users.get(0);

        // Convert user roles to Spring Security authorities with ROLE_ prefix
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null) {
            String role = user.getRole().name();
            // Ensure role has ROLE_ prefix (add it if missing)
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),        // enabled
                true,                   // accountNonExpired
                true,                   // credentialsNonExpired
                true,                   // accountNonLocked
                authorities
        );
    }
}
