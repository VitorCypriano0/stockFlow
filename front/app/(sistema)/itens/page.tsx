"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import PageHeader from "@/app/components/PageHeader";
import StatusPill from "@/app/components/StatusPill";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { Item } from "@/app/types/stockflow";

export default function ItensPage() {
  const [itens, setItens] = useState<Item[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const { data } = await api.get<Item[]>("/itens");
      setItens(data);
      setErro("");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível carregar os itens."));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  async function excluir(item: Item) {
    if (!item.ativo || !window.confirm("Desativar o item " + item.nome + "?")) {
      return;
    }

    try {
      await api.delete("/itens/" + item.id);
      await carregar();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível desativar o item."));
    }
  }

  return (
    <section>
      <PageHeader
        titulo="Itens"
        descricao="Consulte o saldo e mantenha os dados dos produtos do almoxarifado."
        acaoHref="/itens/novo"
        acaoTexto="Novo item"
      />
      {erro ? (
        <p role="alert" className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{erro}</p>
      ) : null}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[850px] text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-5 py-3.5">Código</th>
                <th className="px-5 py-3.5">Item</th>
                <th className="px-5 py-3.5">Fornecedor</th>
                <th className="px-5 py-3.5">Saldo</th>
                <th className="px-5 py-3.5">Mínimo</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {itens.map((item) => {
                const estoqueBaixo = item.quantidadeEstoque <= item.estoqueMinimo;
                return (
                  <tr key={item.id} className="text-slate-700">
                    <td className="px-5 py-4 font-mono text-xs">{item.codigo}</td>
                    <td className="px-5 py-4 font-semibold text-slate-900">
                      {item.nome}
                      <span className="ml-2 text-xs font-normal text-slate-500">({item.unidade})</span>
                    </td>
                    <td className="px-5 py-4">{item.fornecedorNome}</td>
                    <td className={"px-5 py-4 font-semibold " + (estoqueBaixo && item.ativo ? "text-amber-700" : "text-slate-800")}>
                      {item.quantidadeEstoque}
                      {estoqueBaixo && item.ativo ? <span className="ml-2 text-xs font-normal">Estoque baixo</span> : null}
                    </td>
                    <td className="px-5 py-4">{item.estoqueMinimo}</td>
                    <td className="px-5 py-4"><StatusPill ativo={item.ativo} /></td>
                    <td className="px-5 py-4">
                      <div className="flex gap-3">
                        <Link className="font-semibold text-emerald-800 hover:underline" href={"/itens/" + item.id + "/editar"}>
                          Editar
                        </Link>
                        {item.ativo ? (
                          <button className="font-semibold text-red-700 hover:underline" onClick={() => void excluir(item)}>
                            Desativar
                          </button>
                        ) : null}
                      </div>
                    </td>
                  </tr>
                );
              })}
              {!carregando && itens.length === 0 ? (
                <tr><td colSpan={7} className="px-5 py-12 text-center text-slate-500">Nenhum item cadastrado.</td></tr>
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
