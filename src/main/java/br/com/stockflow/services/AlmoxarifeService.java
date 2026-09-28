package br.com.stockflow.services;

import br.com.stockflow.dto.AlmoxarifeRequest;
import br.com.stockflow.dto.AlmoxarifeResponse;
import br.com.stockflow.entities.Almoxarife;
import br.com.stockflow.exceptions.ConflitoException;
import br.com.stockflow.exceptions.RegraNegocioException;
import br.com.stockflow.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.repository.AlmoxarifeRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlmoxarifeService {

    private final AlmoxarifeRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AlmoxarifeService(AlmoxarifeRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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
        validarSenha(requisicao.senha(), true);
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
        almoxarife.setSenhaHash(passwordEncoder.encode(requisicao.senha()));
        return paraResposta(repository.save(almoxarife));
    }

    /** Cria uma conta ou permite que um registro antigo escolha a senha pela primeira vez. */
    @Transactional
    public AlmoxarifeResponse registrar(AlmoxarifeRequest requisicao) {
        validarSenha(requisicao.senha(), true);
        String cpf = limpar(requisicao.cpf());
        String email = limpar(requisicao.email()).toLowerCase();

        return repository.findByEmailIgnoreCase(email)
                .map(existente -> ativarRegistroAntigo(existente, cpf, requisicao.senha()))
                .orElseGet(() -> criar(requisicao));
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
        if (requisicao.senha() != null && !requisicao.senha().isBlank()) {
            validarSenha(requisicao.senha(), false);
            almoxarife.setSenhaHash(passwordEncoder.encode(requisicao.senha()));
        }
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

    private AlmoxarifeResponse ativarRegistroAntigo(Almoxarife almoxarife, String cpf, String senha) {
        if (almoxarife.isAtivo()
                && almoxarife.getSenhaHash() == null
                && almoxarife.getCpf().equalsIgnoreCase(cpf)) {
            almoxarife.setSenhaHash(passwordEncoder.encode(senha));
            return paraResposta(repository.save(almoxarife));
        }
        throw new ConflitoException("Este e-mail já possui uma conta. Confira os dados ou faça login.");
    }

    private void validarSenha(String senha, boolean obrigatoria) {
        if (senha == null || senha.isBlank()) {
            if (obrigatoria) {
                throw new RegraNegocioException("A senha é obrigatória para criar a conta.");
            }
            return;
        }
        if (senha.length() < 8) {
            throw new RegraNegocioException("A senha deve ter pelo menos 8 caracteres.");
        }
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
