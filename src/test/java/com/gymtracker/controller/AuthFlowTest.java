package com.gymtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymtracker.dto.AuthRequest;
import com.gymtracker.dto.RegisterRequest;
import com.gymtracker.enums.Role;
import com.gymtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void register_first_user_returns_created() throws Exception {
        RegisterRequest firstUser = new RegisterRequest();
        firstUser.setUsername("testadmin");
        firstUser.setPassword("TestPassword123!");
        firstUser.setRole(Role.ROLE_ADMIN);

        String body = objectMapper.writeValueAsString(firstUser);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void login_with_valid_credentials_returns_ok() throws Exception {
        RegisterRequest user = new RegisterRequest();
        user.setUsername("logintest");
        user.setPassword("LoginPass123!");
        user.setRole(Role.ROLE_MEMBER);

        String registerBody = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        AuthRequest login = new AuthRequest();
        login.setUsername("logintest");
        login.setPassword("LoginPass123!");

        String loginBody = objectMapper.writeValueAsString(login);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk());
    }
}
