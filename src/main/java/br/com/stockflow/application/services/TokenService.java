package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.AlmoxarifeAutenticado;
import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.entities.Token;
import br.com.stockflow.domain.repository.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {

    private static final String EMISSOR = "stockflow";

    private final TokenRepository tokenRepository;
    private final Algorithm algoritmo;
    private final JWTVerifier verificador;

    public TokenService(
            TokenRepository tokenRepository,
            @Value("${spring.secretkey}") String segredo
    ) {
        if (segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET deve ter pelo menos 32 bytes.");
        }

        this.tokenRepository = tokenRepository;
        this.algoritmo = Algorithm.HMAC256(segredo);
        this.verificador = JWT.require(algoritmo)
                .withIssuer(EMISSOR)
                .build();
    }

    @Transactional
    public String gerarToken(Almoxarife almoxarife, Instant emitidoEm, Instant expiraEm) {
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

        return new AlmoxarifeAutenticado(
                almoxarife.getId(),
                almoxarife.getEmail(),
                almoxarife.getEmpresa() == null ? null : almoxarife.getEmpresa().getId(),
                tokenSalvo.getId(),
                List.of(new SimpleGrantedAuthority(almoxarife.getRole()))
        );
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
