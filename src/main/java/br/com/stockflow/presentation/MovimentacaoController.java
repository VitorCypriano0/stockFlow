package br.com.stockflow.presentation;

import br.com.stockflow.application.dto.MovimentacaoRequest;
import br.com.stockflow.application.dto.MovimentacaoResponse;
import br.com.stockflow.application.exceptions.CredenciaisInvalidasException;
import br.com.stockflow.application.services.MovimentacaoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimentacoes")
@SecurityRequirement(name = "bearerAuth")
public class MovimentacaoController {

    private final MovimentacaoService service;

    public MovimentacaoController(MovimentacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovimentacaoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public MovimentacaoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponse> criar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MovimentacaoRequest requisicao
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(requisicao, idAlmoxarifeAutenticado(jwt)));
    }

    @PutMapping("/{id}")
    public MovimentacaoResponse atualizar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MovimentacaoRequest requisicao
    ) {
        return service.atualizar(id, requisicao, idAlmoxarifeAutenticado(jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.excluir(id, idAlmoxarifeAutenticado(jwt));
        return ResponseEntity.noContent().build();
    }

    private Long idAlmoxarifeAutenticado(Jwt jwt) {
        if (jwt == null) {
            throw new CredenciaisInvalidasException("Token de autenticação inválido.");
        }

        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new CredenciaisInvalidasException("Token de autenticação inválido.");
        }

        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException erro) {
            throw new CredenciaisInvalidasException("Token de autenticação inválido.");
        }
    }
}
