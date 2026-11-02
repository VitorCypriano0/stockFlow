package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Empresa;
import java.util.Optional;

public interface EmpresaRepository {

    Optional<Empresa> findFirstByNomeIgnoreCase(String nome);

    Empresa save(Empresa empresa);
}
