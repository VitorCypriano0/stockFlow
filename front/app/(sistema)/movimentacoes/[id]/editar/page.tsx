import PageHeader from "@/app/components/PageHeader";
import MovimentacaoForm from "../../components/MovimentacaoForm";

interface EditarMovimentacaoPageProps {
  params: Promise<{ id: string }>;
}

export default async function EditarMovimentacaoPage({
  params,
}: EditarMovimentacaoPageProps) {
  const id = Number((await params).id);

  return (
    <section>
      <PageHeader
        titulo="Editar movimentação"
        descricao="Ao salvar, o sistema recalcula o saldo e registra a alteração na auditoria."
      />
      <MovimentacaoForm id={id} />
    </section>
  );
}
