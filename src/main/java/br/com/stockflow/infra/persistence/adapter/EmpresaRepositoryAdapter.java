package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.domain.repository.EmpresaRepository;
import br.com.stockflow.infra.persistence.jpa.EmpresaJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class EmpresaRepositoryAdapter implements EmpresaRepository {

    private final EmpresaJpaRepository jpaRepository;

    public EmpresaRepositoryAdapter(EmpresaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Empresa> findFirstByNomeIgnoreCase(String nome) {
        return jpaRepository.findFirstByNomeIgnoreCase(nome);
    }

    @Override
    public Empresa save(Empresa empresa) {
        return jpaRepository.save(empresa);
    }
}
