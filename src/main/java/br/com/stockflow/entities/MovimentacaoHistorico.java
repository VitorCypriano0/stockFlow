package br.com.stockflow.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "historico_movimentacoes")
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long movimentacaoId;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String itemNome;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false)
    private Long almoxarifeId;

    @Column(nullable = false)
    private String almoxarifeNome;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 1000)
    private String observacao;
}
