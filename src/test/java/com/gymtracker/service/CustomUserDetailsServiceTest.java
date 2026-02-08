package com.gymtracker.service;

import com.gymtracker.entity.AppUser;
import com.gymtracker.enums.Role;
import com.gymtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CustomUserDetailsServiceTest {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void loadUserByUsername_returnsUserWithAuthorities() {
        userRepository.save(AppUser.builder()
                .username("member1")
                .password(passwordEncoder.encode("secret123"))
                .roles(Role.ROLE_MEMBER.name())
                .build());

        UserDetails details = customUserDetailsService.loadUserByUsername("member1");
        assertThat(details.getUsername()).isEqualTo("member1");
        assertThat(details.getAuthorities()).extracting("authority")
                .contains(Role.ROLE_MEMBER.name());
    }
}
