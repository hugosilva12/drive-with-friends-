package Client;

import ModelClass.Driver;
import ModelClass.DriverSettings;
import ModelClass.ListMessageDriver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe Responsável pela estrutura da Página principal.
 */

public class HomeWindow extends Application {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 500;

    private Label textUsername, textLocalizacaoAtual;
    private static Stage homeWindow;
    private static Scene homeScene;

    private Button logoutButton, settingsButton, buttonUpdateLocation, buttonAddFriend, buttonSendIncidentCommunity, buttonSendMessageFriends, buttonListMessages;
    private ImageView imageView;
    private Label nameLabel;

    private Driver d;
    private Socket socket;
    private PrintWriter out;
    private ListMessageDriver lmd;
    private DriverSettings driverSettings;
    private ReceiverMessageBroadcast rmb;
    private ReceiverMessagesServer receiverMessagesServer;

    public HomeWindow(Socket socket, Driver d, ListMessageDriver lmd, DriverSettings driverSettings,ReceiverMessageBroadcast rmb,ReceiverMessagesServer receiverMessagesServer) {
        this.socket = socket;
        this.lmd = lmd;
        this.d = d;
        this.driverSettings = driverSettings;
        this.rmb = rmb;
        this.receiverMessagesServer= receiverMessagesServer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.homeWindow = primaryStage;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.rmb.setStage(homeWindow);


        FileInputStream inputStream = new FileInputStream("imgs/dese.png");
        Image image = new Image(inputStream);
        this.imageView = new ImageView(image);

        this.textLocalizacaoAtual = new Label();
        this.textLocalizacaoAtual.setText("Bem vindo!");
        this.textLocalizacaoAtual.setFont(new Font(30));

        this.textUsername = new Label();
        this.textUsername.setText(d.getNome());
        this.textUsername.setFont(new Font(25));
        this.textUsername.setStyle("-fx-font-weight: bold");
        //Init bottons and styles
        styleButtons();

        //Actions Buttons
        listenerstyleButtons();

        //Load Setting of driver
        loadSettings();
        this.nameLabel = new Label();
        this.nameLabel.setText(d.getNome());
        this.nameLabel.setFont(new Font(18));

        VBox updateButton = new VBox(10);
        updateButton.setAlignment(Pos.CENTER);
        updateButton.setPadding(new Insets(30, 0, 0, 0));
        updateButton.getChildren().addAll(this.buttonUpdateLocation);

        VBox toolbarBox = new VBox(10);
        toolbarBox.setAlignment(Pos.BASELINE_CENTER);

        //ToolBal
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(10, 0, 15, 30));
        toolBar.getItems().add(this.buttonAddFriend);
        toolBar.getItems().add(this.buttonUpdateLocation);
        toolBar.getItems().add(this.buttonSendMessageFriends);
        toolBar.getItems().add(this.buttonSendIncidentCommunity);
        toolBar.getItems().add(this.buttonListMessages);
        toolBar.getItems().add(this.settingsButton);
        toolBar.getItems().add(this.logoutButton);
        VBox vBox = new VBox(toolBar);

        VBox textLocalizacaoAtual = new VBox(10);
        textLocalizacaoAtual.setAlignment(Pos.CENTER);
        textLocalizacaoAtual.setPadding(new Insets(-40, 0, 30, 0));
        textLocalizacaoAtual.getChildren().addAll(this.textLocalizacaoAtual, this.textUsername,this.imageView);


        BorderPane borderLayout = new BorderPane();
        borderLayout.setTop(vBox);
        borderLayout.setCenter(textLocalizacaoAtual);

        this.homeScene = new Scene(borderLayout, WIDTH, HEIGHT);
        this.homeWindow.setScene(this.homeScene);
        this.homeWindow.show();
        notReadMessages(homeWindow);
        setOnCloseRequest();
    }

    void styleButtons() {

        this.buttonUpdateLocation = new Button("Localização");
        this.buttonUpdateLocation.setMaxWidth(150);
        this.buttonUpdateLocation.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonAddFriend = new Button("Add Amigos");
        this.buttonAddFriend.setMaxWidth(150);
        this.buttonAddFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSendIncidentCommunity = new Button("Chat");
        this.buttonSendIncidentCommunity.setMaxWidth(150);
        this.buttonSendIncidentCommunity.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSendMessageFriends = new Button("ChatAmigos");
        this.buttonSendMessageFriends.setMaxWidth(150);
        this.buttonSendMessageFriends.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.logoutButton = new Button("Logout");
        this.logoutButton.setMaxHeight(20);
        this.logoutButton.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonListMessages = new Button("SmsList");
        this.buttonListMessages.setMaxWidth(150);
        this.buttonListMessages.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.settingsButton = new Button("Def Raio");
        this.settingsButton.setMaxWidth(150);
        this.settingsButton.setStyle("-fx-background-color:#686868;-fx-text-fill: white;-fx-font-size: 16px;");


    }
    /**
     * Define as ações que os botões devem executar quando clicados
     */
    void listenerstyleButtons() {

        this.buttonSendMessageFriends.setOnAction(e -> {
            try {
                ChatFriendsWindow localChatFriendsWindow = new ChatFriendsWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                localChatFriendsWindow.start(this.homeWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonUpdateLocation.setOnAction(e -> {
            try {
                LocalizationWindow localizationWindow = new LocalizationWindow(socket, lmd, d, driverSettings,rmb,receiverMessagesServer);
                localizationWindow.start(this.homeWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonAddFriend.setOnAction(e -> {
            try {
                AddFriendsWindow addFriendsWindow = new AddFriendsWindow(socket, lmd, d, driverSettings,rmb,receiverMessagesServer);
                addFriendsWindow.start(this.homeWindow);
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
                loginMenu.start(this.homeWindow);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonSendIncidentCommunity.setOnAction(e -> {
            try {
                ChatCommunityWindow chatCommunityWindow = new ChatCommunityWindow(socket, lmd, d, driverSettings,rmb,receiverMessagesServer);
                chatCommunityWindow.start(this.homeWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonListMessages.setOnAction(e -> {
            try {
                MessageListWindow messageListWindow = new MessageListWindow(socket, lmd, d, driverSettings,rmb,  receiverMessagesServer);
                messageListWindow.start(this.homeWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.settingsButton.setOnAction(e -> {
            try {
                SettingArea messageListWindow = new SettingArea(socket, lmd, d, driverSettings,rmb,receiverMessagesServer);
                messageListWindow.start(this.homeWindow);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

    }

    /**
     * Este método termina a comunicação entre a thread worker e o servidor TCP
     */
    public void setOnCloseRequest() {
        this.homeWindow.setOnCloseRequest(event -> {
            out.println("OFF"); //PARA THREAD
        });
    }

    /**
     * Envia a notificação de quantas mensagens não lidas tem o condutor
     *
     * @param stage stage onde será mostrada a notificação
     */
    public void notReadMessages(Stage stage) {
        int notRead = 0;
        for (int i = 0; i < lmd.size(); i++) {
            if (lmd.getIndex(i) != null) {
                if (!lmd.getIndex(i).getNotified()) {
                    notRead++;
                }
            }
        }
        if (notRead != 0) {
            Scene scene = stage.getScene();
            Parent pane = scene.getRoot();
            NotificationPane notificationPane = new NotificationPane(pane);
            notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
            notificationPane.setText("              " + notRead + " mensagen(s) novas!");
            scene = new Scene(notificationPane, scene.getWidth(), scene.getWidth());
            stage.setScene(scene);
            notificationPane.show();
            lmd.addHm(null);
        }
    }

    /**
     * Carrega as definições do driver para a lista
     */
    public void loadSettings() {
        out.println("LOADSETTING;" + d.getId());
    }

}
