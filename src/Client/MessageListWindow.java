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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

/**
 * Classe responsável por apresentar o histórico de mensagens do condutor
 */
public class MessageListWindow extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;

    private Label title;
    private ListView listMessages;
    private Button logoutButton, buttonUpdateList, buttonHome, buttonLocalization, settingsButton, buttonAddFriend, buttonEnviarMSGComunidade, buttonSendMessageFriends;

    private static Stage messageListStage;
    private static Scene MessageListScene;

    private ListMessageDriver lmd;
    private Driver d;
    private Socket socket;
    private PrintWriter out;
    private DriverSettings driverSettings;
    private ReceiverMessageBroadcast rmb;
    private ReceiverMessagesServer receiverMessagesServer;

    public MessageListWindow(Socket socket, ListMessageDriver lmd, Driver d, DriverSettings driverSettings, ReceiverMessageBroadcast rmb, ReceiverMessagesServer receiverMessagesServer) {
        this.socket = socket;
        this.d = d;
        this.lmd = lmd;
        this.driverSettings = driverSettings;
        this.rmb = rmb;
        this.receiverMessagesServer = receiverMessagesServer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.messageListStage = primaryStage;
        out = new PrintWriter(socket.getOutputStream(), true);


        this.title = new Label();
        this.title.setText("Drive With Friends - MessageList");
        this.title.setFont(new Font(24));
        this.title.setStyle("-fx-font-weight: bold");

        this.listMessages = new ListView();
        this.listMessages.setMaxWidth(500);
        this.listMessages.setMaxHeight(400);
        this.listMessages.setPrefWidth(500);
        this.listMessages.setPrefHeight(400);

        styleButtons();
        listenerStyleButtons();
        //State messages
        updateStateMessage();
        //Load Messages
        insertMessagesList();

        HBox listSmsHBox = new HBox(10);
        listSmsHBox.setAlignment(Pos.CENTER);
        listSmsHBox.setPadding(new Insets(10, 0, 30, 0));
        listSmsHBox.getChildren().addAll(this.listMessages);

        VBox vBoxTitle = new VBox(10);
        vBoxTitle.setAlignment(Pos.CENTER);
        vBoxTitle.setPadding(new Insets(10, 0, 30, 0));

        vBoxTitle.getChildren().addAll(this.title, listSmsHBox, this.buttonUpdateList);

        //ToolBar
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(10, 0, 15, 20));
        toolBar.getItems().add(this.buttonHome);
        toolBar.getItems().add(this.buttonAddFriend);
        toolBar.getItems().add(this.buttonLocalization);
        toolBar.getItems().add(this.buttonSendMessageFriends);
        toolBar.getItems().add(this.buttonEnviarMSGComunidade);
        toolBar.getItems().add(this.settingsButton);

        toolBar.getItems().add(this.logoutButton);
        VBox vBox = new VBox(toolBar);

        BorderPane bp = new BorderPane();
        bp.setTop(vBox);
        bp.setCenter(vBoxTitle);

        this.MessageListScene = new Scene(bp, WIDTH, HEIGHT);
        this.messageListStage.setScene(this.MessageListScene);
        this.messageListStage.show();
    }

    void styleButtons() {

        this.buttonHome = new Button("Home");
        this.buttonHome.setMaxWidth(150);
        this.buttonHome.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonAddFriend = new Button("Adicionar Amigo");
        this.buttonAddFriend.setMaxWidth(150);
        this.buttonAddFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonUpdateList = new Button("Atualizar");
        this.buttonUpdateList.setMaxWidth(150);
        this.buttonUpdateList.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonLocalization = new Button("Localização");
        this.buttonLocalization.setMaxWidth(150);
        this.buttonLocalization.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonEnviarMSGComunidade = new Button("ChatComunidade");
        this.buttonEnviarMSGComunidade.setMaxWidth(150);
        this.buttonEnviarMSGComunidade.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSendMessageFriends = new Button("ChatAmigos");
        this.buttonSendMessageFriends.setMaxWidth(150);
        this.buttonSendMessageFriends.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.settingsButton = new Button("Def Raio");
        this.settingsButton.setMaxWidth(150);
        this.settingsButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.logoutButton = new Button("Logout");
        this.logoutButton.setMaxHeight(20);
        this.logoutButton.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

    }

    void listenerStyleButtons() {

        this.buttonLocalization.setOnAction(e -> {
            try {
                LocalizationWindow localizationWindow = new LocalizationWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                localizationWindow.start(this.messageListStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonUpdateList.setOnAction(e -> {
            loadMessages();
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            insertMessagesList();
        });


        this.buttonHome.setOnAction(e -> {
            try {
                HomeWindow homeWindow = new HomeWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                homeWindow.start(this.messageListStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonEnviarMSGComunidade.setOnAction(e -> {
            try {
                ChatCommunityWindow chatCommunityWindow = new ChatCommunityWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                chatCommunityWindow.start(this.messageListStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonSendMessageFriends.setOnAction(e -> {
            try {
                ChatFriendsWindow chatFriendsWindow = new ChatFriendsWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                chatFriendsWindow.start(this.messageListStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });


        this.buttonAddFriend.setOnAction(e -> {
            try {
                AddFriendsWindow addFriendsWindow = new AddFriendsWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                addFriendsWindow.start(this.messageListStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.settingsButton.setOnAction(e -> {
            try {
                SettingArea sa = new SettingArea(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                sa.start(this.messageListStage);
            } catch (Exception exception) {
                exception.printStackTrace();
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
                loginMenu.start(this.messageListStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    public void loadMessages() {
        out.println("LOADMESSAGE;" + d.getId());
    }

    public void insertMessagesList() {
        if (lmd != null) {
            this.listMessages.getItems().clear();
            for (int i = 0; i < lmd.size(); i++) {
                if (lmd.getIndex(i) != null) {
                    this.listMessages.getItems().add("User:" + lmd.getIndex(i).getUsername() + "          Mensagem:" + lmd.getIndex(i).getMessage());
                }
            }
        } else {
            this.listMessages.getItems().add("NULL");
        }
    }

    //Apaga a mensagem a null e atualiza o estado das mesnagens (se necessário)
    public void updateStateMessage() {
        if (lmd.size() != 0) {
            if (lmd.getIndex(lmd.size() - 1) == null) {
                out.println("CHANGESTATUSMESSAGE;" + d.getId());
                lmd.removeIndex(lmd.size() - 1);
            }
        }
    }
}
