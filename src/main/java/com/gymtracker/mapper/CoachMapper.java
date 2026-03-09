package com.gymtracker.mapper;

import com.gymtracker.dto.CoachCreateRequest;
import com.gymtracker.dto.CoachResponse;
import com.gymtracker.entity.Coach;

public class CoachMapper {

    public static CoachResponse toDto(Coach c) {
        if (c == null) return null;
        CoachResponse r = new CoachResponse();
        r.setId(c.getId());
        r.setUserId(c.getUserId());
        r.setDisplayName(c.getDisplayName());
        r.setPhone(c.getPhone());
        r.setBio(c.getBio());
        r.setCertifications(c.getCertifications());
        r.setPhotoUrl(c.getPhotoUrl());
        r.setActive(c.getActive());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        return r;
    }

    public static Coach toEntity(CoachCreateRequest req) {
        if (req == null) return null;
        return Coach.builder()
                .userId(req.getUserId())
                .displayName(req.getDisplayName())
                .phone(req.getPhone())
                .bio(req.getBio())
                .certifications(req.getCertifications())
                .photoUrl(req.getPhotoUrl())
                .active(true)
                .build();
    }
}

