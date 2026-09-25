import Link from "next/link";

const destaques = [
  {
    titulo: "Cadastro organizado",
    texto: "Mantenha almoxarifes, fornecedores e itens no mesmo lugar.",
  },
  {
    titulo: "Saldo atualizado",
    texto: "Entradas e saídas recalculam o estoque de cada item.",
  },
  {
    titulo: "Auditoria clara",
    texto: "Consulte a data e o almoxarife associados a cada movimentação.",
  },
];

export default function PaginaInicial() {
  return (
    <main className="min-h-screen bg-[#f4f7f5] text-slate-900">
      <header className="mx-auto flex max-w-6xl items-center justify-between px-5 py-6 sm:px-8">
        <Link href="/" className="flex items-center gap-3">
          <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-900 text-lg font-bold text-white">S</span>
          <span className="text-lg font-bold tracking-tight">Stock Flow</span>
        </Link>
        <Link
          href="/home"
          className="rounded-xl bg-emerald-900 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-800"
        >
          Acessar painel
        </Link>
      </header>

      <section className="mx-auto grid max-w-6xl gap-10 px-5 pb-16 pt-12 sm:px-8 md:grid-cols-[1.1fr_0.9fr] md:items-center md:pb-24 md:pt-20">
        <div>
          <p className="mb-4 text-xs font-bold uppercase tracking-[0.2em] text-emerald-700">
            Controle de almoxarifado
          </p>
          <h1 className="max-w-2xl text-4xl font-semibold leading-tight tracking-tight sm:text-5xl">
            Seu estoque em ordem, do recebimento à saída.
          </h1>
          <p className="mt-5 max-w-xl text-base leading-7 text-slate-600">
            O Stock Flow ajuda a acompanhar itens, fornecedores e movimentações
            com um histórico simples de consultar.
          </p>
          <Link
            href="/home"
            className="mt-8 inline-flex rounded-xl bg-emerald-900 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-800"
          >
            Abrir visão geral
          </Link>
        </div>
        <div className="rounded-3xl bg-emerald-950 p-7 text-white shadow-xl sm:p-9">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-emerald-300">No dia a dia</p>
          <h2 className="mt-4 text-2xl font-semibold">Uma movimentação de cada vez.</h2>
          <p className="mt-3 text-sm leading-6 text-emerald-100">
            Cada registro atualiza o saldo e guarda quem o realizou. O sistema
            também impede uma saída maior que o estoque disponível.
          </p>
          <div className="mt-8 flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 p-4">
            <span className="flex h-10 w-10 items-center justify-center rounded-full bg-emerald-700 text-lg">✓</span>
            <span className="text-sm font-medium">Saldo validado antes da saída</span>
          </div>
        </div>
      </section>

      <section className="border-t border-slate-200 bg-white">
        <div className="mx-auto grid max-w-6xl gap-4 px-5 py-12 sm:px-8 md:grid-cols-3 md:py-16">
          {destaques.map((destaque, indice) => (
            <article key={destaque.titulo} className="rounded-2xl border border-slate-200 p-6">
              <span className="text-xs font-bold text-emerald-700">0{indice + 1}</span>
              <h3 className="mt-4 text-lg font-semibold">{destaque.titulo}</h3>
              <p className="mt-2 text-sm leading-6 text-slate-600">{destaque.texto}</p>
            </article>
          ))}
        </div>
      </section>

      <footer className="py-6 text-center text-xs text-slate-500">
        Stock Flow · Projeto acadêmico de controle de almoxarifado
      </footer>
    </main>
  );
}
