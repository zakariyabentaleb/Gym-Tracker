package com.gymtracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("coaches")
public class Coach {

    @Id
    @Column("id")
    private Long id;

    /** link to users.id (optional) */
    @Column("user_id")
    private Long userId;

    @Column("display_name")
    private String displayName;

    @Column("phone")
    private String phone;

    @Column("bio")
    private String bio;

    @Column("certifications")
    private String certifications;

    @Column("photo_url")
    private String photoUrl;

    @Column("active")
    private Boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}

