package br.com.stockflow.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockFlowBootstrapApp extends Application {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private TextField apiUrlField;
    private TextField empresaNomeField;
    private TextField nomeField;
    private TextField cpfField;
    private TextField emailField;
    private TextField telefoneField;
    private PasswordField senhaField;
    private PasswordField chaveField;
    private Button criarButton;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        apiUrlField = textField("http://localhost:8080");
        empresaNomeField = textField("Nome da empresa");
        nomeField = textField("Nome completo");
        cpfField = textField("Somente números ou CPF formatado");
        emailField = textField("admin@empresa.com");
        telefoneField = textField("Opcional");
        senhaField = new PasswordField();
        senhaField.setPromptText("Mínimo de 8 caracteres");
        chaveField = new PasswordField();
        chaveField.setPromptText("Chave APP_BOOTSTRAP_KEY do servidor");
        criarButton = new Button("Criar empresa e administrador");
        statusLabel = new Label("A chave só é enviada no cabeçalho da chamada ao servidor.");

        VBox content = new VBox(10,
                title("Configuração inicial do StockFlow"),
                new Label("Endereço da API"), apiUrlField,
                new Label("Empresa"), empresaNomeField,
                new Label("Administrador inicial"), nomeField,
                new Label("CPF"), cpfField,
                new Label("E-mail"), emailField,
                new Label("Telefone"), telefoneField,
                new Label("Senha do administrador"), senhaField,
                new Label("Chave de bootstrap"), chaveField,
                criarButton,
                statusLabel
        );
        content.setPadding(new Insets(24));
        content.setMaxWidth(560);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        criarButton.setDefaultButton(true);
        criarButton.setOnAction(event -> enviarFormulario());

        stage.setTitle("StockFlow — Configuração inicial");
        stage.setMinWidth(480);
        stage.setMinHeight(620);
        stage.setScene(new Scene(scrollPane, 600, 760));
        stage.show();
    }

    private void enviarFormulario() {
        BootstrapForm formulario;
        try {
            formulario = lerFormulario();
        } catch (IllegalArgumentException erro) {
            mostrarErro(erro.getMessage());
            return;
        }

        criarButton.setDisable(true);
        statusLabel.setText("Enviando solicitação…");
        Task<BootstrapResult> task = new Task<>() {
            @Override
            protected BootstrapResult call() throws Exception {
                return criarAdministrador(formulario);
            }
        };
        task.setOnSucceeded(event -> {
            BootstrapResult resultado = task.getValue();
            statusLabel.setText("Configuração concluída para " + resultado.empresaNome() + ".");
            senhaField.clear();
            chaveField.clear();
            criarButton.setDisable(false);
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Bootstrap concluído");
            alerta.setHeaderText("Empresa e administrador criados");
            alerta.setContentText("Acesse o StockFlow com " + resultado.administradorEmail() + ".");
            alerta.showAndWait();
        });
        task.setOnFailed(event -> {
            criarButton.setDisable(false);
            String mensagem = task.getException() == null
                    ? "Não foi possível concluir o bootstrap."
                    : task.getException().getMessage();
            statusLabel.setText("Falha ao criar a configuração inicial.");
            mostrarErro(mensagem == null ? "Não foi possível concluir o bootstrap." : mensagem);
        });
        Thread thread = new Thread(task, "stockflow-bootstrap-request");
        thread.setDaemon(true);
        thread.start();
    }

    private BootstrapForm lerFormulario() {
        String apiUrl = apiUrlField.getText().trim();
        String empresa = empresaNomeField.getText().trim();
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String email = emailField.getText().trim();
        String senha = senhaField.getText();
        String chave = chaveField.getText();

        if (apiUrl.isBlank() || empresa.isBlank() || nome.isBlank() || cpf.isBlank()
                || email.isBlank() || senha.isBlank() || chave.isBlank()) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
        }
        if (senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        return new BootstrapForm(
                apiUrl, empresa, nome, cpf, email,
                telefoneField.getText().trim(), senha, chave
        );
    }

    private BootstrapResult criarAdministrador(BootstrapForm formulario) throws Exception {
        String baseUrl = formulario.apiUrl().replaceAll("/+$", "");
        String endpoint = baseUrl.endsWith("/api")
                ? baseUrl + "/bootstrap/admin"
                : baseUrl + "/api/bootstrap/admin";
        URI uri = URI.create(endpoint);
        String json = objectMapper.writeValueAsString(new BootstrapRequestBody(
                formulario.empresaNome(), formulario.nome(), formulario.cpf(),
                formulario.email(), formulario.telefone(), formulario.senha()
        ));
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-Bootstrap-Key", formulario.chave())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = response.body().isBlank() ? objectMapper.createObjectNode()
                : objectMapper.readTree(response.body());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(mensagemErro(body, response.statusCode()));
        }
        return new BootstrapResult(
                body.path("empresaNome").asText("empresa"),
                body.path("administradorEmail").asText(formulario.email())
        );
    }

    private String mensagemErro(JsonNode body, int status) {
        if (body.hasNonNull("mensagem")) {
            return body.get("mensagem").asText();
        }
        if (body.hasNonNull("message")) {
            return body.get("message").asText();
        }
        return switch (status) {
            case 401 -> "A chave de bootstrap informada não é válida.";
            case 409 -> "Já existe um administrador no sistema. O bootstrap só funciona uma vez.";
            case 503 -> "O servidor está com o bootstrap desabilitado. Configure APP_BOOTSTRAP_KEY.";
            default -> "O servidor respondeu com erro HTTP " + status + ". Verifique o endereço e os dados enviados.";
        };
    }

    private TextField textField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        return field;
    }

    private Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        return label;
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Não foi possível concluir");
        alerta.setHeaderText("Falha na configuração inicial");
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private record BootstrapForm(
            String apiUrl,
            String empresaNome,
            String nome,
            String cpf,
            String email,
            String telefone,
            String senha,
            String chave
    ) {
    }

    private record BootstrapRequestBody(
            String empresaNome,
            String nome,
            String cpf,
            String email,
            String telefone,
            String senha
    ) {
    }

    private record BootstrapResult(String empresaNome, String administradorEmail) {
    }
}
