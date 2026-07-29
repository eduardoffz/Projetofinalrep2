package com.frota.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.frota.model.*;
import com.frota.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository r, PasswordEncoder pe, TokenService t) {
        this.usuarioRepository = r; this.passwordEncoder = pe; this.tokenService = t;
    }

    public Usuario cadastrar(UsuarioRequest dto) {
        if (usuarioRepository.buscarPorEmail(dto.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ja cadastrado");

        String role = dto.getRole() != null ? dto.getRole().toUpperCase() : "CLIENTE";
        if (!role.equals("CLIENTE") && !role.equals("PROPRIETARIO") && !role.equals("ADMIN"))
            role = "CLIENTE";

        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setSenha(passwordEncoder.encode(dto.getSenha()));
        u.setRole(role);
        u.setTelefone(dto.getTelefone());
        return usuarioRepository.criar(u);
    }

    public LoginResponse login(LoginRequest dto) {
        Usuario u = usuarioRepository.buscarPorEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha invalidos"));
        if (!passwordEncoder.matches(dto.getSenha(), u.getSenha()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha invalidos");
        return new LoginResponse(tokenService.gerarToken(u), u.getNome(), u.getEmail(), u.getRole());
    }
}
