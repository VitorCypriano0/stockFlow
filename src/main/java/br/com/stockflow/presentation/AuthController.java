package br.com.stockflow.presentation;

import br.com.stockflow.application.dto.AlmoxarifeRequest;
import br.com.stockflow.application.dto.AlmoxarifeResponse;
import br.com.stockflow.application.dto.AuthResponse;
import br.com.stockflow.application.dto.LoginRequest;
import br.com.stockflow.application.services.AlmoxarifeService;
import br.com.stockflow.application.services.AuthService;
import br.com.stockflow.application.services.TokenRevogacaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AlmoxarifeService almoxarifeService;
    private final TokenRevogacaoService tokenRevogacaoService;

    public AuthController(
            AuthService authService,
            AlmoxarifeService almoxarifeService,
            TokenRevogacaoService tokenRevogacaoService
    ) {
        this.authService = authService;
        this.almoxarifeService = almoxarifeService;
        this.tokenRevogacaoService = tokenRevogacaoService;
    }

    /** Endpoint público do login; retorna o token JWT quando e-mail e senha são válidos. */
    @PostMapping("/login")
    public AuthResponse entrar(@Valid @RequestBody LoginRequest requisicao) {
        return authService.entrar(requisicao);
    }

    /** Primeiro cadastro cria a empresa e seu almoxarife; registros antigos podem definir a senha. */
    @PostMapping("/registrar")
    public ResponseEntity<AlmoxarifeResponse> registrar(@Valid @RequestBody AlmoxarifeRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(almoxarifeService.registrar(requisicao));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> sair(@AuthenticationPrincipal Jwt jwt) {
        tokenRevogacaoService.revogar(jwt.getId(), jwt.getExpiresAt());
        return ResponseEntity.noContent().build();
    }
}
