import PageHeader from "@/app/components/PageHeader";
import AlmoxarifeForm from "../components/AlmoxarifeForm";

export default function NovoAlmoxarifePage() {
  return (
    <section>
      <PageHeader
        titulo="Novo almoxarife"
        descricao="Cadastre a pessoa responsável por registrar movimentações."
      />
      <AlmoxarifeForm />
    </section>
  );
}
