package com.thetiptop.api.dto;

import java.time.OffsetDateTime;

public class AdminCodeDto {

    private Long id;
    private String code;
    private String status;
    private OffsetDateTime expirationDate;
    private OffsetDateTime issueDate;
    private OffsetDateTime useDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private PrizeDto prize;
    private UserDto validatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(OffsetDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public OffsetDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(OffsetDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public OffsetDateTime getUseDate() {
        return useDate;
    }

    public void setUseDate(OffsetDateTime useDate) {
        this.useDate = useDate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PrizeDto getPrize() {
        return prize;
    }

    public void setPrize(PrizeDto prize) {
        this.prize = prize;
    }

    public UserDto getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(UserDto validatedBy) {
        this.validatedBy = validatedBy;
    }
}
