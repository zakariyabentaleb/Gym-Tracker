package com.gymtracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class AppUser {

    @Id
    @Column("id")
    private Long id;

    @Column("username")
    private String username;

    /**
     * BCrypt hash.
     */
    @Column("password")
    private String password;

    /**
     * Comma-separated roles, e.g. "ROLE_ADMIN,ROLE_MEMBER".
     */
    @Column("roles")
    private String roles;

    public List<String> rolesAsList() {
        if (roles == null || roles.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }
}
