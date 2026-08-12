package com.frota.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.frota.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    private SecretKey key;

    @PostConstruct
    public void init() { this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)); }

    public String gerarToken(Usuario u) {
        Instant now = Instant.now();
        return Jwts.builder().subject(u.getEmail()).claim("nome",u.getNome()).claim("role",u.getRole())
                .claim("userId",u.getId()).issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(now.plus(24,ChronoUnit.HOURS))).signWith(key).compact();
    }
    public Jws<Claims> validarToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
