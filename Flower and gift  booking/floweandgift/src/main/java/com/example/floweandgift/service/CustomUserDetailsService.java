package com.example.floweandgift.service;

import com.example.floweandgift.model.User;
import com.example.floweandgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final ConcurrentHashMap<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockoutTime = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 15 * 60 * 1000; // 15 minutes

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if account is locked
        if (isAccountLocked(username)) {
            throw new UsernameNotFoundException("Account is temporarily locked due to too many failed login attempts");
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        User user = userOptional.get();

        // Handle null role by setting default and saving
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
            userRepository.save(user);
        }

        // Reset login attempts on successful load
        loginAttempts.remove(username);
        lockoutTime.remove(username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                !user.isAccountLocked(), // account non-locked
                true, // credentials non-expired
                true, // account non-expired
                true, // account non-disabled
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public void recordFailedLogin(String username) {
        loginAttempts.computeIfAbsent(username, k -> new AtomicInteger(0)).incrementAndGet();
        if (getLoginAttempts(username) >= MAX_ATTEMPTS) {
            lockoutTime.put(username, System.currentTimeMillis());
        }
    }

    public void recordSuccessfulLogin(String username) {
        loginAttempts.remove(username);
        lockoutTime.remove(username);
    }

    public int getLoginAttempts(String username) {
        AtomicInteger attempts = loginAttempts.get(username);
        return attempts != null ? attempts.get() : 0;
    }

    public boolean isAccountLocked(String username) {
        Long lockTime = lockoutTime.get(username);
        if (lockTime == null) return false;

        if (System.currentTimeMillis() - lockTime > LOCKOUT_DURATION) {
            // Lockout period expired
            loginAttempts.remove(username);
            lockoutTime.remove(username);
            return false;
        }
        return true;
    }

    public long getRemainingLockoutTime(String username) {
        Long lockTime = lockoutTime.get(username);
        if (lockTime == null) return 0;

        long elapsed = System.currentTimeMillis() - lockTime;
        return Math.max(0, LOCKOUT_DURATION - elapsed);
    }

    public User findUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }
}
