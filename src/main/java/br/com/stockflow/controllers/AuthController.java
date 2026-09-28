package br.com.stockflow.controllers;

import br.com.stockflow.dto.AlmoxarifeRequest;
import br.com.stockflow.dto.AlmoxarifeResponse;
import br.com.stockflow.dto.AuthResponse;
import br.com.stockflow.dto.LoginRequest;
import br.com.stockflow.services.AlmoxarifeService;
import br.com.stockflow.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AlmoxarifeService almoxarifeService;

    public AuthController(AuthService authService, AlmoxarifeService almoxarifeService) {
        this.authService = authService;
        this.almoxarifeService = almoxarifeService;
    }

    /** Endpoint público do login; retorna o token JWT quando e-mail e senha são válidos. */
    @PostMapping("/login")
    public AuthResponse entrar(@Valid @RequestBody LoginRequest requisicao) {
        return authService.entrar(requisicao);
    }

    /** Primeiro cadastro cria a conta; um almoxarife antigo pode definir a senha com CPF e e-mail. */
    @PostMapping("/registrar")
    public ResponseEntity<AlmoxarifeResponse> registrar(@Valid @RequestBody AlmoxarifeRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(almoxarifeService.registrar(requisicao));
    }
}
