package br.com.stockflow.application.dto;

import br.com.stockflow.domain.entities.Almoxarife;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record AlmoxarifeAutenticado(
        Long id,
        String email,
        Long empresaId,
        Long tokenId,
        Collection<? extends GrantedAuthority> authorities
) {

    public AlmoxarifeAutenticado(Almoxarife almoxarife, Long tokenId) {
        this(
                almoxarife.getId(),
                almoxarife.getEmail(),
                almoxarife.getEmpresa() == null ? null : almoxarife.getEmpresa().getId(),
                tokenId,
                almoxarife.getAuthorities()
        );
    }
}
