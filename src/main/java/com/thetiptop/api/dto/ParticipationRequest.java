package com.thetiptop.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ParticipationRequest {

    @NotBlank
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

