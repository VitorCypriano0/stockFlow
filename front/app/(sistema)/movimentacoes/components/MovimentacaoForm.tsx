"use client";

import Link from "next/link";
import { useEffect, useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { Field, fieldClasses } from "@/app/components/Field";
import FormMessage from "@/app/components/FormMessage";
import { api, mensagemDeErro } from "@/app/lib/api";
import type {
  Item,
  Movimentacao,
  MovimentacaoInput,
  TipoMovimentacao,
} from "@/app/types/stockflow";

interface MovimentacaoFormProps {
  id?: number;
}

interface MovimentacaoFormState {
  itemId: string;
  tipo: TipoMovimentacao;
  quantidade: string;
  observacao: string;
}

const formularioVazio: MovimentacaoFormState = {
  itemId: "",
  tipo: "ENTRADA",
  quantidade: "1",
  observacao: "",
};

export default function MovimentacaoForm({ id }: MovimentacaoFormProps) {
  const router = useRouter();
  const [formulario, setFormulario] =
    useState<MovimentacaoFormState>(formularioVazio);
  const [itens, setItens] = useState<Item[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    let ativo = true;

    async function carregar() {
      try {
        const [itensResponse, movimentacaoResponse] = await Promise.all([
          api.get<Item[]>("/itens"),
          id === undefined
            ? Promise.resolve(null)
            : api.get<Movimentacao>("/movimentacoes/" + id),
        ]);
        if (!ativo) return;

        setItens(itensResponse.data.filter((item) => item.ativo));

        if (movimentacaoResponse) {
          const movimentacao = movimentacaoResponse.data;
          if (!movimentacao.ativa) {
            setErro("Esta movimentação está cancelada e não pode ser alterada.");
          }
          setFormulario({
            itemId: String(movimentacao.itemId),
            tipo: movimentacao.tipo,
            quantidade: String(movimentacao.quantidade),
            observacao: movimentacao.observacao ?? "",
          });
        }
      } catch (error) {
        if (ativo) {
          setErro(
            mensagemDeErro(error, "Não foi possível carregar os dados para a movimentação."),
          );
        }
      } finally {
        if (ativo) setCarregando(false);
      }
    }

    void carregar();
    return () => {
      ativo = false;
    };
  }, [id]);

  async function salvar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");
    setSalvando(true);

    const dados: MovimentacaoInput = {
      itemId: Number(formulario.itemId),
      tipo: formulario.tipo,
      quantidade: Number(formulario.quantidade),
      observacao: formulario.observacao.trim() || null,
    };

    try {
      if (id === undefined) {
        await api.post("/movimentacoes", dados);
      } else {
        await api.put("/movimentacoes/" + id, dados);
      }
      router.push("/movimentacoes");
      router.refresh();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível registrar a movimentação."));
    } finally {
      setSalvando(false);
    }
  }

  const itemSelecionado = itens.find(
    (item) => item.id === Number(formulario.itemId),
  );
  const bloqueado =
    salvando ||
    Boolean(erro && id !== undefined && erro.includes("cancelada")) ||
    itens.length === 0;

  if (carregando) {
    return <p className="text-sm text-slate-600">Carregando dados...</p>;
  }

  return (
    <form
      onSubmit={salvar}
      className="max-w-3xl rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7"
    >
      {itens.length === 0 ? (
        <div className="mb-5 rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900">
          Para registrar uma movimentação, cadastre pelo menos um item ativo.{" "}
          <Link className="font-bold underline" href="/itens/novo">Cadastrar item</Link>
        </div>
      ) : null}

      <p className="mb-5 rounded-xl bg-slate-50 px-4 py-3 text-sm text-slate-600">
        O almoxarife responsável será identificado pela conta autenticada.
      </p>

      <div className="grid gap-5 sm:grid-cols-2">
        <Field label="Tipo de movimentação">
          <select
            className={fieldClasses}
            value={formulario.tipo}
            onChange={(event) =>
              setFormulario({
                ...formulario,
                tipo: event.target.value as TipoMovimentacao,
              })
            }
            required
          >
            <option value="ENTRADA">Entrada</option>
            <option value="SAIDA">Saída</option>
          </select>
        </Field>
        <Field label="Item">
          <select
            className={fieldClasses}
            value={formulario.itemId}
            onChange={(event) =>
              setFormulario({ ...formulario, itemId: event.target.value })
            }
            required
          >
            <option value="">Selecione um item</option>
            {itens.map((item) => (
              <option key={item.id} value={item.id}>
                {item.codigo} · {item.nome}
              </option>
            ))}
          </select>
        </Field>
        <Field label="Quantidade">
          <input
            className={fieldClasses}
            type="number"
            min="1"
            step="1"
            value={formulario.quantidade}
            onChange={(event) =>
              setFormulario({ ...formulario, quantidade: event.target.value })
            }
            required
          />
        </Field>
        {itemSelecionado ? (
          <div className="rounded-xl bg-emerald-50 px-4 py-3 text-sm text-emerald-900 sm:col-span-2">
            Saldo atual de <strong>{itemSelecionado.nome}</strong>:{" "}
            <strong>{itemSelecionado.quantidadeEstoque} {itemSelecionado.unidade}</strong>
          </div>
        ) : null}
        <div className="sm:col-span-2">
          <Field label="Observação" descricao="Opcional">
            <textarea
              className={fieldClasses}
              rows={3}
              value={formulario.observacao}
              onChange={(event) =>
                setFormulario({ ...formulario, observacao: event.target.value })
              }
              placeholder="Ex.: nota fiscal, setor de destino ou motivo da movimentação"
            />
          </Field>
        </div>
      </div>

      {erro ? (
        <div className="mt-5">
          <FormMessage texto={erro} />
        </div>
      ) : null}

      <div className="mt-7 flex justify-end gap-3 border-t border-slate-100 pt-5">
        <button
          type="button"
          onClick={() => router.push("/movimentacoes")}
          className="min-h-10 rounded-xl border border-slate-300 px-4 text-sm font-semibold text-slate-700 hover:bg-slate-50"
        >
          Cancelar
        </button>
        <button
          type="submit"
          disabled={bloqueado}
          className="min-h-10 rounded-xl bg-emerald-800 px-5 text-sm font-semibold text-white hover:bg-emerald-900 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {salvando ? "Registrando..." : "Salvar movimentação"}
        </button>
      </div>
    </form>
  );
}
