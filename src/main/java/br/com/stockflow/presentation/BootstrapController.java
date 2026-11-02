package br.com.stockflow.presentation;

import br.com.stockflow.application.dto.BootstrapRequest;
import br.com.stockflow.application.dto.BootstrapResponse;
import br.com.stockflow.application.services.BootstrapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bootstrap")
public class BootstrapController {

    private final BootstrapService bootstrapService;

    public BootstrapController(BootstrapService bootstrapService) {
        this.bootstrapService = bootstrapService;
    }

    @PostMapping("/admin")
    public ResponseEntity<BootstrapResponse> criarAdministrador(
            @RequestHeader(name = "X-Bootstrap-Key", required = false) String chave,
            @Valid @RequestBody BootstrapRequest requisicao
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bootstrapService.criarAdministrador(chave, requisicao));
    }
}
