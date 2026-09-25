"use client";

import { useEffect, useState, type FormEvent } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Field, fieldClasses } from "@/app/components/Field";
import FormMessage from "@/app/components/FormMessage";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { Fornecedor, Item, ItemInput } from "@/app/types/stockflow";

interface ItemFormProps {
  id?: number;
}

interface ItemFormState {
  codigo: string;
  nome: string;
  descricao: string;
  unidade: string;
  estoqueMinimo: string;
  fornecedorId: string;
}

const formularioVazio: ItemFormState = {
  codigo: "",
  nome: "",
  descricao: "",
  unidade: "UN",
  estoqueMinimo: "0",
  fornecedorId: "",
};

export default function ItemForm({ id }: ItemFormProps) {
  const router = useRouter();
  const [formulario, setFormulario] = useState<ItemFormState>(formularioVazio);
  const [fornecedores, setFornecedores] = useState<Fornecedor[]>([]);
  const [saldoAtual, setSaldoAtual] = useState<number | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    let ativo = true;

    async function carregar() {
      try {
        const [fornecedoresResponse, itemResponse] = await Promise.all([
          api.get<Fornecedor[]>("/fornecedores"),
          id === undefined ? Promise.resolve(null) : api.get<Item>("/itens/" + id),
        ]);
        if (!ativo) return;

        setFornecedores(fornecedoresResponse.data.filter((fornecedor) => fornecedor.ativo));
        if (itemResponse) {
          const item = itemResponse.data;
          setFormulario({
            codigo: item.codigo,
            nome: item.nome,
            descricao: item.descricao ?? "",
            unidade: item.unidade,
            estoqueMinimo: String(item.estoqueMinimo),
            fornecedorId: String(item.fornecedorId),
          });
          setSaldoAtual(item.quantidadeEstoque);
        }
      } catch (error) {
        if (ativo) {
          setErro(mensagemDeErro(error, "Não foi possível carregar os dados do item."));
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

    const dados: ItemInput = {
      codigo: formulario.codigo.trim(),
      nome: formulario.nome.trim(),
      descricao: formulario.descricao.trim() || null,
      unidade: formulario.unidade.trim(),
      estoqueMinimo: Number(formulario.estoqueMinimo),
      fornecedorId: Number(formulario.fornecedorId),
    };

    try {
      if (id === undefined) {
        await api.post("/itens", dados);
      } else {
        await api.put("/itens/" + id, dados);
      }
      router.push("/itens");
      router.refresh();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível salvar o item."));
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return <p className="text-sm text-slate-600">Carregando cadastro...</p>;
  }

  return (
    <form
      onSubmit={salvar}
      className="max-w-3xl rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7"
    >
      {fornecedores.length === 0 ? (
        <div className="mb-5 rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900">
          Cadastre um fornecedor ativo antes de incluir um item.{" "}
          <Link className="font-bold underline" href="/fornecedores/novo">
            Cadastrar fornecedor
          </Link>
        </div>
      ) : null}

      <div className="grid gap-5 sm:grid-cols-2">
        <Field label="Código do item">
          <input
            className={fieldClasses}
            value={formulario.codigo}
            onChange={(event) =>
              setFormulario({ ...formulario, codigo: event.target.value })
            }
            required
          />
        </Field>
        <Field label="Nome do item">
          <input
            className={fieldClasses}
            value={formulario.nome}
            onChange={(event) =>
              setFormulario({ ...formulario, nome: event.target.value })
            }
            required
          />
        </Field>
        <Field label="Unidade">
          <input
            className={fieldClasses}
            value={formulario.unidade}
            onChange={(event) =>
              setFormulario({ ...formulario, unidade: event.target.value })
            }
            placeholder="UN, KG, L..."
            required
          />
        </Field>
        <Field label="Estoque mínimo">
          <input
            className={fieldClasses}
            type="number"
            min="0"
            step="1"
            value={formulario.estoqueMinimo}
            onChange={(event) =>
              setFormulario({ ...formulario, estoqueMinimo: event.target.value })
            }
            required
          />
        </Field>
        <Field label="Fornecedor">
          <select
            className={fieldClasses}
            value={formulario.fornecedorId}
            onChange={(event) =>
              setFormulario({ ...formulario, fornecedorId: event.target.value })
            }
            required
          >
            <option value="">Selecione um fornecedor</option>
            {fornecedores.map((fornecedor) => (
              <option key={fornecedor.id} value={fornecedor.id}>
                {fornecedor.nome}
              </option>
            ))}
          </select>
        </Field>
        {saldoAtual !== null ? (
          <Field
            label="Saldo atual"
            descricao="O saldo é alterado pelas movimentações de entrada e saída."
          >
            <input
              className={fieldClasses + " bg-slate-50 text-slate-500"}
              value={saldoAtual}
              readOnly
            />
          </Field>
        ) : null}
        <div className="sm:col-span-2">
          <Field label="Descrição" descricao="Opcional">
            <textarea
              className={fieldClasses}
              rows={3}
              value={formulario.descricao}
              onChange={(event) =>
                setFormulario({ ...formulario, descricao: event.target.value })
              }
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
          onClick={() => router.push("/itens")}
          className="min-h-10 rounded-xl border border-slate-300 px-4 text-sm font-semibold text-slate-700 hover:bg-slate-50"
        >
          Cancelar
        </button>
        <button
          type="submit"
          disabled={salvando || fornecedores.length === 0}
          className="min-h-10 rounded-xl bg-emerald-800 px-5 text-sm font-semibold text-white hover:bg-emerald-900 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {salvando ? "Salvando..." : "Salvar item"}
        </button>
      </div>
    </form>
  );
}
