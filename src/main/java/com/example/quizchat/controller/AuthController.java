package com.example.quizchat.controller;

import com.example.quizchat.dto.AuthRequest;
import com.example.quizchat.dto.AuthResponse;
import com.example.quizchat.dto.LoginRequest;
import com.example.quizchat.dto.SignupRequest;
import com.example.quizchat.model.JwtUtils;
import com.example.quizchat.model.User;

import com.example.quizchat.repository.UserRepository;
import com.example.quizchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        User newUser = new User();
        newUser.setEmail(signupRequest.getEmail());
        newUser.setUsername(signupRequest.getUsername());

        // ✅ Fix: Only hash if not already hashed
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
        newUser.setPassword(hashedPassword);

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        System.out.println("🔍 Checking email: " + authRequest.getEmail());
        System.out.println("🔍 Checking password: " + authRequest.getPassword());

        // Fetch the user from DB to verify password manually
        Optional<User> optionalUser = userRepository.findByEmail(authRequest.getEmail());
        if (optionalUser.isEmpty()) {
            System.out.println("❌ User not found with email: " + authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        User user = optionalUser.get();
        System.out.println("✅ Hashed password in DB: " + user.getPassword());

        // Manual check if password matches
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            System.out.println("❌ Password does not match!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid password"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            System.out.println("✅ Authentication successful!");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = userService.generateToken(user);

            // ✅ Include userId in the response
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("userId", user.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }
}