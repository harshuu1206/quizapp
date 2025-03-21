package com.example.quizchat.dto;

public class LoginRequest {
    private String email;
    private String password;

    // ✅ Constructor
    public LoginRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginRequest(String username, String password) {
        this.email = username;
        this.password = password;
    }


}
