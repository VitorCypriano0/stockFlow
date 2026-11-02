package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.ItemRequest;
import br.com.stockflow.application.dto.ItemResponse;
import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.domain.entities.Fornecedor;
import br.com.stockflow.domain.entities.Item;
import br.com.stockflow.application.exceptions.ConflitoException;
import br.com.stockflow.application.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.application.exceptions.RegraNegocioException;
import br.com.stockflow.domain.repository.FornecedorRepository;
import br.com.stockflow.domain.repository.ItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EmpresaAtualService empresaAtualService;

    public ItemService(
            ItemRepository itemRepository,
            FornecedorRepository fornecedorRepository,
            EmpresaAtualService empresaAtualService
    ) {
        this.itemRepository = itemRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.empresaAtualService = empresaAtualService;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listar() {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return itemRepository.findAllByEmpresa_IdOrderByNomeAsc(empresaId).stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    public ItemResponse criar(ItemRequest requisicao) {
        Empresa empresa = empresaAtualService.obterEmpresaAtual();
        String codigo = requisicao.codigo().trim().toUpperCase();
        if (itemRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ConflitoException("Já existe um item com este código.");
        }

        Item item = new Item();
        item.setEmpresa(empresa);
        copiarDados(item, requisicao, codigo, empresa.getId());
        return paraResposta(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse atualizar(Long id, ItemRequest requisicao) {
        Item item = obter(id);
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        String codigo = requisicao.codigo().trim().toUpperCase();
        if (itemRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new ConflitoException("Outro item já usa este código.");
        }

        copiarDados(item, requisicao, codigo, empresaId);
        return paraResposta(itemRepository.save(item));
    }

    @Transactional
    public void excluir(Long id) {
        Item item = obter(id);
        item.setAtivo(false);
        itemRepository.save(item);
    }

    private void copiarDados(Item destino, ItemRequest origem, String codigo, Long empresaId) {
        Fornecedor fornecedor = fornecedorRepository.findByIdAndEmpresa_Id(origem.fornecedorId(), empresaId)
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
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return itemRepository.findByIdAndEmpresa_Id(id, empresaId)
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
