package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Token;
import java.util.Optional;

public interface TokenRepository {

    Optional<Token> findTokenByToken(String token);

    Token save(Token token);

    void deleteById(Long id);
}
