package br.com.stockflow.controllers;

import br.com.stockflow.dto.HistoricoResponse;
import br.com.stockflow.dto.MovimentacaoRequest;
import br.com.stockflow.dto.MovimentacaoResponse;
import br.com.stockflow.services.MovimentacaoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MovimentacaoResponse> criar(@Valid @RequestBody MovimentacaoRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(requisicao));
    }

    @PutMapping("/{id}")
    public MovimentacaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoRequest requisicao
    ) {
        return service.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
