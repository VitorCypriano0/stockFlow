import Link from "next/link";

interface PageHeaderProps {
  titulo: string;
  descricao: string;
  acaoHref?: string;
  acaoTexto?: string;
}

export default function PageHeader({
  titulo,
  descricao,
  acaoHref,
  acaoTexto,
}: PageHeaderProps) {
  return (
    <div className="mb-7 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p className="mb-2 text-xs font-bold uppercase tracking-[0.18em] text-emerald-700">
          Stock Flow
        </p>
        <h1 className="text-3xl font-semibold tracking-tight text-slate-900">
          {titulo}
        </h1>
        <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
          {descricao}
        </p>
      </div>
      {acaoHref && acaoTexto ? (
        <Link
          href={acaoHref}
          className="inline-flex min-h-11 items-center justify-center rounded-xl bg-emerald-800 px-5 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-900"
        >
          {acaoTexto}
        </Link>
      ) : null}
    </div>
  );
}
