interface StatusPillProps {
  ativo: boolean;
  bloqueado?: boolean;
}

export default function StatusPill({ ativo, bloqueado = false }: StatusPillProps) {
  const texto = !ativo ? "Inativo" : bloqueado ? "Bloqueado" : "Ativo";
  const classes = !ativo
    ? "bg-slate-100 text-slate-600"
    : bloqueado
      ? "bg-amber-100 text-amber-800"
      : "bg-emerald-100 text-emerald-800";

  return (
    <span
      className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${classes}`}
    >
      {texto}
    </span>
  );
}
