//package com.example.quizchat.security;
//
//
//import com.example.quizchat.model.User;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JwtUtils {
//
//    private static final String SECRET_KEY = "your-very-secure-key-at-least-32-characters-long"; // At least 32 chars
//    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
//
//    private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
//
//    // ✅ Generate JWT Token
//    public String generateToken(User user) {
//        return Jwts.builder()
//                .subject(user.getUsername())
//                .claim("userId", user.getId())
//                .claim("isAdmin", user.isAdmin())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(key) // ✅ Correct signing method
//                .compact();
//    }
//
//    // ✅ Extract Username from JWT Token
//    public String extractUsername(String token) {
//        return extractClaims(token).getSubject();
//    }
//
//    // ✅ Extract Claims from Token
//    private Claims extractClaims(String token) {
//        return Jwts.parser() // ✅ Use `setSigningKey(key)` instead of `verifyWith(key)`
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // ✅ Validate JWT Token
//    public boolean validateToken(String token, String username) {
//        return extractUsername(token).equals(username) && !isTokenExpired(token);
//    }
//
//    // ✅ Check if Token is Expired
//    private boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }
//}