package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.AlmoxarifeAutenticado;
import br.com.stockflow.application.dto.LoginRequest;
import br.com.stockflow.application.exceptions.CredenciaisInvalidasException;
import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.entities.Token;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import br.com.stockflow.domain.repository.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {

    private static final String EMISSOR = "stockflow";

    private final TokenRepository tokenRepository;
    private final AlmoxarifeRepository almoxarifeRepository;
    private final Algorithm algoritmo;
    private final JWTVerifier verificador;
    private final long validadeEmMinutos;

    public TokenService(
            TokenRepository tokenRepository,
            AlmoxarifeRepository almoxarifeRepository,
            @Value("${spring.secretkey}") String segredo,
            @Value("${spring.tempo_expiracao}") long validadeEmMinutos
    ) {
        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("spring.secretkey deve ter pelo menos 32 bytes.");
        }

        this.tokenRepository = tokenRepository;
        this.almoxarifeRepository = almoxarifeRepository;
        this.algoritmo = Algorithm.HMAC256(segredo);
        this.verificador = JWT.require(algoritmo)
                .withIssuer(EMISSOR)
                .build();
        if (validadeEmMinutos <= 0) {
            throw new IllegalArgumentException("spring.tempo_expiracao deve ser maior que zero.");
        }
        this.validadeEmMinutos = validadeEmMinutos;
    }

    @Transactional
    public String gerarToken(LoginRequest requisicao) {
        String email = requisicao.email().trim().toLowerCase(Locale.ROOT);
        Almoxarife almoxarife = almoxarifeRepository.findByEmailIgnoreCase(email)
                .orElseThrow(CredenciaisInvalidasException::new);
        Instant emitidoEm = Instant.now();
        Instant expiraEm = emitidoEm.plus(validadeEmMinutos, ChronoUnit.MINUTES);

        String token = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(almoxarife.getEmail())
                .withIssuedAt(Date.from(emitidoEm))
                .withExpiresAt(Date.from(expiraEm))
                .sign(algoritmo);
        tokenRepository.save(new Token(token, almoxarife));
        return token;
    }

    @Transactional(readOnly = true)
    public AlmoxarifeAutenticado verificarToken(String token) {
        DecodedJWT jwt = verificador.verify(token);
        String subject = jwt.getSubject();
        Date expiracao = jwt.getExpiresAt();

        if (subject == null || subject.isBlank() || expiracao == null) {
            throw tokenInvalido();
        }

        Token tokenSalvo = tokenRepository.findTokenByToken(token)
                .orElseThrow(TokenService::tokenInvalido);
        var almoxarife = tokenSalvo.getAlmoxarife();
        if (!almoxarife.getEmail().equals(subject) || !almoxarife.isAtivo() || almoxarife.isBloqueado()) {
            throw tokenInvalido();
        }

        return new AlmoxarifeAutenticado(almoxarife, tokenSalvo.getId());
    }

    @Transactional
    public void revogar(Long tokenId) {
        if (tokenId != null) {
            tokenRepository.deleteById(tokenId);
        }
    }

    private static JWTVerificationException tokenInvalido() {
        return new JWTVerificationException("Token inválido ou conta inativa/bloqueada.");
    }
}
