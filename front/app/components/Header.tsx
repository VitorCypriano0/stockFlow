export default function Header() {
  return (
    <header className="flex min-h-16 items-center justify-between border-b border-slate-200 bg-white px-5 sm:px-8">
      <div>
        <p className="text-sm font-semibold text-slate-800">
          Painel do almoxarifado
        </p>
        <p className="text-xs text-slate-500">
          Organize entradas, saídas e saldo dos itens
        </p>
      </div>
      <span className="hidden rounded-full bg-emerald-50 px-3 py-1.5 text-xs font-semibold text-emerald-800 sm:inline-flex">
        Projeto acadêmico
      </span>
    </header>
  );
}
