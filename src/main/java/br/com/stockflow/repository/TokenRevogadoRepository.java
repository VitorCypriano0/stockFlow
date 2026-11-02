package br.com.stockflow.repository;

import br.com.stockflow.entities.TokenRevogado;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRevogadoRepository extends JpaRepository<TokenRevogado, String> {

    long deleteByExpiraEmLessThanEqual(Instant instante);
}
