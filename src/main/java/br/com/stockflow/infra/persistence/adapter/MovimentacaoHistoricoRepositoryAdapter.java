package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.MovimentacaoHistorico;
import br.com.stockflow.domain.repository.MovimentacaoHistoricoRepository;
import br.com.stockflow.infra.persistence.jpa.MovimentacaoHistoricoJpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MovimentacaoHistoricoRepositoryAdapter implements MovimentacaoHistoricoRepository {

    private final MovimentacaoHistoricoJpaRepository jpaRepository;

    public MovimentacaoHistoricoRepositoryAdapter(MovimentacaoHistoricoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<MovimentacaoHistorico> findAllByEmpresa_IdOrderByDataHoraDesc(Long empresaId) {
        return jpaRepository.findAllByEmpresa_IdOrderByDataHoraDesc(empresaId);
    }

    @Override
    public List<MovimentacaoHistorico> findAllByEmpresaIsNull() {
        return jpaRepository.findAllByEmpresaIsNull();
    }

    @Override
    public MovimentacaoHistorico save(MovimentacaoHistorico historico) {
        return jpaRepository.save(historico);
    }

    @Override
    public List<MovimentacaoHistorico> saveAll(List<MovimentacaoHistorico> historicos) {
        return jpaRepository.saveAll(historicos);
    }
}
