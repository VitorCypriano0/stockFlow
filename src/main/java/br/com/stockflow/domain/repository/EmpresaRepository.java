package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Empresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findFirstByNomeIgnoreCase(String nome);
}
