package com.gymtracker.controller;

import com.gymtracker.dto.AuthRequest;
import com.gymtracker.dto.AuthResponse;
import com.gymtracker.dto.RegisterRequest;
import com.gymtracker.entity.AppUser;
import com.gymtracker.enums.Role;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.ROLE_MEMBER;

        // Bootstrap case: if no users exist, allow creation of the first user and force ADMIN role.
        if (userRepository.count() == 0) {
            role = Role.ROLE_ADMIN; // make the first user an admin so they can create others
        } else {
            // Require authenticated caller with ADMIN or RECEPTIONIST to create ANY user (including MEMBER)
            Authentication current = SecurityContextHolder.getContext().getAuthentication();
            if (current == null || !current.isAuthenticated() || current instanceof AnonymousAuthenticationToken) {
                throw new AccessDeniedException("Creating users requires authentication with ADMIN or RECEPTIONIST role");
            }
            boolean allowed = current.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_RECEPTIONIST".equals(a.getAuthority()));
            if (!allowed) {
                throw new AccessDeniedException("Insufficient authority to create users");
            }
        }

        userRepository.save(AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(role.name())
                .build());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(jwt));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (BadCredentialsException ex) {
            // GlobalExceptionHandler will format the JSON
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(""));
        }
    }
}
