package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.TokenRevogado;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRevogadoRepository extends JpaRepository<TokenRevogado, String> {

    long deleteByExpiraEmLessThanEqual(Instant instante);
}
