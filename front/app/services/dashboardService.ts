import api from "./api";
import type { Fornecedor, Item, Movimentacao } from "@/app/types/stockflow";

export interface ResumoDashboard {
  itens: number;
  fornecedores: number;
  estoqueBaixo: number;
  unidades: number;
  movimentacoesHoje: number;
}

export interface DadosDashboard {
  resumo: ResumoDashboard;
  recentes: Movimentacao[];
}

export async function buscarDadosDashboard(): Promise<DadosDashboard> {
  const [itensResponse, fornecedoresResponse, movimentacoesResponse] = await Promise.all([
    api.get<Item[]>("/itens"),
    api.get<Fornecedor[]>("/fornecedores"),
    api.get<Movimentacao[]>("/movimentacoes"),
  ]);

  const itens = itensResponse.data.filter((item) => item.ativo);
  const fornecedores = fornecedoresResponse.data.filter((fornecedor) => fornecedor.ativo);
  const movimentacoes = movimentacoesResponse.data.filter((movimentacao) => movimentacao.ativa);
  const hoje = new Date().toDateString();

  return {
    resumo: {
      itens: itens.length,
      fornecedores: fornecedores.length,
      estoqueBaixo: itens.filter((item) => item.quantidadeEstoque <= item.estoqueMinimo).length,
      unidades: itens.reduce((total, item) => total + item.quantidadeEstoque, 0),
      movimentacoesHoje: movimentacoes.filter(
        (movimentacao) => new Date(movimentacao.dataHora).toDateString() === hoje,
      ).length,
    },
    recentes: movimentacoes.slice(0, 5),
  };
}
