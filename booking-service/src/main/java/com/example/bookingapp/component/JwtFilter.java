package com.example.bookingapp.component;

import com.example.bookingapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtFilter(JwtService j){
        this.jwt = j;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if(jwt.isTokenValid(token)) {
                Long customerId = jwt.extractCustomerId(token);
                var auth = new UsernamePasswordAuthenticationToken(customerId,
                        token, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req,res);
    }
}