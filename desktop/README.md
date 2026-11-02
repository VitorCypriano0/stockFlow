# Configurador JavaFX do StockFlow

Este aplicativo cria a primeira empresa e sua conta administradora por meio da API do StockFlow. Ele não conecta diretamente ao banco de dados.

## Pré-requisitos

- JDK 17 ou superior;
- Maven 3.9 ou superior;
- API do StockFlow em execução;
- variável `APP_BOOTSTRAP_KEY` configurada no ambiente da API com pelo menos 32 caracteres.

## Executar

Na pasta `desktop`, execute:

```powershell
mvn javafx:run
```

Informe a URL da API (por exemplo, `http://localhost:8080`), os dados da empresa e do primeiro administrador, e a mesma chave configurada no servidor. O bootstrap só pode ser concluído quando ainda não existir nenhum administrador. Depois da criação, remova `APP_BOOTSTRAP_KEY` do ambiente e reinicie a API.

O cliente usa Jackson para serializar a requisição JSON e a API Java integrada para HTTP. A chave e a senha são enviadas apenas na chamada HTTPS/HTTP configurada; use HTTPS quando a API estiver fora da máquina local.
