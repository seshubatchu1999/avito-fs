package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponseDto {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "bearer";

    @JsonProperty("user")
    private UserProfileDto user;

    public TokenResponseDto() {}

    public TokenResponseDto(String accessToken, String tokenType, UserProfileDto user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "bearer";
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserProfileDto getUser() { return user; }
    public void setUser(UserProfileDto user) { this.user = user; }
}
