package br.com.stockflow.domain.repository;

import br.com.stockflow.domain.entities.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    List<Item> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId);

    Optional<Item> findByIdAndEmpresa_Id(Long id, Long empresaId);

    List<Item> findAllByEmpresaIsNull();

    Item save(Item item);

    List<Item> saveAll(List<Item> itens);
}
