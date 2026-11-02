package br.com.stockflow.infra.persistence.adapter;

import br.com.stockflow.domain.entities.Item;
import br.com.stockflow.domain.repository.ItemRepository;
import br.com.stockflow.infra.persistence.jpa.ItemJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepositoryAdapter implements ItemRepository {

    private final ItemJpaRepository jpaRepository;

    public ItemRepositoryAdapter(ItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByCodigoIgnoreCase(String codigo) {
        return jpaRepository.existsByCodigoIgnoreCase(codigo);
    }

    @Override
    public boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id) {
        return jpaRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id);
    }

    @Override
    public List<Item> findAllByEmpresa_IdOrderByNomeAsc(Long empresaId) {
        return jpaRepository.findAllByEmpresa_IdOrderByNomeAsc(empresaId);
    }

    @Override
    public Optional<Item> findByIdAndEmpresa_Id(Long id, Long empresaId) {
        return jpaRepository.findByIdAndEmpresa_Id(id, empresaId);
    }

    @Override
    public List<Item> findAllByEmpresaIsNull() {
        return jpaRepository.findAllByEmpresaIsNull();
    }

    @Override
    public Item save(Item item) {
        return jpaRepository.save(item);
    }

    @Override
    public List<Item> saveAll(List<Item> itens) {
        return jpaRepository.saveAll(itens);
    }
}
