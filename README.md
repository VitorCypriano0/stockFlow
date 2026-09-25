# Stock Flow

Stock Flow é um sistema acadêmico para controle de almoxarifado. A aplicação registra almoxarifes, fornecedores, itens e movimentações de entrada e saída.

Os campos escolhidos para esta versão são:

- **Almoxarife:** nome, CPF, e-mail e telefone.
- **Fornecedor:** nome, CNPJ, contato, telefone e e-mail.
- **Item:** código, nome, descrição, unidade, estoque mínimo, saldo atual e fornecedor.
- **Movimentação:** tipo, item, quantidade, almoxarife responsável, data e hora e observação.

O requisito não pede autenticação. Por isso, a movimentação identifica o responsável pelo almoxarife selecionado no formulário.

## O que o sistema faz

- Mantém cadastros de almoxarifes, fornecedores e itens.
- Atualiza o saldo do item por meio de movimentações.
- Bloqueia uma saída quando a quantidade pedida passa do saldo disponível.
- Registra data, hora e almoxarife em cada inclusão, edição ou cancelamento de movimentação.
- Cancela movimentações sem apagar o registro de auditoria e ajusta o saldo de volta.
- Desativa cadastros relacionados a itens ou movimentações para preservar o histórico.

O saldo não pode ser editado no formulário do item. Para aumentar ou diminuir o estoque, registre uma movimentação. Ao cadastrar uma movimentação, selecione o almoxarife responsável; esse nome e o identificador ficam guardados no histórico.

## Como o projeto está organizado

O backend usa Spring Boot e segue o caminho controller, service, repository e banco:

- **Controller** recebe a requisição HTTP e devolve a resposta.
- **Service** valida regras de negócio e coordena as alterações.
- **Repository** usa Spring Data JPA para consultar e salvar entidades.
- **Entity** representa uma tabela do banco.
- **DTO** define os dados aceitos e devolvidos pela API.

O frontend usa Next.js com TypeScript. As páginas de listagem carregam os dados da API; os formulários enviam inclusão e edição. A URL da API fica centralizada em front/app/lib/api.ts.

## Banco de dados

Por padrão, a aplicação usa H2 em memória para que o CRUD funcione localmente sem instalar um banco. Os dados desse modo são apagados quando o backend é reiniciado. A dependência do PostgreSQL já está disponível para quando o banco definitivo for escolhido.

O backend aceita estas variáveis de ambiente:

- DATABASE_URL: URL JDBC completa. Se não for definida, usa jdbc:h2:mem:stockflow;DB_CLOSE_DELAY=-1.
- DATABASE_USERNAME: usuário do banco. O padrão do H2 é sa.
- DATABASE_PASSWORD: senha do banco. O padrão do H2 é vazio.

Para abrir o console do H2 durante o desenvolvimento, use http://localhost:8080/h2-console e informe a URL jdbc:h2:mem:stockflow;DB_CLOSE_DELAY=-1.

## Executar no Windows

Requisitos: JDK 17 com `JAVA_HOME` configurado, Node.js 20.9 ou superior e npm.

Abra um terminal na pasta do projeto e inicie o backend:

    .\mvnw.cmd spring-boot:run

Em outro terminal, inicie o frontend:

    cd front
    npm.cmd install
    npm.cmd run dev

Acesse http://localhost:3000. Antes de registrar uma movimentação, cadastre pelo menos um almoxarife, um fornecedor e um item.

Se a API estiver em outro endereço, copie front/.env.example para front/.env.local e ajuste NEXT_PUBLIC_API_URL.

## Rotas da API

| Recurso | Rotas |
| --- | --- |
| Almoxarifes | /api/almoxarifes |
| Fornecedores | /api/fornecedores |
| Itens | /api/itens |
| Movimentações | /api/movimentacoes |
| Auditoria | /api/historico |

Cada cadastro oferece GET, POST, PUT e DELETE. Exclusões de almoxarife, fornecedor e item são desativações; exclusão de movimentação a cancela e mantém a auditoria.
