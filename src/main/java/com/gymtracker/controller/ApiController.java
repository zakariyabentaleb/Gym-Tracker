package com.gymtracker.controller;

import com.gymtracker.dto.MeResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        return "Hello, " + (authentication != null ? authentication.getName() : "anonymous") + "!";
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new MeResponse(authentication.getName(), roles);
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminPing() {
        return "admin-ok";
    }
}
