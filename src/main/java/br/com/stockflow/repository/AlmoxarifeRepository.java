package br.com.stockflow.repository;

import br.com.stockflow.entities.Almoxarife;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmoxarifeRepository extends JpaRepository<Almoxarife, Long> {

    boolean existsByCpfIgnoreCase(String cpf);

    boolean existsByCpfIgnoreCaseAndIdNot(String cpf, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Almoxarife> findAllByOrderByNomeAsc();
}
