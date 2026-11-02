package br.com.stockflow.application.dto;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record AlmoxarifeAutenticado(
        Long id,
        String email,
        Long empresaId,
        Long tokenId,
        Collection<? extends GrantedAuthority> authorities
) {
}
