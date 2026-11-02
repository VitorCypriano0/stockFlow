package br.com.stockflow.infra.persistence.jpa;

import br.com.stockflow.domain.entities.Empresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaJpaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findFirstByNomeIgnoreCase(String nome);
}
