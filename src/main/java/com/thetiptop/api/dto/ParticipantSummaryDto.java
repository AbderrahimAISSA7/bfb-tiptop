package com.thetiptop.api.dto;

import java.time.OffsetDateTime;

public class ParticipantSummaryDto {

    private Long id;
    private UserDto user;
    private CodeDto code;
    private PrizeDto prize;
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public CodeDto getCode() {
        return code;
    }

    public void setCode(CodeDto code) {
        this.code = code;
    }

    public PrizeDto getPrize() {
        return prize;
    }

    public void setPrize(PrizeDto prize) {
        this.prize = prize;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

