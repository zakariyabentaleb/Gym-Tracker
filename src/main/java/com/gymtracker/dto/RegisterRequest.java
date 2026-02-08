package com.gymtracker.dto;

import com.gymtracker.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 200)
    private String password;

    /**
     * Par défaut: ROLE_MEMBER côté service si null.
     */
    private Role role;
}

