package br.com.stockflow.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "almoxarifes")
@Getter
@Setter
@NoArgsConstructor
public class Almoxarife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    // A senha nunca é armazenada em texto puro; o campo guarda somente o hash BCrypt.
    // Pode ficar vazio em registros antigos até o primeiro cadastro/autenticação.
    @Column(name = "senha_hash")
    private String senhaHash;

    private String telefone;

    @Column(nullable = false)
    private boolean ativo = true;
}
