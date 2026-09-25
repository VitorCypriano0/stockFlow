"use client";

import { useEffect, useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { Field, fieldClasses } from "@/app/components/Field";
import FormMessage from "@/app/components/FormMessage";
import { api, mensagemDeErro } from "@/app/lib/api";
import type { Fornecedor, FornecedorInput } from "@/app/types/stockflow";

interface FornecedorFormProps {
  id?: number;
}

interface FornecedorFormState {
  nome: string;
  cnpj: string;
  contato: string;
  telefone: string;
  email: string;
}

const formularioVazio: FornecedorFormState = {
  nome: "",
  cnpj: "",
  contato: "",
  telefone: "",
  email: "",
};

export default function FornecedorForm({ id }: FornecedorFormProps) {
  const router = useRouter();
  const [formulario, setFormulario] =
    useState<FornecedorFormState>(formularioVazio);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(id !== undefined);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    if (id === undefined) return;

    api
      .get<Fornecedor>("/fornecedores/" + id)
      .then(({ data }) => {
        setFormulario({
          nome: data.nome,
          cnpj: data.cnpj ?? "",
          contato: data.contato ?? "",
          telefone: data.telefone ?? "",
          email: data.email ?? "",
        });
      })
      .catch((error: unknown) => {
        setErro(mensagemDeErro(error, "Não foi possível carregar o fornecedor."));
      })
      .finally(() => setCarregando(false));
  }, [id]);

  async function salvar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");
    setSalvando(true);

    const dados: FornecedorInput = {
      nome: formulario.nome.trim(),
      cnpj: formulario.cnpj.trim() || null,
      contato: formulario.contato.trim() || null,
      telefone: formulario.telefone.trim() || null,
      email: formulario.email.trim() || null,
    };

    try {
      if (id === undefined) {
        await api.post("/fornecedores", dados);
      } else {
        await api.put("/fornecedores/" + id, dados);
      }
      router.push("/fornecedores");
      router.refresh();
    } catch (error) {
      setErro(mensagemDeErro(error, "Não foi possível salvar o fornecedor."));
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
      <div className="grid gap-5 sm:grid-cols-2">
        <Field label="Nome do fornecedor">
          <input
            className={fieldClasses}
            value={formulario.nome}
            onChange={(event) =>
              setFormulario({ ...formulario, nome: event.target.value })
            }
            required
          />
        </Field>
        <Field label="CNPJ" descricao="Opcional">
          <input
            className={fieldClasses}
            value={formulario.cnpj}
            onChange={(event) =>
              setFormulario({ ...formulario, cnpj: event.target.value })
            }
            placeholder="00.000.000/0000-00"
          />
        </Field>
        <Field label="Pessoa de contato" descricao="Opcional">
          <input
            className={fieldClasses}
            value={formulario.contato}
            onChange={(event) =>
              setFormulario({ ...formulario, contato: event.target.value })
            }
          />
        </Field>
        <Field label="Telefone" descricao="Opcional">
          <input
            className={fieldClasses}
            type="tel"
            value={formulario.telefone}
            onChange={(event) =>
              setFormulario({ ...formulario, telefone: event.target.value })
            }
          />
        </Field>
        <Field label="E-mail" descricao="Opcional">
          <input
            className={fieldClasses}
            type="email"
            value={formulario.email}
            onChange={(event) =>
              setFormulario({ ...formulario, email: event.target.value })
            }
          />
        </Field>
      </div>

      {erro ? (
        <div className="mt-5">
          <FormMessage texto={erro} />
        </div>
      ) : null}

      <div className="mt-7 flex justify-end gap-3 border-t border-slate-100 pt-5">
        <button
          type="button"
          onClick={() => router.push("/fornecedores")}
          className="min-h-10 rounded-xl border border-slate-300 px-4 text-sm font-semibold text-slate-700 hover:bg-slate-50"
        >
          Cancelar
        </button>
        <button
          type="submit"
          disabled={salvando}
          className="min-h-10 rounded-xl bg-emerald-800 px-5 text-sm font-semibold text-white hover:bg-emerald-900 disabled:cursor-wait disabled:opacity-60"
        >
          {salvando ? "Salvando..." : "Salvar fornecedor"}
        </button>
      </div>
    </form>
  );
}
