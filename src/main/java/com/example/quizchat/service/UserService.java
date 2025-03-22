package com.example.quizchat.service;

import com.example.quizchat.model.JwtUtils;
import com.example.quizchat.model.User;
import com.example.quizchat.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // ✅ This runs once when the application starts
    @PostConstruct
    public void init() {
        System.out.println("🔄 Checking and updating passwords...");
        rehashOldPasswords();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        // ✅ Ensure password is hashed before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        // ✅ Ensure password remains hashed when updating
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            if (!user.getPassword().equals(existingUser.get().getPassword())) {
                // Only hash the password if it has changed
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public String generateToken(User user) {
        return jwtUtils.generateToken(user);
    }

    // ✅ Compare entered password with stored hashed password
    public boolean validatePassword(String enteredPassword, String storedHashedPassword) {
        return passwordEncoder.matches(enteredPassword, storedHashedPassword);
    }

    // ✅ Fix: Rehash old passwords if they are not hashed yet
    public void rehashOldPasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getPassword().startsWith("$2a$")) { // If password is not already hashed
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                System.out.println("✅ Password updated for user: " + user.getEmail());
            }
        }
    }
}