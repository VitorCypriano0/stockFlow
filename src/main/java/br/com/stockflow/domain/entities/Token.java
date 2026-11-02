package br.com.stockflow.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 2048)
    private String token;

    @Column(name = "data_cadastro", nullable = false)
    private Instant dataCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almoxarife_id", referencedColumnName = "id", nullable = false)
    private Almoxarife almoxarife;

    protected Token() {
    }

    public Token(String token, Almoxarife almoxarife) {
        this.token = token;
        this.almoxarife = almoxarife;
        this.dataCadastro = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Almoxarife getAlmoxarife() {
        return almoxarife;
    }
}
