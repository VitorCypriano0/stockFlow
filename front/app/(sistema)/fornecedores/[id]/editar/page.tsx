import PageHeader from "@/app/components/PageHeader";
import FornecedorForm from "../../components/FornecedorForm";

interface EditarFornecedorPageProps {
  params: Promise<{ id: string }>;
}

export default async function EditarFornecedorPage({
  params,
}: EditarFornecedorPageProps) {
  const id = Number((await params).id);

  return (
    <section>
      <PageHeader
        titulo="Editar fornecedor"
        descricao="Atualize as informações do fornecedor selecionado."
      />
      <FornecedorForm id={id} />
    </section>
  );
}
