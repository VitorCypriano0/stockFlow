package br.com.stockflow.controllers;

import br.com.stockflow.dto.HistoricoResponse;
import br.com.stockflow.services.MovimentacaoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    private final MovimentacaoService service;

    public HistoricoController(MovimentacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<HistoricoResponse> listar() {
        return service.listarHistorico();
    }
}
