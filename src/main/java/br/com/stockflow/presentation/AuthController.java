package br.com.stockflow.presentation;

import br.com.stockflow.application.dto.AlmoxarifeAutenticado;
import br.com.stockflow.application.dto.AlmoxarifeRequest;
import br.com.stockflow.application.dto.AlmoxarifeResponse;
import br.com.stockflow.application.dto.LoginRequest;
import br.com.stockflow.application.dto.LoginResponse;
import br.com.stockflow.application.services.AlmoxarifeService;
import br.com.stockflow.application.services.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AlmoxarifeService almoxarifeService;
    private final TokenService tokenService;

    public AuthController(
            AlmoxarifeService almoxarifeService,
            TokenService tokenService
    ) {
        this.almoxarifeService = almoxarifeService;
        this.tokenService = tokenService;
    }

    /** Endpoint público do login; retorna o token JWT quando e-mail e senha são válidos. */
    @PostMapping("/login")
    public LoginResponse entrar(@Valid @RequestBody LoginRequest requisicao) {
        return almoxarifeService.validarAlmoxarifeAutenticadoRetornaToken(requisicao);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public AlmoxarifeResponse buscarAlmoxarifeAutenticado(
            @AuthenticationPrincipal AlmoxarifeAutenticado almoxarife
    ) {
        return almoxarifeService.buscarAutenticado(almoxarife);
    }

    /** Primeiro cadastro cria a empresa e seu almoxarife; registros antigos podem definir a senha. */
    @PostMapping("/registrar")
    public ResponseEntity<AlmoxarifeResponse> registrar(@Valid @RequestBody AlmoxarifeRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(almoxarifeService.registrar(requisicao));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> sair(@AuthenticationPrincipal AlmoxarifeAutenticado almoxarife) {
        tokenService.revogar(almoxarife.tokenId());
        return ResponseEntity.noContent().build();
    }
}
