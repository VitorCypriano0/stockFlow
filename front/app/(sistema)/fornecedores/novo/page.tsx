import PageHeader from "@/app/components/PageHeader";
import FornecedorForm from "../components/FornecedorForm";

export default function NovoFornecedorPage() {
  return (
    <section>
      <PageHeader
        titulo="Novo fornecedor"
        descricao="Cadastre os dados da empresa que fornece os itens."
      />
      <FornecedorForm />
    </section>
  );
}
