package br.com.stockflow.services;

import br.com.stockflow.dto.ItemRequest;
import br.com.stockflow.dto.ItemResponse;
import br.com.stockflow.entities.Fornecedor;
import br.com.stockflow.entities.Item;
import br.com.stockflow.exceptions.ConflitoException;
import br.com.stockflow.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.exceptions.RegraNegocioException;
import br.com.stockflow.repository.FornecedorRepository;
import br.com.stockflow.repository.ItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final FornecedorRepository fornecedorRepository;

    public ItemService(ItemRepository itemRepository, FornecedorRepository fornecedorRepository) {
        this.itemRepository = itemRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listar() {
        return itemRepository.findAllByOrderByNomeAsc().stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    public ItemResponse criar(ItemRequest requisicao) {
        String codigo = requisicao.codigo().trim().toUpperCase();
        if (itemRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ConflitoException("Já existe um item com este código.");
        }

        Item item = new Item();
        copiarDados(item, requisicao, codigo);
        return paraResposta(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse atualizar(Long id, ItemRequest requisicao) {
        Item item = obter(id);
        String codigo = requisicao.codigo().trim().toUpperCase();
        if (itemRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new ConflitoException("Outro item já usa este código.");
        }

        copiarDados(item, requisicao, codigo);
        return paraResposta(itemRepository.save(item));
    }

    @Transactional
    public void excluir(Long id) {
        Item item = obter(id);
        item.setAtivo(false);
        itemRepository.save(item);
    }

    private void copiarDados(Item destino, ItemRequest origem, String codigo) {
        Fornecedor fornecedor = fornecedorRepository.findById(origem.fornecedorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado."));
        if (!fornecedor.isAtivo()) {
            throw new RegraNegocioException("Selecione um fornecedor ativo.");
        }

        destino.setCodigo(codigo);
        destino.setNome(origem.nome().trim());
        destino.setDescricao(limpar(origem.descricao()));
        destino.setUnidade(origem.unidade().trim().toUpperCase());
        destino.setEstoqueMinimo(origem.estoqueMinimo());
        destino.setFornecedor(fornecedor);
    }

    private Item obter(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
    }

    private ItemResponse paraResposta(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getCodigo(),
                item.getNome(),
                item.getDescricao(),
                item.getUnidade(),
                item.getQuantidadeEstoque(),
                item.getEstoqueMinimo(),
                item.getFornecedor().getId(),
                item.getFornecedor().getNome(),
                item.isAtivo()
        );
    }

    private String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
