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
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe Responsável pelo envio de mensagens para a comunidade
 */
public class ChatCommunityWindow extends Application {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 500;
    private Button sendMessageButton, logoutButton, settingsButton, buttonMessageList ,buttonUpdateLocation, buttonAddFriend, buttonSendMessageFriends, buttonHome;

    private static Stage chatWindow;
    private static Scene chatFriendsScene;
    private DriverSettings driverSettings;
    private ListMessageDriver lmd;
    private Driver d;
    private Socket socket;
    private PrintWriter out;
    private ReceiverMessageBroadcast rmb;
    private Label title;
    private TextArea inputFriend;
    private ReceiverMessagesServer receiverMessagesServer;

    public ChatCommunityWindow(Socket socket, ListMessageDriver lmd , Driver d, DriverSettings driverSettings, ReceiverMessageBroadcast rmb, ReceiverMessagesServer receiverMessagesServer) {
        this.d = d;
        this.socket = socket;
        this.lmd = lmd;
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
        listenerStyleButtons();
        this.inputFriend = new TextArea();
        this.inputFriend.setMaxWidth(200);
        this.inputFriend.setMaxHeight(150);
        this.inputFriend.setPrefWidth(200);
        this.inputFriend.setPrefHeight(150);
        this.inputFriend.positionCaret(-50);
        //Design
        this.title = new Label();
        this.title.setText("Informe a comunidade!");
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
        toolBar.getItems().add(this.buttonUpdateLocation);
        toolBar.getItems().add(this.buttonSendMessageFriends);
        toolBar.getItems().add(this.buttonMessageList);
        toolBar.getItems().add(this.settingsButton);
        toolBar.getItems().add(this.logoutButton);
        VBox vBox = new VBox(toolBar);


        BorderPane borderLayoutWindow = new BorderPane();
        borderLayoutWindow.setTop(vBox);
        borderLayoutWindow.setCenter(btnVBox);

        this.chatFriendsScene = new Scene(borderLayoutWindow, WIDTH, HEIGHT);
        this.chatWindow.setScene(this.chatFriendsScene);
        this.chatWindow.show();

    }

    public static void notifier(String pTitle, String pMessage, Stage chatWindow) {
        Popup popup = new Popup();

        Label label = new Label();
        // set background
        label.setStyle(" -fx-background-color: white;");

        // add the label
        popup.getContent().add(label);

        // set size of label
        label.setMinWidth(80);
        label.setMinHeight(50);

        if (!popup.isShowing())
            popup.show(chatWindow);
        else
            popup.hide();
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

        this.buttonSendMessageFriends = new Button("ChatAmigos");
        this.buttonSendMessageFriends.setMaxWidth(150);
        this.buttonSendMessageFriends.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonMessageList = new Button("L.Messages");
        this.buttonMessageList.setMaxWidth(150);
        this.buttonMessageList.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.settingsButton = new Button("Def Raio");
        this.settingsButton.setMaxWidth(150);
        this.settingsButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.sendMessageButton = new Button("Enviar");
        this.sendMessageButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.sendMessageButton.setMaxWidth(200);

        // Logout button
        this.logoutButton = new Button("Logout");
        this.logoutButton.setMaxHeight(20);
        this.logoutButton.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

    }

    void listenerStyleButtons() {


        this.buttonHome.setOnAction(e -> {
            try {
                HomeWindow localizationWindow = new HomeWindow(socket,d, lmd,driverSettings,rmb,receiverMessagesServer);
                localizationWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonAddFriend.setOnAction(e -> {
            try {
                AddFriendsWindow addFriendsWindow = new AddFriendsWindow(socket,lmd,d,driverSettings,rmb,receiverMessagesServer);
                addFriendsWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonMessageList.setOnAction(e -> {
            try {
                MessageListWindow mlWindow = new MessageListWindow(socket,lmd,d,driverSettings,rmb,receiverMessagesServer);
                mlWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonUpdateLocation.setOnAction(e -> {
            try {
                LocalizationWindow localizationWindow = new LocalizationWindow(socket,lmd,d,driverSettings,rmb,receiverMessagesServer);
                localizationWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.sendMessageButton.setOnAction(e -> {
            if (inputFriend.getText().isEmpty()) {
                displayMSG();
            } else {
                try {
                    out.println("FORALL;" + d.getId() + ";" + inputFriend.getText());
                    inputFriend.setText("");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        this.buttonSendMessageFriends.setOnAction(e -> {
            try {
                ChatFriendsWindow cfWindow = new ChatFriendsWindow(socket,d, lmd,driverSettings,rmb,receiverMessagesServer);
                cfWindow.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.settingsButton.setOnAction(e -> {
            try {
                SettingArea settings = new SettingArea(socket,lmd,d,driverSettings,rmb,receiverMessagesServer);
                settings.start(this.chatWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.logoutButton.setOnAction(e -> {

            Login loginMenu = new Login();
            try {
                loginMenu.start(this.chatWindow);
                rmb.setListening();
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