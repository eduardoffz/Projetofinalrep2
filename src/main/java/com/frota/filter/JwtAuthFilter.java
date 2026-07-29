package com.frota.filter;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.frota.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    public JwtAuthFilter(TokenService t) { this.tokenService = t; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Jws<Claims> claims = tokenService.validarToken(header.substring(7));
                req.setAttribute("usuarioLogado", claims.getPayload().getSubject());
                req.setAttribute("usuarioNome", claims.getPayload().get("nome", String.class));
                req.setAttribute("usuarioRole", claims.getPayload().get("role", String.class));
                req.setAttribute("usuarioId", claims.getPayload().get("userId", Long.class));
            } catch (Exception e) { res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); return; }
        }
        chain.doFilter(req, res);
    }
}
