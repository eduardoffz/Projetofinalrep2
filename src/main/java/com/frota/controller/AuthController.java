package com.frota.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frota.model.*;
import com.frota.service.AuthService;
import com.frota.service.ProprietarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final ProprietarioService propService;

    public AuthController(AuthService a, ProprietarioService p) { this.authService = a; this.propService = p; }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioRequest dto) {
        Usuario u = authService.cadastrar(dto);
        if ("PROPRIETARIO".equals(u.getRole())) {
            propService.registrar(u.getId(), dto.getDocumento(), dto.getChavePix(), dto.getEndereco());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(u);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
