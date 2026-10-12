package com.ijse.moviebooking.security;

import com.ijse.moviebooking.entity.User;
import com.ijse.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return org.springframework.security.core.userdetails.User.withUsername(u.getEmail())
                .password(u.getPassword()).authorities("ROLE_" + u.getRole().name()).build();
    }
}
