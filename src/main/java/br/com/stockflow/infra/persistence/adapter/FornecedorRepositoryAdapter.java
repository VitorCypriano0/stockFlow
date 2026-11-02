package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.Fornecedor;
import br.com.stockflow.domain.repository.FornecedorRepository;
import br.com.stockflow.infra.persistence.jpa.FornecedorJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class FornecedorRepositoryAdapter implements FornecedorRepository {

    private final FornecedorJpaRepository jpaRepository;

    public FornecedorRepositoryAdapter(FornecedorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByCnpjIgnoreCase(String cnpj) {
        return jpaRepository.existsByCnpjIgnoreCase(cnpj);
    }

    @Override
    public boolean existsByCnpjIgnoreCaseAndIdNot(String cnpj, Long id) {
        return jpaRepository.existsByCnpjIgnoreCaseAndIdNot(cnpj, id);
    }

    @Override
    public List<Fornecedor> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId) {
        return jpaRepository.findAllByEmpresa_IdOrderByNomeAsc(empresaId);
    }

    @Override
    public Optional<Fornecedor> findByIdAndEmpresa_Id(Long id, Long empresaId) {
        return jpaRepository.findByIdAndEmpresa_Id(id, empresaId);
    }

    @Override
    public List<Fornecedor> findAllByEmpresaIsNull() {
        return jpaRepository.findAllByEmpresaIsNull();
    }

    @Override
    public Fornecedor save(Fornecedor fornecedor) {
        return jpaRepository.save(fornecedor);
    }

    @Override
    public List<Fornecedor> saveAll(List<Fornecedor> fornecedores) {
        return jpaRepository.saveAll(fornecedores);
    }
}
