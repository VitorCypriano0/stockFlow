package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.Movimentacao;
import br.com.stockflow.domain.repository.MovimentacaoRepository;
import br.com.stockflow.infra.persistence.jpa.MovimentacaoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MovimentacaoRepositoryAdapter implements MovimentacaoRepository {

    private final MovimentacaoJpaRepository jpaRepository;

    public MovimentacaoRepositoryAdapter(MovimentacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Movimentacao> findAllByEmpresa_IdOrderByDataHoraDesc(Long empresaId) {
        return jpaRepository.findAllByEmpresa_IdOrderByDataHoraDesc(empresaId);
    }

    @Override
    public Optional<Movimentacao> findByIdAndEmpresa_Id(Long id, Long empresaId) {
        return jpaRepository.findByIdAndEmpresa_Id(id, empresaId);
    }

    @Override
    public List<Movimentacao> findAllByEmpresaIsNull() {
        return jpaRepository.findAllByEmpresaIsNull();
    }

    @Override
    public Movimentacao save(Movimentacao movimentacao) {
        return jpaRepository.save(movimentacao);
    }

    @Override
    public List<Movimentacao> saveAll(List<Movimentacao> movimentacoes) {
        return jpaRepository.saveAll(movimentacoes);
    }
}
