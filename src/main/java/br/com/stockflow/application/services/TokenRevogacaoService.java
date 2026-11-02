package br.com.stockflow.application.services;

import br.com.stockflow.domain.entities.TokenRevogado;
import br.com.stockflow.application.exceptions.RegraNegocioException;
import br.com.stockflow.domain.repository.TokenRevogadoRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenRevogacaoService {

    private final TokenRevogadoRepository repository;

    public TokenRevogacaoService(TokenRevogadoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void revogar(String id, Instant expiraEm) {
        if (id == null || id.isBlank() || expiraEm == null) {
            throw new RegraNegocioException("Não foi possível identificar a sessão para encerrar.");
        }

        Instant agora = Instant.now();
        repository.deleteByExpiraEmLessThanEqual(agora);
        if (expiraEm.isAfter(agora)) {
            repository.save(new TokenRevogado(id, expiraEm));
        }
    }

    @Transactional(readOnly = true)
    public boolean estaRevogado(String id) {
        return repository.existsById(id);
    }
}
