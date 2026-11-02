package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.MovimentacaoHistorico;
import java.util.List;

public interface MovimentacaoHistoricoRepository {

    List<MovimentacaoHistorico> findAllByEmpresa_IdOrderByDataHoraDesc(Long empresaId);

    List<MovimentacaoHistorico> findAllByEmpresaIsNull();

    MovimentacaoHistorico save(MovimentacaoHistorico historico);

    List<MovimentacaoHistorico> saveAll(List<MovimentacaoHistorico> historicos);
}
