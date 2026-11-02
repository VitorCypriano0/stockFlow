package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.BootstrapRequest;
import br.com.stockflow.application.dto.BootstrapResponse;
import br.com.stockflow.application.exceptions.CredenciaisInvalidasException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BootstrapService {

    private final AlmoxarifeService almoxarifeService;
    private final String chaveConfigurada;

    public BootstrapService(
            AlmoxarifeService almoxarifeService,
            @Value("${app.bootstrap.key:}") String chaveConfigurada
    ) {
        this.almoxarifeService = almoxarifeService;
        this.chaveConfigurada = chaveConfigurada;
    }

    public synchronized BootstrapResponse criarAdministrador(
            String chaveRecebida,
            BootstrapRequest requisicao
    ) {
        byte[] chaveEsperada = chaveConfigurada.getBytes(StandardCharsets.UTF_8);
        if (chaveConfigurada.length() < 32 || chaveEsperada.length < 32) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "O bootstrap está desabilitado. Configure APP_BOOTSTRAP_KEY com pelo menos 32 caracteres."
            );
        }

        byte[] chaveInformada = chaveRecebida == null
                ? new byte[0]
                : chaveRecebida.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(chaveEsperada, chaveInformada)) {
            throw new CredenciaisInvalidasException("Chave de bootstrap inválida.");
        }

        return almoxarifeService.criarAdministradorInicial(requisicao);
    }
}
