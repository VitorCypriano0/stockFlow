package br.com.stockflow.application.services;

import br.com.stockflow.application.dto.AlmoxarifeAutenticado;
import br.com.stockflow.application.exceptions.CredenciaisInvalidasException;
import br.com.stockflow.application.exceptions.RecursoNaoEncontradoException;
import br.com.stockflow.application.exceptions.RegraNegocioException;
import br.com.stockflow.domain.entities.Almoxarife;
import br.com.stockflow.domain.entities.Empresa;
import br.com.stockflow.domain.repository.AlmoxarifeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaAtualService {

    private final AlmoxarifeRepository almoxarifeRepository;

    public EmpresaAtualService(AlmoxarifeRepository almoxarifeRepository) {
        this.almoxarifeRepository = almoxarifeRepository;
    }

    @Transactional(readOnly = true)
    public Empresa obterEmpresaAtual() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null
                || !(autenticacao.getPrincipal() instanceof AlmoxarifeAutenticado almoxarifeAutenticado)) {
            throw new CredenciaisInvalidasException("Sessão autenticada não encontrada.");
        }
        Almoxarife almoxarife = almoxarifeRepository.findById(almoxarifeAutenticado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarife autenticado não encontrado."));
        Empresa empresa = almoxarife.getEmpresa();
        if (empresa == null) {
            throw new RegraNegocioException("O almoxarife autenticado ainda não está associado a uma empresa.");
        }
        return empresa;
    }
}
