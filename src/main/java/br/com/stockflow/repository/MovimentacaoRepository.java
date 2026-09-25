package br.com.stockflow.repository;

import br.com.stockflow.entities.Movimentacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByOrderByDataHoraDesc();
}
