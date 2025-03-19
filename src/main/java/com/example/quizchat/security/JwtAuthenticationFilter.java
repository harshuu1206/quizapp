//package com.example.quizchat.security;
//
//import com.example.quizchat.service.UserService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtUtils jwtUtils;
//    private final UserService userService;
//
//    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
//        this.jwtUtils = jwtUtils;
//        this.userService = userService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        // If there's no Authorization header or it doesn't start with "Bearer ", move to next filter
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Extract token from "Bearer <token>"
//        String token = authHeader.substring(7);
//        String username = jwtUtils.extractUsername(token);
//
//        // If username is found and the user is not already authenticated
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userService.getUserByUsername(username).map(user ->
//                    User.withUsername(user.getUsername())
//                            .password(user.getPassword()) // Password is already hashed
//                            .roles(user.isAdmin() ? "ADMIN" : "USER")
//                            .build()
//            ).orElse(null);
//
//            // Validate the token and set authentication context
//            if (jwtUtils.validateToken(token, username) && userDetails != null) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // Set authentication in security context
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        // Continue the filter chain
//        filterChain.doFilter(request, response);
//    }
//
//}
