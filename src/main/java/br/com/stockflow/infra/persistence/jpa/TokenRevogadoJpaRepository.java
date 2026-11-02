package br.com.stockflow.infra.persistence.jpa;

import br.com.stockflow.domain.entities.TokenRevogado;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRevogadoJpaRepository extends JpaRepository<TokenRevogado, String> {

    long deleteByExpiraEmLessThanEqual(Instant instante);
}
