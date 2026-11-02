package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.TokenRevogado;
import br.com.stockflow.domain.repository.TokenRevogadoRepository;
import br.com.stockflow.infra.persistence.jpa.TokenRevogadoJpaRepository;
import java.time.Instant;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRevogadoRepositoryAdapter implements TokenRevogadoRepository {

    private final TokenRevogadoJpaRepository jpaRepository;

    public TokenRevogadoRepositoryAdapter(TokenRevogadoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public long deleteByExpiraEmLessThanEqual(Instant instante) {
        return jpaRepository.deleteByExpiraEmLessThanEqual(instante);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public TokenRevogado save(TokenRevogado tokenRevogado) {
        return jpaRepository.save(tokenRevogado);
    }
}
