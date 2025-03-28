package com.example.quizchat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,@Lazy UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.extractToken(request);
        System.out.println("🔍 Received Token: " + token); // Log received token

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            System.out.println("👤 Extracted Username: " + username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails != null) {
                System.out.println("✅ UserDetails Loaded: " + userDetails.getUsername());

                var authentication = jwtTokenProvider.getAuthentication(token, userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("✅ Authentication Set for: " + authentication.getName());
            } else {
                System.out.println("❌ UserDetails is null for username: " + username);
            }
        } else {
            System.out.println("❌ Token is invalid or missing.");
        }

        chain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || path.startsWith("/api/auth/");  // ✅ Skip JWT check for login & register
    }
}