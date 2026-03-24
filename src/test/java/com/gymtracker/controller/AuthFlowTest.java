package com.gymtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymtracker.dto.AuthRequest;
import com.gymtracker.dto.RegisterRequest;
import com.gymtracker.entity.AppUser;
import com.gymtracker.enums.Role;
import com.gymtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthFlowTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    private ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void admin_can_register_and_login() throws Exception {
        // Register first user via bootstrap (no auth required, becomes admin)
        RegisterRequest firstUser = new RegisterRequest();
        firstUser.setUsername("admin1");
        firstUser.setPassword("AdminPass123!");
        firstUser.setRole(Role.ROLE_ADMIN);

        String firstUserBody = objectMapper.writeValueAsString(firstUser);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstUserBody))
                .andExpect(status().isCreated());

        // Now login with those credentials
        AuthRequest login = new AuthRequest();
        login.setUsername("admin1");
        login.setPassword("AdminPass123!");
        String loginBody = objectMapper.writeValueAsString(login);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk());
    }

    @Test
    void admin_creates_member_and_member_can_call_me() throws Exception {
        // Register first user via bootstrap (becomes admin, no auth required)
        RegisterRequest firstUser = new RegisterRequest();
        firstUser.setUsername("admin1");
        firstUser.setPassword("AdminPass123!");
        firstUser.setRole(Role.ROLE_ADMIN);

        String firstUserBody = objectMapper.writeValueAsString(firstUser);
        String firstUserJson = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstUserBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String adminToken = firstUserJson.replaceAll(".*\"token\":\"", "")
                .replaceAll("\".*", "");

        // use admin token to register a new member
        RegisterRequest register = new RegisterRequest();
        register.setUsername("member1");
        register.setPassword("secret123");
        register.setRole(Role.ROLE_MEMBER);

        String registerBody = objectMapper.writeValueAsString(register);
        String registerJson = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String memberToken = registerJson.replaceAll(".*\"token\":\"", "")
                .replaceAll("\".*", "");

        // member calls /api/me
        mockMvc.perform(get("/api/me")
                        .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk());
    }
}

