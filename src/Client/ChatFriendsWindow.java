package Client;

import ModelClass.Driver;
import ModelClass.DriverSettings;
import ModelClass.ListMessageDriver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe Responsável pelo envio de mensagens para os amigos
 */
public class ChatFriendsWindow extends Application {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 500;
    private Button sendMessageButton, buttonLocalization, buttonAtualizaLocalizacao, buttonHome, buttonMessageList, settingsButton, logoutButton, buttonAddFriend, buttonSendIncidentCommunity;

    private static Stage chatWindow;
    private static Scene chatFriendsScene;

    private ListMessageDriver lmd;
    private Driver d;
    private Socket socket;
    private DriverSettings driverSettings;
    private ReceiverMessageBroadcast rmb;
    private ReceiverMessagesServer receiverMessagesServer;
    private PrintWriter out;
    private NotificationPane notificationPane;
    private Label title;
    private TextArea inputFriend;

    public ChatFriendsWindow(Socket socket, Driver d, ListMessageDriver lmd, DriverSettings driverSettings, ReceiverMessageBroadcast rmb, ReceiverMessagesServer receiverMessagesServer) {
        this.socket = socket;
        this.lmd = lmd;
        this.d = d;
        this.driverSettings = driverSettings;
        this.rmb = rmb;
        this.receiverMessagesServer = receiverMessagesServer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.chatWindow = primaryStage;
        out = new PrintWriter(socket.getOutputStream(), true);
        //Buttons OPERATIONS
        styleButtons();
        listennerButtons(primaryStage);


        this.inputFriend = new TextArea();
        this.inputFriend.setMaxWidth(250);
        this.inputFriend.setMaxHeight(150);
        this.inputFriend.setPrefWidth(200);
        this.inputFriend.setPrefHeight(150);
        this.inputFriend.positionCaret(-50);
        //Design
        this.title = new Label();
        this.title.setText("Fale com os amigos!");
        this.title.setFont(new Font(30));

        //Vbox window
        VBox vBoxtitle = new VBox(10);
        vBoxtitle.setAlignment(Pos.CENTER);
        vBoxtitle.setPadding(new Insets(-70, 0, 30, 0));
        vBoxtitle.getChildren().addAll(this.title, this.inputFriend, this.sendMessageButton);

        VBox btnVBox = new VBox(10);
        btnVBox.setAlignment(Pos.CENTER);
        btnVBox.getChildren().addAll(vBoxtitle);


        //ToolBal
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(10, 0, 15, 30));
        toolBar.getItems().add(this.buttonHome);
        toolBar.getItems().add(this.buttonAddFriend);
        toolBar.getItems().add(this.buttonLocalization);
        toolBar.getItems().add(this.buttonAtualizaLocalizacao);
        toolBar.getItems().add(this.buttonSendIncidentCommunity);
        toolBar.getItems().add(this.buttonMessageList);
        toolBar.getItems().add(this.settingsButton);
        toolBar.getItems().add(this.logoutButton);
        VBox vBox = new VBox(toolBar);


        BorderPane bp = new BorderPane();
        bp.setTop(vBox);
        bp.setCenter(vBoxtitle);


        this.notificationPane = new NotificationPane();

        VBox vBox2 = new VBox(notificationPane);
        bp.setBottom(vBox2);

        this.chatFriendsScene = new Scene(bp, WIDTH, HEIGHT);
        this.chatWindow.setScene(this.chatFriendsScene);
        this.chatWindow.show();
    }


    void styleButtons() {

        this.buttonHome = new Button("Home");
        this.buttonHome.setMaxWidth(150);
        this.buttonHome.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonAddFriend = new Button("Add Amigos");
        this.buttonAddFriend.setMaxWidth(150);
        this.buttonAddFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSendIncidentCommunity = new Button("ChatComunidade");
        this.buttonSendIncidentCommunity.setMaxWidth(150);
        this.buttonSendIncidentCommunity.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonAtualizaLocalizacao = new Button("Atualizar localização");
        this.buttonAtualizaLocalizacao.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.buttonAtualizaLocalizacao.setMaxWidth(200);

        this.buttonLocalization = new Button("Localizacao");
        this.buttonLocalization.setMaxWidth(150);
        this.buttonLocalization.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonMessageList = new Button("L.Messages");
        this.buttonMessageList.setMaxWidth(150);
        this.buttonMessageList.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.settingsButton = new Button("Def Raio");
        this.settingsButton.setMaxWidth(150);
        this.settingsButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.sendMessageButton = new Button("Enviar");
        this.sendMessageButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.sendMessageButton.setMaxWidth(200);

        this.logoutButton = new Button("Logout");
        this.logoutButton.setMaxHeight(20);
        this.logoutButton.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

    }

    /**
     * Define as ações que os botões devem executar quando clicados
     */
    void listennerButtons(Stage button) {

        this.buttonHome.setOnAction(e -> {
            try {
                HomeWindow hWindow = new HomeWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                hWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonAddFriend.setOnAction(e -> {
            try {
                AddFriendsWindow afWindow = new AddFriendsWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                afWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonSendIncidentCommunity.setOnAction(e -> {
            try {
                ChatCommunityWindow ccWindow = new ChatCommunityWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                ccWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonLocalization.setOnAction(e -> {
            try {
                LocalizationWindow lWindow = new LocalizationWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                lWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });


        this.buttonMessageList.setOnAction(e -> {
            try {
                MessageListWindow mlWindow = new MessageListWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                mlWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.settingsButton.setOnAction(e -> {
            try {
                SettingArea sa = new SettingArea(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                sa.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.sendMessageButton.setOnAction(e -> {
            if (inputFriend.getText().isEmpty()) {
                displayMSG();
            } else {
                try {
                    out.println("FORFRIENDS;" + d.getId() + ";" + inputFriend.getText());
                    inputFriend.setText("");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });


        this.logoutButton.setOnAction(e -> {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("OFF"); //PARA THREAD
                rmb.setListening();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Login loginMenu = new Login();
            try {
                loginMenu.start(this.chatWindow);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    public void displayMSG() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erro");
        alert.setHeaderText("Falha ");
        alert.setContentText("Mensagem está Vazia!");
        alert.showAndWait();
    }
}