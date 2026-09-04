package com.example.customer_service.component;

import com.example.customer_service.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.FilterChain;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    JwtFilter(JwtService j){this.jwt = j;}

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            if (jwt.isTokenValid(token)) {
                var email = jwt.extractEmail(token);
                var auth = new UsernamePasswordAuthenticationToken(
                        email, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res);
    }
}
