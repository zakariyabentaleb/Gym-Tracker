package com.gymtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymtracker.dto.RegisterRequest;
import com.gymtracker.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void protectedEndpoint_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_then_access_me_with_token() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("u1");
        register.setPassword("secret123");
        register.setRole(Role.ROLE_MEMBER);

        String registerBody = objectMapper.writeValueAsString(register);
        String json = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // crude extraction without adding extra JSONPath deps
        String jwt = json.replaceAll(".*\\\"token\\\":\\\"", "")
                .replaceAll("\\\".*", "");

        mockMvc.perform(get("/api/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }
}
package com.gymtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymtracker.dto.AuthRequest;
import com.gymtracker.dto.RegisterRequest;
import com.gymtracker.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void protectedEndpoint_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_then_access_me_with_token() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("u1");
        register.setPassword("secret123");
        register.setRole(Role.ROLE_MEMBER);

        String registerBody = objectMapper.writeValueAsString(register);
        String token = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // crude extraction without adding extra JSONPath deps
        String jwt = token.replaceAll(".*\\\"token\\\":\\\"", "")
                .replaceAll("\\\".*", "");

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }
}

