# Stock Flow

Sistema acadêmico para controle de almoxarifado. A aplicação mantém almoxarifes, fornecedores e itens, registra entradas e saídas e guarda auditoria das movimentações.

## Organização

O backend usa Spring Boot com a separação controller, service, repository, entity e DTO. Os serviços dependem das interfaces de repositório do domínio; adaptadores JPA na infraestrutura implementam essas portas. O frontend usa Next.js, TypeScript, Tailwind CSS e Axios. As páginas de listagem consultam a API e mostram dados reais nestas rotas:

- `/almoxarifes`: responsáveis pelas movimentações.
- `/fornecedores`: empresas fornecedoras.
- `/itens`: cadastro e saldo atual.
- `/movimentacoes`: entradas e saídas do estoque.
- `/historico`: auditoria das alterações.

## Banco de dados e segredo do JWT

O backend usa PostgreSQL e espera o banco `cafeteria` disponível em `localhost`. Não mantenha a senha do banco nem a chave de assinatura JWT no Git.

1. Copie `.env.example` para `.env` na raiz.
2. Ajuste `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` para o PostgreSQL local.
3. Gere uma chave aleatória com pelo menos 32 bytes e coloque o resultado em `JWT_SECRET`. No PowerShell, gere uma chave com:

       [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))

O Spring Boot carrega esse `.env` local. O Hibernate atualiza o esquema com `ddl-auto=update`, e o SQL fica visível no console durante o desenvolvimento. PostgreSQL é selecionado automaticamente pelo driver; não configure `HSQLDialect`, que corresponde a outro banco.

## Login e autenticação

Abra `/cadastro` no frontend para criar uma empresa e sua primeira conta administradora. Um registro antigo ainda sem senha pode definir uma pela primeira vez informando o mesmo e-mail e CPF. Depois, acesse `/login`.

- `POST /api/auth/registrar`: cria uma empresa com sua primeira conta administradora ou ativa um registro antigo sem senha.
- `POST /api/auth/login`: valida e-mail e senha e devolve um token JWT do tipo Bearer.
- `POST /api/bootstrap/admin`: cria a primeira empresa e o primeiro administrador usando a chave `APP_BOOTSTRAP_KEY`; só fica disponível enquanto não houver administrador no banco.
- As rotas de negócio exigem `Authorization: Bearer <token>` e isolam os dados por empresa. O gerenciamento de almoxarifes é restrito a administradores.

As senhas são gravadas como hash BCrypt. O token tem duração configurável em `JWT_EXPIRATION_MINUTES` (padrão: 60 minutos). A chave JWT deve ser a mesma enquanto houver tokens que precisem continuar válidos.

No frontend, o token e os dados do usuário ficam na store Redux em memória; atualizar ou fechar a página encerra a sessão local e exige novo login. O estado do painel também é gerenciado no Redux. O serviço Axios injeta o token nas chamadas protegidas e encerra a sessão ao receber HTTP 401.

### Configuração inicial pela aplicação JavaFX

Com a API em execução, configure `APP_BOOTSTRAP_KEY` no `.env` com pelo menos 32 caracteres e reinicie o backend. Na pasta `desktop`, rode `mvn javafx:run` e informe a mesma chave e os dados da empresa e do administrador. Após a criação, remova a chave do ambiente e reinicie a API. Veja [desktop/README.md](desktop/README.md) para detalhes.

## Documentação da API

Com o backend ativo, a interface Swagger fica em <http://localhost:8080/swagger-ui.html>.

### Demonstrar o CRUD pela Swagger UI

1. Abra `POST /api/auth/registrar` e crie uma conta de almoxarife, ou use uma conta já cadastrada.
2. Execute `POST /api/auth/login` com o e-mail e a senha. Copie o valor de `token` da resposta.
3. Clique em **Authorize**, cole o token e confirme. A interface envia o cabeçalho `Authorization: Bearer ...` nas rotas protegidas.
4. Para demonstrar o CRUD de itens, crie primeiro um fornecedor em `POST /api/fornecedores` e use o `id` retornado em `fornecedorId` no `POST /api/itens`.
5. Use `GET /api/itens` para listar e `PUT /api/itens/{id}` para editar o item. A resposta de criação informa o `id` para as próximas operações.

O botão **Authorize** envia o JWT apenas nas operações marcadas como protegidas. O endpoint de login permanece público para permitir obter o token.

## Executar no Windows

Requisitos: JDK 17, PostgreSQL, Node.js 20.9 ou superior e npm.

1. Configure `.env` como descrito acima e confirme que o banco `cafeteria` existe.
2. Inicie o backend na raiz do projeto:

       .\mvnw.cmd spring-boot:run

3. Em outro terminal, configure o endereço da API para o frontend e inicie o Next.js:

       Copy-Item front\.env.example front\.env.local
       cd front
       npm.cmd install
       npm.cmd run dev

4. Acesse <http://localhost:3000>, crie ou ative a conta e faça login.

O CORS do backend libera `http://localhost:3000` para as rotas `/api/**`.
