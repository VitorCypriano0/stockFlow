package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Movimentacao;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoRepository {

    List<Movimentacao> findAllByEmpresa_IdOrderByDataHoraDesc(Long empresaId);

    Optional<Movimentacao> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Movimentacao> findAllByEmpresaIsNull();

    Movimentacao save(Movimentacao movimentacao);

    List<Movimentacao> saveAll(List<Movimentacao> movimentacoes);
}
