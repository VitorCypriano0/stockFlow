package br.com.stockflow.controllers;

import br.com.stockflow.dto.FornecedorRequest;
import br.com.stockflow.dto.FornecedorResponse;
import br.com.stockflow.services.FornecedorService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    @GetMapping
    public List<FornecedorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public FornecedorResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(requisicao));
    }

    @PutMapping("/{id}")
    public FornecedorResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorRequest requisicao
    ) {
        return service.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
