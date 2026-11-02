package br.com.stockflow.infra.migration;

import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import br.com.stockflow.domain.repository.EmpresaRepository;
import br.com.stockflow.domain.repository.FornecedorRepository;
import br.com.stockflow.domain.repository.ItemRepository;
import br.com.stockflow.domain.repository.MovimentacaoHistoricoRepository;
import br.com.stockflow.domain.repository.MovimentacaoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EmpresaLegadaMigration implements ApplicationRunner {

    private static final String NOME_EMPRESA_LEGADA = "Empresa legada StockFlow";

    private final EmpresaRepository empresaRepository;
    private final AlmoxarifeRepository almoxarifeRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ItemRepository itemRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final MovimentacaoHistoricoRepository historicoRepository;

    public EmpresaLegadaMigration(
            EmpresaRepository empresaRepository,
            AlmoxarifeRepository almoxarifeRepository,
            FornecedorRepository fornecedorRepository,
            ItemRepository itemRepository,
            MovimentacaoRepository movimentacaoRepository,
            MovimentacaoHistoricoRepository historicoRepository
    ) {
        this.empresaRepository = empresaRepository;
        this.almoxarifeRepository = almoxarifeRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.itemRepository = itemRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var almoxarifes = almoxarifeRepository.findAllByEmpresaIsNull();
        var fornecedores = fornecedorRepository.findAllByEmpresaIsNull();
        var itens = itemRepository.findAllByEmpresaIsNull();
        var movimentacoes = movimentacaoRepository.findAllByEmpresaIsNull();
        var historicos = historicoRepository.findAllByEmpresaIsNull();

        if (almoxarifes.isEmpty() && fornecedores.isEmpty() && itens.isEmpty()
                && movimentacoes.isEmpty() && historicos.isEmpty()) {
            return;
        }

        Empresa empresaLegada = empresaRepository.findFirstByNomeIgnoreCase(NOME_EMPRESA_LEGADA)
                .orElseGet(() -> empresaRepository.save(new Empresa(NOME_EMPRESA_LEGADA)));

        almoxarifes.forEach(almoxarife -> almoxarife.setEmpresa(empresaLegada));
        fornecedores.forEach(fornecedor -> fornecedor.setEmpresa(empresaLegada));
        itens.forEach(item -> item.setEmpresa(empresaLegada));
        movimentacoes.forEach(movimentacao -> movimentacao.setEmpresa(empresaLegada));
        historicos.forEach(historico -> historico.setEmpresa(empresaLegada));

        almoxarifeRepository.saveAll(almoxarifes);
        fornecedorRepository.saveAll(fornecedores);
        itemRepository.saveAll(itens);
        movimentacaoRepository.saveAll(movimentacoes);
        historicoRepository.saveAll(historicos);
    }
}
