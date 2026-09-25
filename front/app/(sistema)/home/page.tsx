"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import PageHeader from "@/app/components/PageHeader";
import { api, mensagemDeErro } from "@/app/lib/api";
import { formatarDataHora } from "@/app/lib/format";
import type { Fornecedor, Item, Movimentacao } from "@/app/types/stockflow";

interface Resumo {
  itens: number;
  fornecedores: number;
  estoqueBaixo: number;
  unidades: number;
  movimentacoesHoje: number;
}

const resumoVazio: Resumo = {
  itens: 0,
  fornecedores: 0,
  estoqueBaixo: 0,
  unidades: 0,
  movimentacoesHoje: 0,
};

export default function HomePage() {
  const [resumo, setResumo] = useState<Resumo>(resumoVazio);
  const [recentes, setRecentes] = useState<Movimentacao[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const [itensResponse, fornecedoresResponse, movimentacoesResponse] =
        await Promise.all([
          api.get<Item[]>("/itens"),
          api.get<Fornecedor[]>("/fornecedores"),
          api.get<Movimentacao[]>("/movimentacoes"),
        ]);

      const itens = itensResponse.data.filter((item) => item.ativo);
      const fornecedores = fornecedoresResponse.data.filter(
        (fornecedor) => fornecedor.ativo,
      );
      const movimentacoes = movimentacoesResponse.data.filter(
        (movimentacao) => movimentacao.ativa,
      );
      const hoje = new Date().toDateString();

      setResumo({
        itens: itens.length,
        fornecedores: fornecedores.length,
        estoqueBaixo: itens.filter(
          (item) => item.quantidadeEstoque <= item.estoqueMinimo,
        ).length,
        unidades: itens.reduce((total, item) => total + item.quantidadeEstoque, 0),
        movimentacoesHoje: movimentacoes.filter(
          (movimentacao) => new Date(movimentacao.dataHora).toDateString() === hoje,
        ).length,
      });
      setRecentes(movimentacoes.slice(0, 5));
      setErro("");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível carregar a visão geral."));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  const cartoes = [
    { titulo: "Itens ativos", valor: resumo.itens, detalhe: "Itens cadastrados" },
    { titulo: "Unidades em estoque", valor: resumo.unidades, detalhe: "Saldo somado dos itens" },
    { titulo: "Estoque baixo", valor: resumo.estoqueBaixo, detalhe: "No mínimo ou abaixo" },
    { titulo: "Movimentações hoje", valor: resumo.movimentacoesHoje, detalhe: "Entradas e saídas ativas" },
  ];

  return (
    <section>
      <PageHeader
        titulo="Visão geral"
        descricao="Acompanhe os principais números do almoxarifado e abra uma tarefa."
      />
      {erro ? (
        <p role="alert" className="mb-5 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{erro}</p>
      ) : null}

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {cartoes.map((cartao) => (
          <article key={cartao.titulo} className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm font-medium text-slate-600">{cartao.titulo}</p>
            <p className="mt-3 text-3xl font-semibold tracking-tight text-slate-900">
              {carregando ? "—" : cartao.valor}
            </p>
            <p className="mt-1 text-xs text-slate-500">{cartao.detalhe}</p>
          </article>
        ))}
      </div>

      <div className="mt-7 grid gap-5 xl:grid-cols-[1.4fr_0.6fr]">
        <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <div className="flex items-center justify-between border-b border-slate-100 px-5 py-4">
            <div>
              <h2 className="font-semibold text-slate-900">Movimentações recentes</h2>
              <p className="mt-1 text-xs text-slate-500">Os últimos registros feitos no sistema.</p>
            </div>
            <Link href="/movimentacoes" className="text-sm font-semibold text-emerald-800 hover:underline">
              Ver todas
            </Link>
          </div>
          <div className="divide-y divide-slate-100">
            {recentes.map((movimentacao) => (
              <div key={movimentacao.id} className="flex flex-wrap items-center justify-between gap-3 px-5 py-4">
                <div>
                  <p className="text-sm font-semibold text-slate-900">{movimentacao.itemNome}</p>
                  <p className="mt-1 text-xs text-slate-500">
                    {movimentacao.almoxarifeNome} · {formatarDataHora(movimentacao.dataHora)}
                  </p>
                </div>
                <span className={movimentacao.tipo === "ENTRADA" ? "rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-800" : "rounded-full bg-amber-50 px-3 py-1 text-xs font-bold text-amber-800"}>
                  {movimentacao.tipo === "ENTRADA" ? "+" : "−"} {movimentacao.quantidade}
                </span>
              </div>
            ))}
            {!carregando && recentes.length === 0 ? (
              <p className="px-5 py-10 text-center text-sm text-slate-500">Ainda não há movimentações.</p>
            ) : null}
          </div>
        </section>

        <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="font-semibold text-slate-900">Acesso rápido</h2>
          <p className="mt-1 text-xs text-slate-500">Comece por um desses cadastros.</p>
          <div className="mt-4 grid gap-2">
            <Link className="rounded-xl bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-900 transition hover:bg-emerald-100" href="/movimentacoes/novo">
              Registrar movimentação <span aria-hidden="true">→</span>
            </Link>
            <Link className="rounded-xl bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:bg-slate-100" href="/itens/novo">
              Cadastrar item <span aria-hidden="true">→</span>
            </Link>
            <Link className="rounded-xl bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:bg-slate-100" href="/fornecedores">
              Ver {resumo.fornecedores} fornecedores <span aria-hidden="true">→</span>
            </Link>
            <Link className="rounded-xl bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-800 transition hover:bg-slate-100" href="/historico">
              Consultar auditoria <span aria-hidden="true">→</span>
            </Link>
          </div>
        </section>
      </div>
    </section>
  );
}
