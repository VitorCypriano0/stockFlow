package br.com.stockflow.infra.configuration;

import br.com.stockflow.application.services.TokenService;
import com.auth0.jwt.exceptions.JWTException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if (path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")) {
            return true;
        }

        return HttpMethod.POST.matches(request.getMethod())
                && (path.equals("/api/auth/login")
                || path.equals("/api/auth/registrar")
                || path.equals("/api/bootstrap/admin"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token de autenticação inválido.");
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        try {
            var almoxarife = tokenService.verificarToken(token);
            var autenticacao = new UsernamePasswordAuthenticationToken(
                    almoxarife,
                    null,
                    almoxarife.authorities()
            );
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        } catch (JWTException erro) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token de autenticação inválido.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
