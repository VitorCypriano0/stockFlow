package br.com.stockflow.application.exceptions;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        this("E-mail ou senha inválidos.");
    }

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
