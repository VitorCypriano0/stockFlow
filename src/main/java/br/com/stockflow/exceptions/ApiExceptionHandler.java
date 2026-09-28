package br.com.stockflow.exceptions;

import br.com.stockflow.dto.ApiError;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> recursoNaoEncontrado(RecursoNaoEncontradoException erro) {
        return resposta(HttpStatus.NOT_FOUND, erro.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ApiError> credenciaisInvalidas(CredenciaisInvalidasException erro) {
        return resposta(HttpStatus.UNAUTHORIZED, erro.getMessage());
    }

    @ExceptionHandler({
            RegraNegocioException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> requisicaoInvalida(Exception erro) {
        String mensagem = erro instanceof MethodArgumentNotValidException validacao
                ? validacao.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
                        .orElse("Os dados informados são inválidos.")
                : erro instanceof HttpMessageNotReadableException
                        ? "O corpo da requisição contém dados inválidos."
                        : erro.getMessage();
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler({
            ConflitoException.class,
            DataIntegrityViolationException.class,
            ObjectOptimisticLockingFailureException.class
    })
    public ResponseEntity<ApiError> conflito(Exception erro) {
        String mensagem;
        if (erro instanceof ConflitoException) {
            mensagem = erro.getMessage();
        } else if (erro instanceof ObjectOptimisticLockingFailureException) {
            mensagem = "O estoque foi alterado por outra operação. Atualize a página e tente novamente.";
        } else {
            mensagem = "Já existe um cadastro com os mesmos dados únicos.";
        }
        return resposta(HttpStatus.CONFLICT, mensagem);
    }

    private ResponseEntity<ApiError> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ApiError(mensagem, LocalDateTime.now()));
    }
}
