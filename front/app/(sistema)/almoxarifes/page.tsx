"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import PageHeader from "@/app/components/PageHeader";
import StatusPill from "@/app/components/StatusPill";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { Almoxarife } from "@/app/types/stockflow";

export default function AlmoxarifesPage() {
  const [almoxarifes, setAlmoxarifes] = useState<Almoxarife[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const { data } = await api.get<Almoxarife[]>("/almoxarifes");
      setAlmoxarifes(data);
      setErro("");
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível carregar os almoxarifes."));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  async function excluir(almoxarife: Almoxarife) {
    if (
      !almoxarife.ativo ||
      !window.confirm("Desativar o cadastro de " + almoxarife.nome + "?")
    ) {
      return;
    }

    try {
      await api.delete("/almoxarifes/" + almoxarife.id);
      await carregar();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível desativar o almoxarife."));
    }
  }

  return (
    <section>
      <PageHeader
        titulo="Almoxarifes"
        descricao="Gerencie quem pode ser associado às movimentações do estoque."
        acaoHref="/almoxarifes/novo"
        acaoTexto="Novo almoxarife"
      />
      {erro ? (
        <p role="alert" className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-800">
          {erro}
        </p>
      ) : null}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[760px] text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-5 py-3.5">Nome</th>
                <th className="px-5 py-3.5">CPF</th>
                <th className="px-5 py-3.5">E-mail</th>
                <th className="px-5 py-3.5">Telefone</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {almoxarifes.map((almoxarife) => (
                <tr key={almoxarife.id} className="text-slate-700">
                  <td className="px-5 py-4 font-semibold text-slate-900">{almoxarife.nome}</td>
                  <td className="px-5 py-4">{almoxarife.cpf}</td>
                  <td className="px-5 py-4">{almoxarife.email}</td>
                  <td className="px-5 py-4">{almoxarife.telefone || "—"}</td>
                  <td className="px-5 py-4"><StatusPill ativo={almoxarife.ativo} /></td>
                  <td className="px-5 py-4">
                    <div className="flex gap-3">
                      <Link className="font-semibold text-emerald-800 hover:underline" href={"/almoxarifes/" + almoxarife.id + "/editar"}>
                        Editar
                      </Link>
                      {almoxarife.ativo ? (
                        <button className="font-semibold text-red-700 hover:underline" onClick={() => void excluir(almoxarife)}>
                          Desativar
                        </button>
                      ) : null}
                    </div>
                  </td>
                </tr>
              ))}
              {!carregando && almoxarifes.length === 0 ? (
                <tr><td colSpan={6} className="px-5 py-12 text-center text-slate-500">Nenhum almoxarife cadastrado.</td></tr>
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
