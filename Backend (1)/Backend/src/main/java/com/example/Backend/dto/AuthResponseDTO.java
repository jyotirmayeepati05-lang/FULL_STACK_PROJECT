package com.example.Backend.dto;

public class AuthResponseDTO {

    private String token;
    private String type = "Bearer";
    private UserResponseDTO user;
    private long expiresIn;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, UserResponseDTO user, long expiresIn) {
        this.token = token;
        this.type = "Bearer";
        this.user = user;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
