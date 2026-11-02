package br.com.stockflow.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(
        name = "tokens_revogados",
        indexes = @Index(name = "idx_tokens_revogados_expira_em", columnList = "expira_em")
)
public class TokenRevogado {

    @Id
    @Column(name = "jti", nullable = false, length = 36)
    private String id;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    protected TokenRevogado() {
    }

    public TokenRevogado(String id, Instant expiraEm) {
        this.id = id;
        this.expiraEm = expiraEm;
    }
}
