import PageHeader from "@/app/components/PageHeader";
import AlmoxarifeForm from "../../components/AlmoxarifeForm";

interface EditarAlmoxarifePageProps {
  params: Promise<{ id: string }>;
}

export default async function EditarAlmoxarifePage({
  params,
}: EditarAlmoxarifePageProps) {
  const id = Number((await params).id);

  return (
    <section>
      <PageHeader
        titulo="Editar almoxarife"
        descricao="Atualize os dados do cadastro selecionado."
      />
      <AlmoxarifeForm id={id} />
    </section>
  );
}
