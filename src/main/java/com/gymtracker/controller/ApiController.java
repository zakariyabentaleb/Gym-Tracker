package com.gymtracker.controller;

import com.gymtracker.dto.MeResponse;
import com.gymtracker.entity.AppUser;
import com.gymtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;

    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        return "Hello, " + (authentication != null ? authentication.getName() : "anonymous") + "!";
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        AppUser user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        return new MeResponse(user.getId(), authentication.getName(), roles);
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminPing() {
        return "admin-ok";
    }
}
