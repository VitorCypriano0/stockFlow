package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    List<Item> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId);

    Optional<Item> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Item> findAllByEmpresaIsNull();
}
