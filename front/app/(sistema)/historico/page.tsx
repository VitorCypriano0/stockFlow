"use client";

import { useCallback, useEffect, useState } from "react";
import PageHeader from "@/app/components/PageHeader";
import { api, mensagemDeErro } from "@/app/lib/api";
import { formatarDataHora } from "@/app/lib/format";
import type { RegistroHistorico } from "@/app/types/stockflow";

export default function HistoricoPage() {
  const [registros, setRegistros] = useState<RegistroHistorico[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const { data } = await api.get<RegistroHistorico[]>("/historico");
      setRegistros(data);
      setErro("");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível carregar o histórico."));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  return (
    <section>
      <PageHeader
        titulo="Histórico de auditoria"
        descricao="Cada registro mostra quando ocorreu a ação e qual almoxarife a realizou."
      />
      {erro ? (
        <p role="alert" className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{erro}</p>
      ) : null}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[900px] text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-5 py-3.5">Data e hora</th>
                <th className="px-5 py-3.5">Ação</th>
                <th className="px-5 py-3.5">Movimentação</th>
                <th className="px-5 py-3.5">Item</th>
                <th className="px-5 py-3.5">Almoxarife</th>
                <th className="px-5 py-3.5">Observação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {registros.map((registro) => (
                <tr key={registro.id} className="text-slate-700">
                  <td className="px-5 py-4">{formatarDataHora(registro.dataHora)}</td>
                  <td className="px-5 py-4">
                    <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-700">
                      {registro.acao}
                    </span>
                  </td>
                  <td className="px-5 py-4">
                    #{registro.movimentacaoId} · {registro.tipo === "ENTRADA" ? "Entrada" : "Saída"} · {registro.quantidade}
                  </td>
                  <td className="px-5 py-4 font-semibold text-slate-900">{registro.itemNome}</td>
                  <td className="px-5 py-4">{registro.almoxarifeNome}</td>
                  <td className="max-w-64 truncate px-5 py-4" title={registro.observacao ?? ""}>
                    {registro.observacao || "—"}
                  </td>
                </tr>
              ))}
              {!carregando && registros.length === 0 ? (
                <tr><td colSpan={6} className="px-5 py-12 text-center text-slate-500">O histórico aparecerá aqui após a primeira movimentação.</td></tr>
              ) : null}
              {carregando ? (
                <tr><td colSpan={6} className="px-5 py-12 text-center text-slate-500">Carregando...</td></tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
