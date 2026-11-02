package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.AlmoxarifeResponse;
import br.com.stockflow.application.dto.AuthResponse;
import br.com.stockflow.application.dto.LoginRequest;
import br.com.stockflow.application.exceptions.CredenciaisInvalidasException;
import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AlmoxarifeRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final long validadeEmMinutos;

    public AuthService(
            AlmoxarifeRepository repository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            @Value("${spring.tempo_expiracao}") long validadeEmMinutos
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        if (validadeEmMinutos <= 0) {
            throw new IllegalArgumentException("spring.tempo_expiracao deve ser maior que zero.");
        }
        this.validadeEmMinutos = validadeEmMinutos;
    }

    /** Valida as credenciais e devolve um JWT assinado para as próximas chamadas da API. */
    @Transactional
    public AuthResponse entrar(LoginRequest requisicao) {
        String email = requisicao.email().trim().toLowerCase();
        Almoxarife almoxarife = repository.findByEmailIgnoreCase(email)
                .filter(Almoxarife::isAtivo)
                .filter(usuario -> !usuario.isBloqueado())
                .filter(usuario -> usuario.getSenhaHash() != null
                        && passwordEncoder.matches(requisicao.senha(), usuario.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);

        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(validadeEmMinutos, ChronoUnit.MINUTES);
        String token = tokenService.gerarToken(almoxarife, agora, expiraEm);

        return new AuthResponse(token, "Bearer", expiraEm, paraResposta(almoxarife));
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
}
