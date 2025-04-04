package com.example.quizchat.dto;

public class LoginRequest {
    private String email;
    private String password;

    // ✅ No-Args Constructor
    public LoginRequest() {}

    // ✅ Parameterized Constructor with Correct Field Names
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ✅ Getters and Setters
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
}