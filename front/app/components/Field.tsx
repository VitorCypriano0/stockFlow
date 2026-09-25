import type { ReactNode } from "react";

export const fieldClasses =
  "mt-1.5 w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100";

interface FieldProps {
  label: string;
  children: ReactNode;
  descricao?: string;
}

export function Field({ label, children, descricao }: FieldProps) {
  return (
    <label className="block text-sm font-semibold text-slate-700">
      {label}
      {children}
      {descricao ? (
        <span className="mt-1 block text-xs font-normal text-slate-500">
          {descricao}
        </span>
      ) : null}
    </label>
  );
}
