package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.TokenRevogado;
import java.time.Instant;

public interface TokenRevogadoRepository {

    long deleteByExpiraEmLessThanEqual(Instant instante);

    boolean existsById(String id);

    TokenRevogado save(TokenRevogado tokenRevogado);
}
