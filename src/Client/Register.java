package Client;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe responsável por gerir as ações da janela "Registrar"
 */
public class Register extends Application {
    private Label titleContent, textName, textPassword, textUsername, textLocalizacao, textLatitude, textLongitude, textTemConta;
    private TextField inputName, inputPass, inputUserName, inputLatitude, inputLongitude;
    private Button register, login;
    private Scene registerPage;
    private Stage registerPageWindow;
    //DIMENSOES
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 550;


    private PrintWriter out;
    private BufferedReader in;

    private Socket socket;

    /**
     * Exibe um alerta ao utilizador da aplicação de que o input não é válido
     */
    public void displayMSG(int op) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        switch (op) {
            case 1:
                alert.setTitle("Erro");
                alert.setHeaderText("Falha no Registo!");
                alert.setContentText("Tem campos invalidos");
                break;
            case 2:
                alert.setTitle("Erro");
                alert.setHeaderText("Falha no Registo!");
                alert.setContentText("Username já existente");
                break;
            case 3:
                alert.setTitle("Sucesso");
                alert.setHeaderText("Conta registada");
                alert.setContentText("Conta registada com sucesso, pode efetuar login!");
                break;
        }
        alert.showAndWait();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.registerPageWindow = primaryStage;

        this.socket = new Socket("localhost", 2048);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        setOnCloseRequest();
        //Butoes
        styleButtons();
        listennerActionButtons();

        initializeLabels();
        ///Layout
        HBox hBoxId = new HBox(10);
        hBoxId.setAlignment(Pos.CENTER);
        hBoxId.getChildren().addAll(this.textName, this.inputUserName);

        // Login Container
        HBox hBoxLoginUsername = new HBox(10);
        hBoxLoginUsername.setAlignment(Pos.CENTER);
        hBoxLoginUsername.getChildren().addAll(this.textUsername, this.inputUserName);

        // Nome Container
        HBox hBoxLoginName = new HBox(10);
        hBoxLoginName.setAlignment(Pos.CENTER);
        hBoxLoginName.getChildren().addAll(this.textName, this.inputName);

        HBox hBoxLoginPass = new HBox(10);
        hBoxLoginPass.setAlignment(Pos.CENTER);
        hBoxLoginPass.getChildren().addAll(this.textPassword, this.inputPass);
        ///destaque localização
        HBox hlocalizacao = new HBox(10);
        hlocalizacao.setAlignment(Pos.CENTER);
        hlocalizacao.getChildren().addAll(this.textLocalizacao);

        ///input  latitude
        HBox hBoxinputLatitude = new HBox(10);
        hBoxinputLatitude.setAlignment(Pos.CENTER);
        hBoxinputLatitude.getChildren().addAll(this.textLatitude, this.inputLatitude);
        ///input  longitude
        HBox hBoxinputLongitude = new HBox(10);
        hBoxinputLongitude.setAlignment(Pos.CENTER);
        hBoxinputLongitude.getChildren().addAll(this.textLongitude, this.inputLongitude);

        // BOTAO REGISTAR
        HBox hBoxButton = new HBox(10);
        hBoxButton.setAlignment(Pos.CENTER);
        hBoxButton.setPadding(new Insets(20, 20, 30, 40));
        hBoxButton.getChildren().add(this.register);
        // BOTAO MUDAR PRA LOGIN
        HBox hBoxButtonLogin = new HBox(10);
        hBoxButtonLogin.setAlignment(Pos.CENTER);
        hBoxButtonLogin.setPadding(new Insets(0, 25, 60, 40));
        hBoxButtonLogin.getChildren().add(this.login);

        // Mensagem Possui Conta
        HBox hBoxMsgPossuiConta = new HBox(10);
        hBoxMsgPossuiConta.setAlignment(Pos.CENTER);
        hBoxMsgPossuiConta.setPadding(new Insets(0, 25, 20, 40));
        hBoxMsgPossuiConta.getChildren().add(this.textTemConta);

        //Vboxs
        VBox vBoxTitle = new VBox();
        vBoxTitle.setAlignment(Pos.CENTER);
        vBoxTitle.getChildren().addAll(this.titleContent);

        VBox vBoxForm = new VBox(10);
        vBoxForm.setAlignment(Pos.CENTER);
        vBoxForm.getChildren().addAll(hBoxId, hBoxLoginUsername, hBoxLoginName, hBoxLoginPass, hlocalizacao, hBoxinputLatitude, hBoxinputLongitude);

        VBox vBoxButtons = new VBox();
        vBoxButtons.setAlignment(Pos.CENTER);
        vBoxButtons.getChildren().addAll(hBoxButton, hBoxMsgPossuiConta, hBoxButtonLogin);


        BorderPane borderLayout = new BorderPane();
        borderLayout.setPadding(new Insets(30, 10, 0, 10));
        borderLayout.setTop(vBoxTitle);
        borderLayout.setCenter(vBoxForm);
        borderLayout.setBottom(vBoxButtons);

        this.registerPage = new Scene(borderLayout, SCENE_WIDTH, SCENE_HEIGHT);
        this.registerPageWindow.setScene(registerPage);
        this.registerPageWindow.show();
    }

    void listennerActionButtons() {
        this.login.setOnAction(e -> {
            Login registerPage = new Login();
            try {
                registerPage.start(registerPageWindow);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        this.register.setOnAction(e -> {

            if (this.inputUserName.getText().isEmpty() || this.inputName.getText().isEmpty() || this.inputPass.getText().isEmpty() || this.inputLatitude.getText().isEmpty() || this.inputLongitude.getText().isEmpty()) {
                displayMSG(1);
            } else {
                out.println("REGISTAR;" + this.inputUserName.getText() + ";" + this.inputName.getText() + ";" + this.inputPass.getText() + ";" + this.inputLatitude.getText() + ";" + this.inputLongitude.getText() + ";");

                int i = 0;
                String inputLine = null;
                //Espera pela resposta
                try {
                    while ((inputLine = in.readLine()) != null) {
                        i++;
                        System.out.println(inputLine);
                        if (i == 1) {
                            break;
                        }
                    }

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                if (inputLine.equals("0")) {
                    displayMSG(2);
                } else {
                    displayMSG(3);
                    this.inputLongitude.setText("");
                    this.inputLatitude.setText("");
                    this.inputPass.setText("");
                    this.inputName.setText("");
                    this.inputUserName.setText("");
                }
            }
        });

    }

    /**
     * Método que inicializa e dá estilos aos botões
     */
    void styleButtons() {
        //BOTOES
        this.register = new Button("Submeter registo!");
        this.register.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.register.setMaxWidth(200);
        this.login = new Button("Efetuar Login");
        this.login.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.login.setMaxWidth(200);
    }

    /**
     * Inicializa as labels
     */
    void initializeLabels() {
        // Titulo
        this.titleContent = new Label();
        this.titleContent.setText("Bem vindo ao Registo de Conta");
        this.titleContent.setTextFill(Color.rgb(255, 0, 0));
        this.titleContent.setFont(new Font(30));
        // Titulo-Localizacao
        this.textLocalizacao = new Label();
        this.textLocalizacao.setText("Localização Atual");
        this.textLocalizacao.setFont(new Font(20));
        // "tem conta vai pro login
        this.textTemConta = new Label();
        this.textTemConta.setText("Já possui conta?");
        this.textTemConta.setFont(new Font(15));
        // Registo
        this.textName = new Label();
        this.textName.setText("Nome      ");
        this.textName.setFont(new Font(15));
        //Pass Word
        this.textPassword = new Label();
        this.textPassword.setText("Password");
        this.textPassword.setFont(new Font(15));
        // ID Unico
        this.textUsername = new Label();
        this.textUsername.setText("UserName");
        this.textUsername.setFont(new Font(15));

        // Latitude
        this.textLatitude = new Label();
        this.textLatitude.setText("Latitude   ");
        this.textLatitude.setFont(new Font(14));

        // Longitude
        this.textLongitude = new Label();
        this.textLongitude.setText("Longitude");
        this.textLongitude.setFont(new Font(14));


        this.inputName = new TextField();
        this.inputPass = new TextField();
        this.inputUserName = new TextField();
        this.inputLatitude = new TextField();
        this.inputLongitude = new TextField();

    }

    /**
     * Ação realizada no fecho da janela
     */
    public void setOnCloseRequest() {
        registerPageWindow.setOnCloseRequest(event -> {
            try {
                out.println("OFF"); //PARA THREAD
                socket.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
