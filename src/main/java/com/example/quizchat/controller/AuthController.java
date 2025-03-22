package com.example.quizchat.controller;

import com.example.quizchat.dto.LoginRequest;
import com.example.quizchat.dto.SignupRequest;
import com.example.quizchat.model.JwtUtils;
import com.example.quizchat.model.User;

import com.example.quizchat.repository.UserRepository;
import com.example.quizchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8091")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils; // ✅ Inject JWT utility

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        // Check if user already exists
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        // ✅ Hash the password before saving
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User newUser = new User();
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(hashedPassword); // ✅ Store hashed password
        newUser.setUsername(signupRequest.getUsername());

        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // ✅ Login with JWT Token
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("🔍 Checking login for email: " + loginRequest.getEmail());
        System.out.println("Raw Password Entered: " + loginRequest.getPassword());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        System.out.println("🗝️ Stored Hashed Password: " + user.getPassword());

        // Check password properly
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("❌ Password does NOT match!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid password"));
        }

        System.out.println("✅ Password matched successfully!");
        return ResponseEntity.ok(Map.of("message", "Login Successful"));
    }
}