package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.HistoricoResponse;
import br.com.stockflow.application.dto.MovimentacaoRequest;
import br.com.stockflow.application.dto.MovimentacaoResponse;
import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.domain.entities.Item;
import br.com.stockflow.domain.entities.Movimentacao;
import br.com.stockflow.domain.entities.MovimentacaoHistorico;
import br.com.stockflow.domain.entities.TipoMovimentacao;
import br.com.stockflow.application.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.application.exceptions.RegraNegocioException;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import br.com.stockflow.domain.repository.ItemRepository;
import br.com.stockflow.domain.repository.MovimentacaoHistoricoRepository;
import br.com.stockflow.domain.repository.MovimentacaoRepository;
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
    private final EmpresaAtualService empresaAtualService;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            MovimentacaoHistoricoRepository historicoRepository,
            ItemRepository itemRepository,
            AlmoxarifeRepository almoxarifeRepository,
            EmpresaAtualService empresaAtualService
    ) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.historicoRepository = historicoRepository;
        this.itemRepository = itemRepository;
        this.almoxarifeRepository = almoxarifeRepository;
        this.empresaAtualService = empresaAtualService;
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listar() {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return movimentacaoRepository.findAllByEmpresa_IdOrderByDataHoraDesc(empresaId).stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponse buscar(Long id) {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return paraResposta(obter(id, empresaId));
    }

    @Transactional
    public MovimentacaoResponse criar(MovimentacaoRequest requisicao, Long almoxarifeAutenticadoId) {
        Empresa empresa = empresaAtualService.obterEmpresaAtual();
        Long empresaId = empresa.getId();
        Item item = obterItemAtivo(requisicao.itemId(), empresaId);
        Almoxarife almoxarife = obterAlmoxarifeAtivo(almoxarifeAutenticadoId, empresaId);
        aplicar(item, requisicao.tipo(), requisicao.quantidade());
        itemRepository.save(item);

        Movimentacao movimentacao = new Movimentacao();
        preencher(movimentacao, item, almoxarife, empresa, requisicao);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setAtiva(true);
        movimentacao = movimentacaoRepository.save(movimentacao);
        registrarHistorico(movimentacao, "CRIADA", almoxarife);
        return paraResposta(movimentacao);
    }

    @Transactional
    public MovimentacaoResponse atualizar(
            Long id,
            MovimentacaoRequest requisicao,
            Long almoxarifeAutenticadoId
    ) {
        Empresa empresa = empresaAtualService.obterEmpresaAtual();
        Long empresaId = empresa.getId();
        Movimentacao movimentacao = obter(id, empresaId);
        if (!movimentacao.isAtiva()) {
            throw new RegraNegocioException("Uma movimentação cancelada não pode ser alterada.");
        }

        reverter(movimentacao);
        Item item = obterItemAtivo(requisicao.itemId(), empresaId);
        Almoxarife almoxarife = obterAlmoxarifeAtivo(almoxarifeAutenticadoId, empresaId);
        aplicar(item, requisicao.tipo(), requisicao.quantidade());
        itemRepository.save(item);

        preencher(movimentacao, item, almoxarife, empresa, requisicao);
        movimentacao = movimentacaoRepository.save(movimentacao);
        registrarHistorico(movimentacao, "ATUALIZADA", almoxarife);
        return paraResposta(movimentacao);
    }

    @Transactional
    public void excluir(Long id, Long almoxarifeAutenticadoId) {
        Empresa empresa = empresaAtualService.obterEmpresaAtual();
        Long empresaId = empresa.getId();
        Movimentacao movimentacao = obter(id, empresaId);
        if (!movimentacao.isAtiva()) {
            throw new RegraNegocioException("Esta movimentação já foi cancelada.");
        }

        Almoxarife almoxarife = obterAlmoxarifeAtivo(almoxarifeAutenticadoId, empresaId);
        reverter(movimentacao);
        movimentacao.setAtiva(false);
        movimentacaoRepository.save(movimentacao);
        registrarHistorico(movimentacao, "CANCELADA", almoxarife);
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponse> listarHistorico() {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return historicoRepository.findAllByEmpresa_IdOrderByDataHoraDesc(empresaId).stream()
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
            Empresa empresa,
            MovimentacaoRequest requisicao
    ) {
        destino.setItem(item);
        destino.setAlmoxarife(almoxarife);
        destino.setEmpresa(empresa);
        destino.setTipo(requisicao.tipo());
        destino.setQuantidade(requisicao.quantidade());
        destino.setObservacao(limpar(requisicao.observacao()));
    }

    private void registrarHistorico(Movimentacao movimentacao, String acao, Almoxarife almoxarife) {
        MovimentacaoHistorico registro = new MovimentacaoHistorico();
        registro.setMovimentacaoId(movimentacao.getId());
        registro.setAcao(acao);
        registro.setTipo(movimentacao.getTipo().name());
        registro.setItemNome(movimentacao.getItem().getNome());
        registro.setQuantidade(movimentacao.getQuantidade());
        registro.setAlmoxarifeId(almoxarife.getId());
        registro.setAlmoxarifeNome(almoxarife.getNome());
        registro.setEmpresa(movimentacao.getEmpresa());
        registro.setDataHora(LocalDateTime.now());
        registro.setObservacao(movimentacao.getObservacao());
        historicoRepository.save(registro);
    }

    private Movimentacao obter(Long id, Long empresaId) {
        return movimentacaoRepository.findByIdAndEmpresa_Id(id, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação não encontrada."));
    }

    private Item obterItemAtivo(Long id, Long empresaId) {
        Item item = itemRepository.findByIdAndEmpresa_Id(id, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
        if (!item.isAtivo()) {
            throw new RegraNegocioException("Selecione um item ativo.");
        }
        return item;
    }

    private Almoxarife obterAlmoxarifeAtivo(Long id, Long empresaId) {
        Almoxarife almoxarife = almoxarifeRepository.findByIdAndEmpresa_Id(id, empresaId)
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
