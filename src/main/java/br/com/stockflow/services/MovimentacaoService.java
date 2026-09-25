package br.com.stockflow.services;

import br.com.stockflow.dto.HistoricoResponse;
import br.com.stockflow.dto.MovimentacaoRequest;
import br.com.stockflow.dto.MovimentacaoResponse;
import br.com.stockflow.entities.Almoxarife;
import br.com.stockflow.entities.Item;
import br.com.stockflow.entities.Movimentacao;
import br.com.stockflow.entities.MovimentacaoHistorico;
import br.com.stockflow.entities.TipoMovimentacao;
import br.com.stockflow.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.exceptions.RegraNegocioException;
import br.com.stockflow.repository.AlmoxarifeRepository;
import br.com.stockflow.repository.ItemRepository;
import br.com.stockflow.repository.MovimentacaoHistoricoRepository;
import br.com.stockflow.repository.MovimentacaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final MovimentacaoHistoricoRepository historicoRepository;
    private final ItemRepository itemRepository;
    private final AlmoxarifeRepository almoxarifeRepository;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            MovimentacaoHistoricoRepository historicoRepository,
            ItemRepository itemRepository,
            AlmoxarifeRepository almoxarifeRepository
    ) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.historicoRepository = historicoRepository;
        this.itemRepository = itemRepository;
        this.almoxarifeRepository = almoxarifeRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listar() {
        return movimentacaoRepository.findAllByOrderByDataHoraDesc().stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    public MovimentacaoResponse criar(MovimentacaoRequest requisicao) {
        Item item = obterItemAtivo(requisicao.itemId());
        Almoxarife almoxarife = obterAlmoxarifeAtivo(requisicao.almoxarifeId());
        aplicar(item, requisicao.tipo(), requisicao.quantidade());
        itemRepository.save(item);

        Movimentacao movimentacao = new Movimentacao();
        preencher(movimentacao, item, almoxarife, requisicao);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setAtiva(true);
        movimentacao = movimentacaoRepository.save(movimentacao);
        registrarHistorico(movimentacao, "CRIADA");
        return paraResposta(movimentacao);
    }

    @Transactional
    public MovimentacaoResponse atualizar(Long id, MovimentacaoRequest requisicao) {
        Movimentacao movimentacao = obter(id);
        if (!movimentacao.isAtiva()) {
            throw new RegraNegocioException("Uma movimentação cancelada não pode ser alterada.");
        }

        reverter(movimentacao);
        Item item = obterItemAtivo(requisicao.itemId());
        Almoxarife almoxarife = obterAlmoxarifeAtivo(requisicao.almoxarifeId());
        aplicar(item, requisicao.tipo(), requisicao.quantidade());
        itemRepository.save(item);

        preencher(movimentacao, item, almoxarife, requisicao);
        movimentacao = movimentacaoRepository.save(movimentacao);
        registrarHistorico(movimentacao, "ATUALIZADA");
        return paraResposta(movimentacao);
    }

    @Transactional
    public void excluir(Long id) {
        Movimentacao movimentacao = obter(id);
        if (!movimentacao.isAtiva()) {
            throw new RegraNegocioException("Esta movimentação já foi cancelada.");
        }

        reverter(movimentacao);
        movimentacao.setAtiva(false);
        movimentacaoRepository.save(movimentacao);
        registrarHistorico(movimentacao, "CANCELADA");
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponse> listarHistorico() {
        return historicoRepository.findAllByOrderByDataHoraDesc().stream()
                .map(this::paraResposta)
                .toList();
    }

    private void aplicar(Item item, TipoMovimentacao tipo, int quantidade) {
        if (tipo == TipoMovimentacao.SAIDA && quantidade > item.getQuantidadeEstoque()) {
            throw new RegraNegocioException(
                    "A saída solicitada é maior que o saldo disponível (" + item.getQuantidadeEstoque() + ")."
            );
        }

        int ajuste = tipo == TipoMovimentacao.ENTRADA ? quantidade : -quantidade;
        item.setQuantidadeEstoque(item.getQuantidadeEstoque() + ajuste);
    }

    private void reverter(Movimentacao movimentacao) {
        Item item = movimentacao.getItem();
        int ajuste = movimentacao.getTipo() == TipoMovimentacao.ENTRADA
                ? -movimentacao.getQuantidade()
                : movimentacao.getQuantidade();
        int saldoRevertido = item.getQuantidadeEstoque() + ajuste;
        if (saldoRevertido < 0) {
            throw new RegraNegocioException(
                    "Não é possível cancelar esta entrada: parte do saldo já foi retirada."
            );
        }
        item.setQuantidadeEstoque(saldoRevertido);
        itemRepository.save(item);
    }

    private void preencher(
            Movimentacao destino,
            Item item,
            Almoxarife almoxarife,
            MovimentacaoRequest requisicao
    ) {
        destino.setItem(item);
        destino.setAlmoxarife(almoxarife);
        destino.setTipo(requisicao.tipo());
        destino.setQuantidade(requisicao.quantidade());
        destino.setObservacao(limpar(requisicao.observacao()));
    }

    private void registrarHistorico(Movimentacao movimentacao, String acao) {
        MovimentacaoHistorico registro = new MovimentacaoHistorico();
        registro.setMovimentacaoId(movimentacao.getId());
        registro.setAcao(acao);
        registro.setTipo(movimentacao.getTipo().name());
        registro.setItemNome(movimentacao.getItem().getNome());
        registro.setQuantidade(movimentacao.getQuantidade());
        registro.setAlmoxarifeId(movimentacao.getAlmoxarife().getId());
        registro.setAlmoxarifeNome(movimentacao.getAlmoxarife().getNome());
        registro.setDataHora(LocalDateTime.now());
        registro.setObservacao(movimentacao.getObservacao());
        historicoRepository.save(registro);
    }

    private Movimentacao obter(Long id) {
        return movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação não encontrada."));
    }

    private Item obterItemAtivo(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
        if (!item.isAtivo()) {
            throw new RegraNegocioException("Selecione um item ativo.");
        }
        return item;
    }

    private Almoxarife obterAlmoxarifeAtivo(Long id) {
        Almoxarife almoxarife = almoxarifeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarife não encontrado."));
        if (!almoxarife.isAtivo()) {
            throw new RegraNegocioException("Selecione um almoxarife ativo.");
        }
        return almoxarife;
    }

    private MovimentacaoResponse paraResposta(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getItem().getId(),
                movimentacao.getItem().getNome(),
                movimentacao.getAlmoxarife().getId(),
                movimentacao.getAlmoxarife().getNome(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataHora(),
                movimentacao.getObservacao(),
                movimentacao.isAtiva()
        );
    }

    private HistoricoResponse paraResposta(MovimentacaoHistorico registro) {
        return new HistoricoResponse(
                registro.getId(),
                registro.getMovimentacaoId(),
                registro.getAcao(),
                registro.getTipo(),
                registro.getItemNome(),
                registro.getQuantidade(),
                registro.getAlmoxarifeId(),
                registro.getAlmoxarifeNome(),
                registro.getDataHora(),
                registro.getObservacao()
        );
    }

    private String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
