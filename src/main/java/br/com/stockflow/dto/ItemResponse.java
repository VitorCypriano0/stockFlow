package br.com.stockflow.dto;

public record ItemResponse(
        Long id,
        String codigo,
        String nome,
        String descricao,
        String unidade,
        int quantidadeEstoque,
        int estoqueMinimo,
        Long fornecedorId,
        String fornecedorNome,
        boolean ativo
) {
}
