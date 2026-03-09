package com.gymtracker.mapper;

import com.gymtracker.dto.WaitlistResponse;
import com.gymtracker.entity.Waitlist;

public class WaitlistMapper {
    public static WaitlistResponse toDto(Waitlist w) {
        if (w == null) return null;
        WaitlistResponse r = new WaitlistResponse();
        r.setId(w.getId());
        r.setScheduleId(w.getScheduleId());
        r.setMemberId(w.getMemberId());
        r.setPosition(w.getPosition());
        r.setCreatedAt(w.getCreatedAt());
        return r;
    }
}
