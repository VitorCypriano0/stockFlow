import PageHeader from "@/app/components/PageHeader";
import ItemForm from "../components/ItemForm";

export default function NovoItemPage() {
  return (
    <section>
      <PageHeader
        titulo="Novo item"
        descricao="Cadastre um item e associe-o a um fornecedor."
      />
      <ItemForm />
    </section>
  );
}
