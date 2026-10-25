package br.com.stockflow.validation;

import java.util.regex.Pattern;

public final class CpfValidador {

    private static final Pattern FORMATO_ACEITO = Pattern.compile("[0-9.\\s-]+");
    private static final Pattern DIGITOS_REPETIDOS = Pattern.compile("([0-9])\\1{10}");

    private CpfValidador() {
    }

    public static boolean ehValido(String cpf) {
        if (cpf == null || !FORMATO_ACEITO.matcher(cpf).matches()) {
            return false;
        }

        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11 || DIGITOS_REPETIDOS.matcher(digitos).matches()) {
            return false;
        }

        int primeiroDigito = calcularDigito(digitos, 9, 10);
        if (primeiroDigito != digitos.charAt(9) - '0') {
            return false;
        }

        int segundoDigito = calcularDigito(digitos, 10, 11);
        return segundoDigito == digitos.charAt(10) - '0';
    }

    private static int calcularDigito(String cpf, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int indice = 0; indice < quantidade; indice++) {
            soma += (cpf.charAt(indice) - '0') * (pesoInicial - indice);
        }

        int digito = 11 - (soma % 11);
        return digito > 9 ? 0 : digito;
    }
}
