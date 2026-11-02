package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.AlmoxarifeRequest;
import br.com.stockflow.application.dto.AlmoxarifeResponse;
import br.com.stockflow.application.dto.BootstrapRequest;
import br.com.stockflow.application.dto.BootstrapResponse;
import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.application.exceptions.ConflitoException;
import br.com.stockflow.application.exceptions.RegraNegocioException;
import br.com.stockflow.application.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import br.com.stockflow.domain.repository.EmpresaRepository;
import br.com.stockflow.domain.validation.CpfValidador;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlmoxarifeService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    private final AlmoxarifeRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaAtualService empresaAtualService;
    private final PasswordEncoder passwordEncoder;

    public AlmoxarifeService(
            AlmoxarifeRepository repository,
            EmpresaRepository empresaRepository,
            EmpresaAtualService empresaAtualService,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.empresaAtualService = empresaAtualService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AlmoxarifeResponse> listar() {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return repository.findAllByEmpresa_IdOrderByNomeAsc(empresaId).stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlmoxarifeResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    public AlmoxarifeResponse criar(AlmoxarifeRequest requisicao) {
        return criar(requisicao, empresaAtualService.obterEmpresaAtual(), ROLE_USER);
    }

    private AlmoxarifeResponse criar(AlmoxarifeRequest requisicao, Empresa empresa, String role) {
        validarSenha(requisicao.senha(), true);
        String cpf = validarCpf(requisicao.cpf());
        String email = limpar(requisicao.email()).toLowerCase();
        if (repository.existsByCpfIgnoreCase(cpf)) {
            throw new ConflitoException("Já existe um almoxarife com este CPF.");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe um almoxarife com este e-mail.");
        }

        Almoxarife almoxarife = new Almoxarife();
        copiarDados(almoxarife, requisicao, cpf, email);
        almoxarife.setEmpresa(empresa);
        almoxarife.setSenhaHash(passwordEncoder.encode(requisicao.senha()));
        almoxarife.setRole(role);
        return paraResposta(repository.save(almoxarife));
    }

    /** Cria uma conta ou permite que um registro antigo escolha a senha pela primeira vez. */
    @Transactional
    public AlmoxarifeResponse registrar(AlmoxarifeRequest requisicao) {
        validarSenha(requisicao.senha(), true);
        String cpf = validarCpf(requisicao.cpf());
        String email = limpar(requisicao.email()).toLowerCase();

        Almoxarife existente = repository.findByEmailIgnoreCase(email).orElse(null);
        if (existente != null) {
            return ativarRegistroAntigo(existente, cpf, requisicao.senha());
        }

        String empresaNome = limpar(requisicao.empresaNome());
        if (empresaNome == null || empresaNome.isBlank()) {
            throw new RegraNegocioException("Informe o nome da empresa para criar uma nova conta.");
        }
        Empresa empresa = empresaRepository.save(new Empresa(empresaNome));
        return criar(requisicao, empresa, ROLE_ADMIN);
    }

    @Transactional
    public BootstrapResponse criarAdministradorInicial(BootstrapRequest requisicao) {
        if (repository.existsByRole(ROLE_ADMIN)) {
            throw new ConflitoException("O administrador inicial já foi criado.");
        }

        AlmoxarifeRequest dadosAdministrador = new AlmoxarifeRequest(
                requisicao.nome(), requisicao.cpf(), requisicao.email(),
                requisicao.telefone(), requisicao.senha(), requisicao.empresaNome()
        );
        String empresaNome = limpar(requisicao.empresaNome());
        if (empresaNome == null || empresaNome.isBlank()) {
            throw new RegraNegocioException("Informe o nome da empresa.");
        }

        Empresa empresa = empresaRepository.save(new Empresa(empresaNome));
        AlmoxarifeResponse administrador = criar(dadosAdministrador, empresa, ROLE_ADMIN);
        return new BootstrapResponse(
                empresa.getId(), empresa.getNome(), administrador.id(),
                administrador.nome(), administrador.email()
        );
    }

    @Transactional
    public AlmoxarifeResponse atualizar(Long id, AlmoxarifeRequest requisicao) {
        Almoxarife almoxarife = obter(id);
        String cpf = validarCpf(requisicao.cpf());
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

    @Transactional
    public AlmoxarifeResponse atualizarBloqueio(Long id, boolean bloqueado) {
        Almoxarife almoxarife = obter(id);
        if (!almoxarife.isAtivo()) {
            throw new RegraNegocioException("Não é possível alterar o acesso de um almoxarife inativo.");
        }

        almoxarife.setBloqueado(bloqueado);
        return paraResposta(repository.save(almoxarife));
    }

    private Almoxarife obter(Long id) {
        Long empresaId = empresaAtualService.obterEmpresaAtual().getId();
        return repository.findByIdAndEmpresa_Id(id, empresaId)
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
                && !almoxarife.isBloqueado()
                && almoxarife.getSenhaHash() == null
                && almoxarife.getCpf().equalsIgnoreCase(cpf)) {
            almoxarife.setSenhaHash(passwordEncoder.encode(senha));
            if (almoxarife.getEmpresa() != null
                    && !repository.existsByRoleAndEmpresa_Id(ROLE_ADMIN, almoxarife.getEmpresa().getId())) {
                almoxarife.setRole(ROLE_ADMIN);
            }
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
                almoxarife.isAtivo(),
                almoxarife.isBloqueado(),
                almoxarife.getRole()
        );
    }

    private String limpar(String texto) {
        return texto == null ? null : texto.trim();
    }

    private String validarCpf(String cpf) {
        String valor = limpar(cpf);
        if (!CpfValidador.ehValido(valor)) {
            throw new RegraNegocioException("Informe um CPF válido.");
        }
        return valor;
    }
}
