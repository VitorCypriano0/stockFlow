"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import PageHeader from "@/app/components/PageHeader";
import StatusPill from "@/app/components/StatusPill";
import { api, mensagemDeErro } from "@/app/lib/api";
import { formatarDataHora } from "@/app/lib/format";
import type { Movimentacao } from "@/app/types/stockflow";

export default function MovimentacoesPage() {
  const [movimentacoes, setMovimentacoes] = useState<Movimentacao[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const { data } = await api.get<Movimentacao[]>("/movimentacoes");
      setMovimentacoes(data);
      setErro("");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível carregar as movimentações."));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  async function cancelar(movimentacao: Movimentacao) {
    if (
      !movimentacao.ativa ||
      !window.confirm("Cancelar a movimentação #" + movimentacao.id + "?")
    ) {
      return;
    }

    try {
      await api.delete("/movimentacoes/" + movimentacao.id);
      await carregar();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível cancelar a movimentação."));
    }
  }

  return (
    <section>
      <PageHeader
        titulo="Movimentações"
        descricao="Cada entrada ou saída atualiza o saldo e gera um registro de auditoria."
        acaoHref="/movimentacoes/novo"
        acaoTexto="Nova movimentação"
      />
      {erro ? (
        <p role="alert" className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{erro}</p>
      ) : null}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[920px] text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-5 py-3.5">Data e hora</th>
                <th className="px-5 py-3.5">Tipo</th>
                <th className="px-5 py-3.5">Item</th>
                <th className="px-5 py-3.5">Quantidade</th>
                <th className="px-5 py-3.5">Almoxarife</th>
                <th className="px-5 py-3.5">Estado</th>
                <th className="px-5 py-3.5">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {movimentacoes.map((movimentacao) => (
                <tr key={movimentacao.id} className="text-slate-700">
                  <td className="px-5 py-4">{formatarDataHora(movimentacao.dataHora)}</td>
                  <td className="px-5 py-4">
                    <span className={movimentacao.tipo === "ENTRADA" ? "font-bold text-emerald-800" : "font-bold text-amber-700"}>
                      {movimentacao.tipo === "ENTRADA" ? "Entrada" : "Saída"}
                    </span>
                  </td>
                  <td className="px-5 py-4 font-semibold text-slate-900">{movimentacao.itemNome}</td>
                  <td className="px-5 py-4">{movimentacao.quantidade}</td>
                  <td className="px-5 py-4">{movimentacao.almoxarifeNome}</td>
                  <td className="px-5 py-4"><StatusPill ativo={movimentacao.ativa} /></td>
                  <td className="px-5 py-4">
                    {movimentacao.ativa ? (
                      <div className="flex gap-3">
                        <Link className="font-semibold text-emerald-800 hover:underline" href={"/movimentacoes/" + movimentacao.id + "/editar"}>
                          Editar
                        </Link>
                        <button className="font-semibold text-red-700 hover:underline" onClick={() => void cancelar(movimentacao)}>
                          Cancelar
                        </button>
                      </div>
                    ) : <span className="text-xs text-slate-400">Sem ações</span>}
                  </td>
                </tr>
              ))}
              {!carregando && movimentacoes.length === 0 ? (
                <tr><td colSpan={7} className="px-5 py-12 text-center text-slate-500">Nenhuma movimentação registrada.</td></tr>
              ) : null}
              {carregando ? (
                <tr><td colSpan={7} className="px-5 py-12 text-center text-slate-500">Carregando...</td></tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
