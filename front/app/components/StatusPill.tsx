interface StatusPillProps {
  ativo: boolean;
}

export default function StatusPill({ ativo }: StatusPillProps) {
  return (
    <span
      className={
        ativo
          ? "inline-flex rounded-full bg-emerald-100 px-2.5 py-1 text-xs font-semibold text-emerald-800"
          : "inline-flex rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-600"
      }
    >
      {ativo ? "Ativo" : "Inativo"}
    </span>
  );
}
