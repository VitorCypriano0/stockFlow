package br.com.stockflow.controllers;

import br.com.stockflow.dto.AlmoxarifeRequest;
import br.com.stockflow.dto.AlmoxarifeResponse;
import br.com.stockflow.services.AlmoxarifeService;
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
@RequestMapping("/api/almoxarifes")
@SecurityRequirement(name = "bearerAuth")
public class AlmoxarifeController {

    private final AlmoxarifeService service;

    public AlmoxarifeController(AlmoxarifeService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlmoxarifeResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AlmoxarifeResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AlmoxarifeResponse> criar(@Valid @RequestBody AlmoxarifeRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(requisicao));
    }

    @PutMapping("/{id}")
    public AlmoxarifeResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlmoxarifeRequest requisicao
    ) {
        return service.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
