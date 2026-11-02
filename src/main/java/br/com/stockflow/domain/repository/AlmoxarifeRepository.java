package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Almoxarife;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmoxarifeRepository extends JpaRepository<Almoxarife, Long> {

    boolean existsByCpfIgnoreCase(String cpf);

    boolean existsByCpfIgnoreCaseAndIdNot(String cpf, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<Almoxarife> findByEmailIgnoreCase(String email);

    List<Almoxarife> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId);

    Optional<Almoxarife> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Almoxarife> findAllByEmpresaIsNull();
}
