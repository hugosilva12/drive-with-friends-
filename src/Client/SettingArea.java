package Client;

import ModelClass.Driver;
import ModelClass.DriverSettings;
import ModelClass.FileJsonOperations;
import ModelClass.ListMessageDriver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

/**
 * Classe Responsável pela atualização das areas nas quais o utilizador pretende receber o número de incidentes
 */
public class SettingArea extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;

    private TextField inputArea, inputAreas;
    private Label textTitle, textSend, titleFriends;
    private Driver d;
    private Button updateButton, logoutButton, buttonHome, buttonMessageList, buttonUpdateLocation, buttonAddFriend, buttonSendIncidentCommunity, buttonsendMessageFriends, buttonAddCity;
    private static Stage localStage;
    private static Scene homeScene;

    private Socket socket;
    private ListMessageDriver lmd;
    private DriverSettings driverSettings;
    private PrintWriter out;
    private BufferedReader in;
    private FileJsonOperations fileJsonOperations;
    private ReceiverMessageBroadcast rmb;
    private ReceiverMessagesServer receiverMessagesServer;
    private ListView listArea;

    public SettingArea(Socket socket, ListMessageDriver lmd, Driver d, DriverSettings driverSettings, ReceiverMessageBroadcast rmb, ReceiverMessagesServer receiverMessagesServer) {
        this.d = d;
        this.lmd = lmd;
        this.socket = socket;
        this.fileJsonOperations = new FileJsonOperations();
        this.driverSettings = driverSettings;
        this.rmb = rmb;
        this.receiverMessagesServer = receiverMessagesServer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.localStage = primaryStage;

        out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.textTitle = new Label();
        this.textTitle.setText("Definir Raio! (kms)");
        this.textTitle.setFont(new Font(25));

        settingList();
        this.textSend = new Label();
        this.textSend.setText("");
        this.textSend.setFont(new Font(12));

        this.inputArea = new TextField();
        this.inputArea.setText(driverSettings.getKm());

        this.inputAreas = new TextField();
        this.inputAreas.setText("Digite cidade!");
        this.titleFriends = new Label();
        this.titleFriends.setText("Minhas Areas");
        this.titleFriends.setFont(new Font(20));

        styleButtons();
        listenerstyleButtons();

        HBox hBoxTitle = new HBox(10);
        hBoxTitle.setPadding(new Insets(-30, 0, 30, 0));
        hBoxTitle.setAlignment(Pos.CENTER);
        hBoxTitle.getChildren().addAll(this.textTitle);

        ///input raio
        HBox hBoxinputArea = new HBox(10);
        hBoxinputArea.setAlignment(Pos.CENTER);
        hBoxinputArea.getChildren().addAll(this.inputArea);


        HBox addArea = new HBox(10);
        addArea.setAlignment(Pos.CENTER);
        addArea.setPadding(new Insets(0, 40, 30, 40));
        addArea.getChildren().addAll(this.titleFriends, this.listArea, this.inputAreas);


        VBox vboxLocalizacao = new VBox(10);
        vboxLocalizacao.setAlignment(Pos.CENTER);
        vboxLocalizacao.setPadding(new Insets(-40, 0, 30, 0));
        vboxLocalizacao.getChildren().addAll(hBoxTitle, hBoxinputArea, this.updateButton, this.textSend, addArea, this.buttonAddCity);


        //ToolBal
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(10, 0, 15, 30));
        toolBar.getItems().add(this.buttonHome);
        toolBar.getItems().add(this.buttonAddFriend);
        toolBar.getItems().add(this.buttonUpdateLocation);
        toolBar.getItems().add(this.buttonsendMessageFriends);
        toolBar.getItems().add(this.buttonSendIncidentCommunity);
        toolBar.getItems().add(this.buttonMessageList);

        toolBar.getItems().add(this.logoutButton);
        VBox vBox = new VBox(toolBar);

        BorderPane borderLayout = new BorderPane();
        borderLayout.setTop(vBox);
        borderLayout.setCenter(vboxLocalizacao);

        this.homeScene = new Scene(borderLayout, WIDTH, HEIGHT);
        this.localStage.setScene(this.homeScene);
        this.localStage.show();
        setOnCloseRequest();
    }

    void styleButtons() {

        this.buttonHome = new Button("Home");
        this.buttonHome.setMaxWidth(150);
        this.buttonHome.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonAddFriend = new Button("Add Amigos");
        this.buttonAddFriend.setMaxWidth(150);
        this.buttonAddFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonUpdateLocation = new Button("Localizacao");
        this.buttonUpdateLocation.setMaxWidth(150);
        this.buttonUpdateLocation.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSendIncidentCommunity = new Button("ChatComunidade");
        this.buttonSendIncidentCommunity.setMaxWidth(150);
        this.buttonSendIncidentCommunity.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonsendMessageFriends = new Button("ChatAmigos");
        this.buttonsendMessageFriends.setMaxWidth(150);
        this.buttonsendMessageFriends.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.updateButton = new Button("Definir Raio");
        this.updateButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.updateButton.setMaxWidth(200);

        this.buttonAddCity = new Button("Adicionar Zona");
        this.buttonAddCity.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.buttonAddCity.setMaxWidth(200);

        this.buttonMessageList = new Button("L.Messages");
        this.buttonMessageList.setMaxWidth(150);
        this.buttonMessageList.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.logoutButton = new Button("Logout");
        this.logoutButton.setMaxHeight(20);
        this.logoutButton.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

    }

    /**
     * Define as ações que os botões devem executar quando clicados
     */
    void listenerstyleButtons() {

        this.buttonHome.setOnAction(e -> {
            try {
                HomeWindow hWindow = new HomeWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                hWindow.start(this.localStage);
                fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonAddFriend.setOnAction(e -> {
            try {
                AddFriendsWindow afWindow = new AddFriendsWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                afWindow.start(this.localStage);
                fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonUpdateLocation.setOnAction(e -> {
            try {
                LocalizationWindow lWindow = new LocalizationWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                lWindow.start(this.localStage);
                fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonSendIncidentCommunity.setOnAction(e -> {
            try {
                ChatCommunityWindow ccWindow = new ChatCommunityWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                ccWindow.start(this.localStage);
                fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonsendMessageFriends.setOnAction(e -> {
            try {
                ChatFriendsWindow cfWindow = new ChatFriendsWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                cfWindow.start(this.localStage);
                fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonMessageList.setOnAction(e -> {
            try {
                MessageListWindow mlWindow = new MessageListWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                mlWindow.start(this.localStage);
                fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.updateButton.setOnAction(e -> {

            if (!this.inputArea.getText().isEmpty()) {
                out.println("DEFINEAREA;" + d.getId() + ";" + this.inputArea.getText() + ";" + driverSettings.getAreas());
                this.textSend.setText("Raio atualizado!");
            }
        });

        this.buttonAddCity.setOnAction(e -> {

            if (!this.inputAreas.getText().equals("Digite cidade!") && !this.inputArea.getText().isEmpty()) {
                if (driverSettings.getAreas().equals("null")) {
                    out.println("DEFINEAREA;" + d.getId() + ";" + this.inputArea.getText() + ";" + this.inputAreas.getText() + ":");
                } else {
                    out.println("DEFINEAREA;" + d.getId() + ";" + this.inputArea.getText() + ";" + driverSettings.getAreas() + this.inputAreas.getText() + ":");
                }
                this.textSend.setText("Area Adicionada!");
                settingList();
            } else {
                displayMSG();
            }

        });

        this.logoutButton.setOnAction(e -> {
            out.println(this.d.toString());
            Login loginMenu = new Login();
            try {
                loginMenu.start(this.localStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    public void settingList() {
        this.listArea = new ListView();
        this.listArea.setMaxWidth(100);
        this.listArea.setMaxHeight(40);
        if (driverSettings.getAreas().equals("null")) {
            this.listArea.getItems().add("Vazia");
        } else {
            String[] zones = driverSettings.getAreas().split(":");
            for (int i = 0; i < zones.length; i++) {
                this.listArea.getItems().add(zones[i]);
            }
        }
    }

    /**
     * Exibe um alerta ao utilizador da aplicação de que o input não é válido
     */
    public void displayMSG() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erro");
        alert.setHeaderText("Dados Invalidos");
        alert.setContentText("Verifique os inputs");
        alert.showAndWait();
    }

    public void setOnCloseRequest() {
        this.localStage.setOnCloseRequest(event -> {
            out.println("OFF"); //PARA THREAD
        });
    }
}