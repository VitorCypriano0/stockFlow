package br.com.stockflow.repository;

import br.com.stockflow.entities.Fornecedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    boolean existsByCnpjIgnoreCase(String cnpj);

    boolean existsByCnpjIgnoreCaseAndIdNot(String cnpj, Long id);

    List<Fornecedor> findAllByOrderByNomeAsc();
}
