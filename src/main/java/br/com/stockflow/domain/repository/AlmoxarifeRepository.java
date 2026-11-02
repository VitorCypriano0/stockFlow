package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Almoxarife;
import java.util.List;
import java.util.Optional;

public interface AlmoxarifeRepository {

    Optional<Almoxarife> findById(Long id);

    boolean existsByCpfIgnoreCase(String cpf);

    boolean existsByCpfIgnoreCaseAndIdNot(String cpf, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByRole(String role);

    boolean existsByRoleAndEmpresa_Id(String role, Long empresaId);

    Optional<Almoxarife> findByEmailIgnoreCase(String email);

    List<Almoxarife> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId);

    Optional<Almoxarife> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Almoxarife> findAllByEmpresaIsNull();

    Almoxarife save(Almoxarife almoxarife);

    List<Almoxarife> saveAll(List<Almoxarife> almoxarifes);
}
