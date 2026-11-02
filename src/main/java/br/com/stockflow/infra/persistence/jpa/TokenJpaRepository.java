package br.com.stockflow.infra.persistence.jpa;

import br.com.stockflow.domain.entities.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {

    Optional<Token> findTokenByToken(String token);
}
