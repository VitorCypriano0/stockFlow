import PageHeader from "@/app/components/PageHeader";
import ItemForm from "../../components/ItemForm";

interface EditarItemPageProps {
  params: Promise<{ id: string }>;
}

export default async function EditarItemPage({ params }: EditarItemPageProps) {
  const id = Number((await params).id);

  return (
    <section>
      <PageHeader
        titulo="Editar item"
        descricao="Atualize os dados de cadastro. O saldo só muda por movimentação."
      />
      <ItemForm id={id} />
    </section>
  );
}
