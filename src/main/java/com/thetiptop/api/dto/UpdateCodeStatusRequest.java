package com.thetiptop.api.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateCodeStatusRequest {

    @NotBlank
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
