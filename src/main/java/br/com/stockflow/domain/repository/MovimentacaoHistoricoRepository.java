package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.MovimentacaoHistorico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoHistoricoRepository extends JpaRepository<MovimentacaoHistorico, Long> {

    List<MovimentacaoHistorico> findAllByEmpresa_IdOrderByDataHoraDesc(Long empresaId);

    List<MovimentacaoHistorico> findAllByEmpresaIsNull();
}
