export interface Almoxarife {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  telefone: string | null;
  ativo: boolean;
}

export interface AlmoxarifeInput {
  nome: string;
  cpf: string;
  email: string;
  telefone: string | null;
  senha?: string;
}

export interface AuthResponse {
  token: string;
  tipo: "Bearer";
  expiraEm: string;
  almoxarife: Almoxarife;
}

export interface Fornecedor {
  id: number;
  nome: string;
  cnpj: string | null;
  contato: string | null;
  telefone: string | null;
  email: string | null;
  ativo: boolean;
}

export interface FornecedorInput {
  nome: string;
  cnpj: string | null;
  contato: string | null;
  telefone: string | null;
  email: string | null;
}

export interface Item {
  id: number;
  codigo: string;
  nome: string;
  descricao: string | null;
  unidade: string;
  quantidadeEstoque: number;
  estoqueMinimo: number;
  fornecedorId: number;
  fornecedorNome: string;
  ativo: boolean;
}

export interface ItemInput {
  codigo: string;
  nome: string;
  descricao: string | null;
  unidade: string;
  estoqueMinimo: number;
  fornecedorId: number;
}

export type TipoMovimentacao = "ENTRADA" | "SAIDA";

export interface Movimentacao {
  id: number;
  itemId: number;
  itemNome: string;
  almoxarifeId: number;
  almoxarifeNome: string;
  tipo: TipoMovimentacao;
  quantidade: number;
  dataHora: string;
  observacao: string | null;
  ativa: boolean;
}

export interface MovimentacaoInput {
  itemId: number;
  almoxarifeId: number;
  tipo: TipoMovimentacao;
  quantidade: number;
  observacao: string | null;
}

export interface RegistroHistorico {
  id: number;
  movimentacaoId: number;
  acao: "CRIADA" | "ATUALIZADA" | "CANCELADA";
  tipo: TipoMovimentacao;
  itemNome: string;
  quantidade: number;
  almoxarifeId: number;
  almoxarifeNome: string;
  dataHora: string;
  observacao: string | null;
}
