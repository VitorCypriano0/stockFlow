package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import br.com.stockflow.infra.persistence.jpa.AlmoxarifeJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AlmoxarifeRepositoryAdapter implements AlmoxarifeRepository {

    private final AlmoxarifeJpaRepository jpaRepository;

    public AlmoxarifeRepositoryAdapter(AlmoxarifeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Almoxarife> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByCpfIgnoreCase(String cpf) {
        return jpaRepository.existsByCpfIgnoreCase(cpf);
    }

    @Override
    public boolean existsByCpfIgnoreCaseAndIdNot(String cpf, Long id) {
        return jpaRepository.existsByCpfIgnoreCaseAndIdNot(cpf, id);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id) {
        return jpaRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    public boolean existsByRole(String role) {
        return jpaRepository.existsByRole(role);
    }

    @Override
    public boolean existsByRoleAndEmpresa_Id(String role, Long empresaId) {
        return jpaRepository.existsByRoleAndEmpresa_Id(role, empresaId);
    }

    @Override
    public Optional<Almoxarife> findByEmailIgnoreCase(String email) {
        return jpaRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public List<Almoxarife> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId) {
        return jpaRepository.findAllByEmpresa_IdOrderByNomeAsc(empresaId);
    }

    @Override
    public Optional<Almoxarife> findByIdAndEmpresa_Id(Long id, Long empresaId) {
        return jpaRepository.findByIdAndEmpresa_Id(id, empresaId);
    }

    @Override
    public List<Almoxarife> findAllByEmpresaIsNull() {
        return jpaRepository.findAllByEmpresaIsNull();
    }

    @Override
    public Almoxarife save(Almoxarife almoxarife) {
        return jpaRepository.save(almoxarife);
    }

    @Override
    public List<Almoxarife> saveAll(List<Almoxarife> almoxarifes) {
        return jpaRepository.saveAll(almoxarifes);
    }
}
