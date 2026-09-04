package com.example.customer_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final String JWT_SECRET = System.getenv("JWT_KEY");

    private SecretKey getSignInKey(){
        byte[] bytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean isTokenValid(String token){
        try{
            extractUsername(token);
            return true;
        } catch(Exception e){return false;}
    }
}
