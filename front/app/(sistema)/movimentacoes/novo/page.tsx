import PageHeader from "@/app/components/PageHeader";
import MovimentacaoForm from "../components/MovimentacaoForm";

export default function NovaMovimentacaoPage() {
  return (
    <section>
      <PageHeader
        titulo="Nova movimentação"
        descricao="Registre uma entrada ou saída. A saída não pode exceder o saldo."
      />
      <MovimentacaoForm />
    </section>
  );
}
