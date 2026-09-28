package br.com.stockflow.services;

import br.com.stockflow.dto.AlmoxarifeResponse;
import br.com.stockflow.dto.AuthResponse;
import br.com.stockflow.dto.LoginRequest;
import br.com.stockflow.entities.Almoxarife;
import br.com.stockflow.exceptions.CredenciaisInvalidasException;
import br.com.stockflow.repository.AlmoxarifeRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AlmoxarifeRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long validadeEmMinutos;

    public AuthService(
            AlmoxarifeRepository repository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration-minutes}") long validadeEmMinutos
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.validadeEmMinutos = validadeEmMinutos;
    }

    /** Valida as credenciais e devolve um JWT assinado para as próximas chamadas da API. */
    public AuthResponse entrar(LoginRequest requisicao) {
        String email = requisicao.email().trim().toLowerCase();
        Almoxarife almoxarife = repository.findByEmailIgnoreCase(email)
                .filter(Almoxarife::isAtivo)
                .filter(usuario -> usuario.getSenhaHash() != null
                        && passwordEncoder.matches(requisicao.senha(), usuario.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);

        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(validadeEmMinutos, ChronoUnit.MINUTES);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("stockflow")
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .subject(almoxarife.getId().toString())
                .claim("email", almoxarife.getEmail())
                .claim("nome", almoxarife.getNome())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims
        )).getTokenValue();

        return new AuthResponse(token, "Bearer", expiraEm, paraResposta(almoxarife));
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
}
