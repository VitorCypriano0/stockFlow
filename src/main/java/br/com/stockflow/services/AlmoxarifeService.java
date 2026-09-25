package br.com.stockflow.services;

import br.com.stockflow.dto.AlmoxarifeRequest;
import br.com.stockflow.dto.AlmoxarifeResponse;
import br.com.stockflow.entities.Almoxarife;
import br.com.stockflow.exceptions.ConflitoException;
import br.com.stockflow.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.repository.AlmoxarifeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlmoxarifeService {

    private final AlmoxarifeRepository repository;

    public AlmoxarifeService(AlmoxarifeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AlmoxarifeResponse> listar() {
        return repository.findAllByOrderByNomeAsc().stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlmoxarifeResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    public AlmoxarifeResponse criar(AlmoxarifeRequest requisicao) {
        String cpf = limpar(requisicao.cpf());
        String email = limpar(requisicao.email()).toLowerCase();
        if (repository.existsByCpfIgnoreCase(cpf)) {
            throw new ConflitoException("Já existe um almoxarife com este CPF.");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe um almoxarife com este e-mail.");
        }

        Almoxarife almoxarife = new Almoxarife();
        copiarDados(almoxarife, requisicao, cpf, email);
        return paraResposta(repository.save(almoxarife));
    }

    @Transactional
    public AlmoxarifeResponse atualizar(Long id, AlmoxarifeRequest requisicao) {
        Almoxarife almoxarife = obter(id);
        String cpf = limpar(requisicao.cpf());
        String email = limpar(requisicao.email()).toLowerCase();

        if (repository.existsByCpfIgnoreCaseAndIdNot(cpf, id)) {
            throw new ConflitoException("Outro almoxarife já usa este CPF.");
        }
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflitoException("Outro almoxarife já usa este e-mail.");
        }

        copiarDados(almoxarife, requisicao, cpf, email);
        return paraResposta(repository.save(almoxarife));
    }

    @Transactional
    public void excluir(Long id) {
        Almoxarife almoxarife = obter(id);
        almoxarife.setAtivo(false);
        repository.save(almoxarife);
    }

    private Almoxarife obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarife não encontrado."));
    }

    private void copiarDados(Almoxarife destino, AlmoxarifeRequest origem, String cpf, String email) {
        destino.setNome(limpar(origem.nome()));
        destino.setCpf(cpf);
        destino.setEmail(email);
        destino.setTelefone(limpar(origem.telefone()));
    }

    private AlmoxarifeResponse paraResposta(Almoxarife almoxarife) {
        return new AlmoxarifeResponse(
                almoxarife.getId(),
                almoxarife.getNome(),
                almoxarife.getCpf(),
                almoxarife.getEmail(),
                almoxarife.getTelefone(),
                almoxarife.isAtivo()
        );
    }

    private String limpar(String texto) {
        return texto == null ? null : texto.trim();
    }
}
