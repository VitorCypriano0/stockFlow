import Link from "next/link";

const links = [
  { href: "/home", texto: "Visão geral" },
  { href: "/almoxarifes", texto: "Almoxarifes" },
  { href: "/fornecedores", texto: "Fornecedores" },
  { href: "/itens", texto: "Itens" },
  { href: "/movimentacoes", texto: "Movimentações" },
  { href: "/historico", texto: "Auditoria" },
];

export default function Sidebar() {
  return (
    <aside className="w-full shrink-0 border-b border-emerald-950/10 bg-emerald-950 px-4 py-5 text-white md:min-h-screen md:w-64 md:border-b-0 md:border-r md:px-5">
      <Link href="/home" className="mb-5 flex items-center gap-3 md:mb-10">
        <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-700 text-lg font-bold">
          S
        </span>
        <span>
          <strong className="block text-lg tracking-tight">Stock Flow</strong>
          <small className="text-emerald-200">Controle de almoxarifado</small>
        </span>
      </Link>
      <p className="mb-3 px-2 text-[11px] font-bold uppercase tracking-[0.16em] text-emerald-300">
        Gestão
      </p>
      <nav className="grid grid-cols-2 gap-1.5 sm:grid-cols-3 md:flex md:flex-col">
        {links.map((link) => (
          <Link
            key={link.href}
            href={link.href}
            className="rounded-lg px-3 py-2.5 text-sm font-medium text-emerald-50 transition hover:bg-emerald-900"
          >
            {link.texto}
          </Link>
        ))}
      </nav>
    </aside>
  );
}
