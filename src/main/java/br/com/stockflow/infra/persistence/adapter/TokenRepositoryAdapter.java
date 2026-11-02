package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.Token;
import br.com.stockflow.domain.repository.TokenRepository;
import br.com.stockflow.infra.persistence.jpa.TokenJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepositoryAdapter implements TokenRepository {

    private final TokenJpaRepository jpaRepository;

    public TokenRepositoryAdapter(TokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Token> findTokenByToken(String token) {
        return jpaRepository.findTokenByToken(token);
    }

    @Override
    public Token save(Token token) {
        return jpaRepository.save(token);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
