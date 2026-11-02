package br.com.stockflow.infra.persistence.jpa;

import br.com.stockflow.domain.entities.Movimentacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoJpaRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByEmpresa_IdOrderByDataHoraDesc(Long empresaId);

    Optional<Movimentacao> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Movimentacao> findAllByEmpresaIsNull();
}
