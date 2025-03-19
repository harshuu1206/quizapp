package com.example.quizchat.controller;

import com.example.quizchat.model.User;

import com.example.quizchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        // ✅ Encode the password before saving
        String rawPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        user.setPassword(hashedPassword);

        User newUser = userService.createUser(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    // ✅ Login with password verification
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String rawPassword = loginRequest.get("password");

        return userService.getUserByUsername(username)
                .map(user -> {
                    boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());

                    if (passwordMatches) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Login successful");
                        response.put("id", user.getId());
                        response.put("username", user.getUsername());
                        response.put("email", user.getEmail());
                        response.put("isAdmin", user.isAdmin());
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"));
    }
}