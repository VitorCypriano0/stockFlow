package br.com.stockflow.repository;

import br.com.stockflow.entities.MovimentacaoHistorico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoHistoricoRepository extends JpaRepository<MovimentacaoHistorico, Long> {

    List<MovimentacaoHistorico> findAllByOrderByDataHoraDesc();
}
