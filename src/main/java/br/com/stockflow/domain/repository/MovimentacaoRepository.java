package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Movimentacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByOrderByDataHoraDesc();
}
