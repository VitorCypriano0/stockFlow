"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import PageHeader from "@/app/components/PageHeader";
import StatusPill from "@/app/components/StatusPill";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { Fornecedor } from "@/app/types/stockflow";

export default function FornecedoresPage() {
  const [fornecedores, setFornecedores] = useState<Fornecedor[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const { data } = await api.get<Fornecedor[]>("/fornecedores");
      setFornecedores(data);
      setErro("");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível carregar os fornecedores."));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  async function excluir(fornecedor: Fornecedor) {
    if (
      !fornecedor.ativo ||
      !window.confirm("Desativar o fornecedor " + fornecedor.nome + "?")
    ) {
      return;
    }

    try {
      await api.delete("/fornecedores/" + fornecedor.id);
      await carregar();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível desativar o fornecedor."));
    }
  }

  return (
    <section>
      <PageHeader
        titulo="Fornecedores"
        descricao="Mantenha os dados das empresas ligadas aos itens do estoque."
        acaoHref="/fornecedores/novo"
        acaoTexto="Novo fornecedor"
      />
      {erro ? (
        <p role="alert" className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">{erro}</p>
      ) : null}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[760px] text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-5 py-3.5">Fornecedor</th>
                <th className="px-5 py-3.5">CNPJ</th>
                <th className="px-5 py-3.5">Contato</th>
                <th className="px-5 py-3.5">E-mail / telefone</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {fornecedores.map((fornecedor) => (
                <tr key={fornecedor.id} className="text-slate-700">
                  <td className="px-5 py-4 font-semibold text-slate-900">{fornecedor.nome}</td>
                  <td className="px-5 py-4">{fornecedor.cnpj || "—"}</td>
                  <td className="px-5 py-4">{fornecedor.contato || "—"}</td>
                  <td className="px-5 py-4">{fornecedor.email || fornecedor.telefone || "—"}</td>
                  <td className="px-5 py-4"><StatusPill ativo={fornecedor.ativo} /></td>
                  <td className="px-5 py-4">
                    <div className="flex gap-3">
                      <Link className="font-semibold text-emerald-800 hover:underline" href={"/fornecedores/" + fornecedor.id + "/editar"}>
                        Editar
                      </Link>
                      {fornecedor.ativo ? (
                        <button className="font-semibold text-red-700 hover:underline" onClick={() => void excluir(fornecedor)}>
                          Desativar
                        </button>
                      ) : null}
                    </div>
                  </td>
                </tr>
              ))}
              {!carregando && fornecedores.length === 0 ? (
                <tr><td colSpan={6} className="px-5 py-12 text-center text-slate-500">Nenhum fornecedor cadastrado.</td></tr>
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
