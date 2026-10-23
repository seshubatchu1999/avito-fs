package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class UserLoginRequestDto {

    @NotBlank
    @JsonProperty("username")
    private String username;

    @NotBlank
    @JsonProperty("password")
    private String password;

    @JsonProperty("as_admin")
    private Boolean asAdmin;

    public UserLoginRequestDto() {}

    public UserLoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getAsAdmin() { return asAdmin; }
    public void setAsAdmin(Boolean asAdmin) { this.asAdmin = asAdmin; }
}