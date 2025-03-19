package com.example.quizchat.service;

import com.example.quizchat.model.User;
import com.example.quizchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public User createUser(User user) {
        System.out.println("🔹 Raw Password Before Encoding: " + user.getPassword());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("🔹 Hashed Password Stored in DB: " + hashedPassword);

        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
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

    // 🔹 Add this method to manually test password matching
    public void testPasswordMatching() {
        String rawPassword = "123456"; // The original password
        String hashedPassword =  userRepository.findByUsername("testuser").get().getPassword(); // Copy from DB

        boolean isMatch = passwordEncoder.matches(rawPassword, hashedPassword);
        System.out.println("✅ Manual Check - Password Matches: " + isMatch);
    }
}
