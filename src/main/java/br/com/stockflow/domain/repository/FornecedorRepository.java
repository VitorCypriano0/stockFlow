package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Fornecedor;
import java.util.List;
import java.util.Optional;

public interface FornecedorRepository {

    boolean existsByCnpjIgnoreCase(String cnpj);

    boolean existsByCnpjIgnoreCaseAndIdNot(String cnpj, Long id);

    List<Fornecedor> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId);

    Optional<Fornecedor> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Fornecedor> findAllByEmpresaIsNull();

    Fornecedor save(Fornecedor fornecedor);

    List<Fornecedor> saveAll(List<Fornecedor> fornecedores);
}
