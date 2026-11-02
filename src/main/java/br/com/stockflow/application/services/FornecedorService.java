package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.FornecedorRequest;
import br.com.stockflow.application.dto.FornecedorResponse;
import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.domain.entities.Fornecedor;
import br.com.stockflow.application.exceptions.ConflitoException;
import br.com.stockflow.application.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.domain.repository.FornecedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;
    private final EmpresaAtualService empresaAtualService;

    public FornecedorService(FornecedorRepository repository, EmpresaAtualService empresaAtualService) {
        this.repository = repository;
        this.empresaAtualService = empresaAtualService;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return repository.findAllByEmpresa_IdOrderByNomeAsc(empresaId).stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest requisicao) {
        Empresa empresa = empresaAtualService.obterEmpresaAtual();
        String cnpj = normalizarDocumento(requisicao.cnpj());
        validarDocumentoUnico(cnpj, null);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setEmpresa(empresa);
        copiarDados(fornecedor, requisicao, cnpj);
        return paraResposta(repository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest requisicao) {
        Fornecedor fornecedor = obter(id);
        String cnpj = normalizarDocumento(requisicao.cnpj());
        validarDocumentoUnico(cnpj, id);

        copiarDados(fornecedor, requisicao, cnpj);
        return paraResposta(repository.save(fornecedor));
    }

    @Transactional
    public void excluir(Long id) {
        Fornecedor fornecedor = obter(id);
        fornecedor.setAtivo(false);
        repository.save(fornecedor);
    }

    private void validarDocumentoUnico(String cnpj, Long idAtual) {
        if (cnpj == null) {
            return;
        }
        boolean duplicado = idAtual == null
                ? repository.existsByCnpjIgnoreCase(cnpj)
                : repository.existsByCnpjIgnoreCaseAndIdNot(cnpj, idAtual);
        if (duplicado) {
            throw new ConflitoException("Já existe um fornecedor com este CNPJ.");
        }
    }

    private Fornecedor obter(Long id) {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return repository.findByIdAndEmpresa_Id(id, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado."));
    }

    private void copiarDados(Fornecedor destino, FornecedorRequest origem, String cnpj) {
        destino.setNome(origem.nome().trim());
        destino.setCnpj(cnpj);
        destino.setContato(limpar(origem.contato()));
        destino.setTelefone(limpar(origem.telefone()));
        destino.setEmail(limpar(origem.email()));
    }

    private FornecedorResponse paraResposta(Fornecedor fornecedor) {
        return new FornecedorResponse(
                fornecedor.getId(),
                fornecedor.getNome(),
                fornecedor.getCnpj(),
                fornecedor.getContato(),
                fornecedor.getTelefone(),
                fornecedor.getEmail(),
                fornecedor.isAtivo()
        );
    }

    private String normalizarDocumento(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return null;
        }
        return cnpj.replaceAll("\\D", "");
    }

    private String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
