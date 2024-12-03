package Client;

import ModelClass.Driver;
import ModelClass.DriverSettings;
import ModelClass.ListMessageDriver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe responsável por atualizar a localização do condutor
 */
public class LocalizationWindow extends Application {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 500;

    private TextField inputLatitude, inputLongitude;
    private Label textLatitude, textLongitude, textLocalizacaoAtual;
    private Driver d;
    private Button buttonUpdate, buttonLogout, buttonHome, buttonMessageList, buttonSettings, buttonAddFriend, buttonSendMessageFriends, buttonSendIncidentCommunity;
    private static Stage localStage;
    private static Scene homeScene;
    private Socket socket;

    private ListMessageDriver lmd;
    private PrintWriter out;

    private DriverSettings driverSettings;
    private ReceiverMessageBroadcast rmb;
    private ReceiverMessagesServer receiverMessagesServer;

    public LocalizationWindow(Socket socket, ListMessageDriver lmd, Driver d, DriverSettings driverSettings, ReceiverMessageBroadcast rmb, ReceiverMessagesServer receiverMessagesServer) {
        this.d = d;
        this.lmd = lmd;
        this.socket = socket;
        this.driverSettings = driverSettings;
        this.rmb = rmb;
        this.receiverMessagesServer = receiverMessagesServer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.localStage = primaryStage;

        out = new PrintWriter(socket.getOutputStream(), true);
        this.textLocalizacaoAtual = new Label();
        this.textLocalizacaoAtual.setText("Localização Atual");
        this.textLocalizacaoAtual.setFont(new Font(25));
        // Latitude
        this.textLatitude = new Label();
        this.textLatitude.setText("Latitude   ");
        this.textLatitude.setFont(new Font(14));

        // Longitude
        this.textLongitude = new Label();
        this.textLongitude.setText("Longitude");
        this.textLongitude.setFont(new Font(14));

        this.inputLatitude = new TextField();
        this.inputLongitude = new TextField();

        this.inputLatitude.setText(String.valueOf(d.getLatitude()));
        this.inputLongitude.setText(String.valueOf(d.getLongitude()));
        //style buttons
        styleButtons();
        listenerButtons();


        HBox hBoxTitle = new HBox(10);
        hBoxTitle.setPadding(new Insets(-30, 0, 30, 0));
        hBoxTitle.setAlignment(Pos.CENTER);
        hBoxTitle.getChildren().addAll(this.textLocalizacaoAtual);

        ///input  latitude
        HBox hBoxinputLatitude = new HBox(10);
        hBoxinputLatitude.setAlignment(Pos.CENTER);
        hBoxinputLatitude.getChildren().addAll(this.textLatitude, this.inputLatitude);
        ///input  longitude
        HBox hBoxinputLongitude = new HBox(10);
        hBoxinputLongitude.setAlignment(Pos.CENTER);
        hBoxinputLongitude.getChildren().addAll(this.textLongitude, this.inputLongitude);


        VBox vboxLocalizacao = new VBox(10);
        vboxLocalizacao.setAlignment(Pos.CENTER);
        vboxLocalizacao.setPadding(new Insets(-40, 0, 30, 0));
        vboxLocalizacao.getChildren().addAll(hBoxTitle, hBoxinputLatitude, hBoxinputLongitude, this.buttonUpdate);

        //ToolBal
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(10, 0, 15, 30));
        toolBar.getItems().add(this.buttonHome);
        toolBar.getItems().add(this.buttonAddFriend);
        toolBar.getItems().add(this.buttonSendIncidentCommunity);
        toolBar.getItems().add(this.buttonSendMessageFriends);
        toolBar.getItems().add(this.buttonMessageList);
        toolBar.getItems().add(this.buttonSettings);

        toolBar.getItems().add(this.buttonLogout);
        VBox vBox = new VBox(toolBar);


        BorderPane borderLayout = new BorderPane();
        borderLayout.setTop(vBox);
        borderLayout.setCenter(vboxLocalizacao);


        this.homeScene = new Scene(borderLayout, WIDTH, HEIGHT);
        this.localStage.setScene(this.homeScene);
        this.localStage.show();

    }

    void styleButtons() {
        //buttons
        // Update Button

        this.buttonHome = new Button("Home");
        this.buttonHome.setMaxWidth(150);
        this.buttonHome.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");


        this.buttonAddFriend = new Button("Add Amigos");
        this.buttonAddFriend.setMaxWidth(150);
        this.buttonAddFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");


        this.buttonSendMessageFriends = new Button("ChatComunidade");
        this.buttonSendMessageFriends.setMaxWidth(150);
        this.buttonSendMessageFriends.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");


        this.buttonSendIncidentCommunity = new Button("ChatAmigos");
        this.buttonSendIncidentCommunity.setMaxWidth(150);
        this.buttonSendIncidentCommunity.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonMessageList = new Button("L.Messages");
        this.buttonMessageList.setMaxWidth(150);
        this.buttonMessageList.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSettings = new Button("Def Raio");
        this.buttonSettings.setMaxWidth(150);
        this.buttonSettings.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonUpdate = new Button("Atualizar localização");
        this.buttonUpdate.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.buttonUpdate.setMaxWidth(200);

        this.buttonLogout = new Button("Logout");
        this.buttonLogout.setMaxHeight(20);
        this.buttonLogout.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

    }

    /**
     * Define as ações que os botões devem executar quando clicados
     */
    void listenerButtons() {

        this.buttonHome.setOnAction(e -> {
            try {
                HomeWindow hWindow = new HomeWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                hWindow.start(this.localStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });


        this.buttonAddFriend.setOnAction(e -> {
            try {
                AddFriendsWindow afWindow = new AddFriendsWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                afWindow.start(this.localStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonSendMessageFriends.setOnAction(e -> {
            try {
                ChatCommunityWindow ccWindow = new ChatCommunityWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                ccWindow.start(this.localStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonSendIncidentCommunity.setOnAction(e -> {
            try {
                ChatFriendsWindow cfWindow = new ChatFriendsWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                cfWindow.start(this.localStage);
                //fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonMessageList.setOnAction(e -> {
            try {
                MessageListWindow mlWindow = new MessageListWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                mlWindow.start(this.localStage);
                //fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonSettings.setOnAction(e -> {
            try {
                SettingArea cfWindow = new SettingArea(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                cfWindow.start(this.localStage);
                //fileJsonOperations.updateDataDriver(d);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonUpdate.setOnAction(e -> {
            String double1 = this.inputLatitude.getText();
            String double2 = this.inputLongitude.getText();

            if (!double1.isEmpty() && !double2.isEmpty()) {
                Double latitude = Double.parseDouble(this.inputLatitude.getText());
                Double longitude = Double.parseDouble(this.inputLongitude.getText());
                d.setLatitude(latitude);
                d.setLongitude(longitude);
                this.inputLatitude.setText(String.valueOf(d.getLatitude()));
                this.inputLongitude.setText(String.valueOf(d.getLongitude()));
                String list = "";
                if (d.getFriendsList().getSize() == 0) {
                    list = "null";
                }
                for (int i = 0; i < d.getFriendsList().getSize(); i++) {
                    list = list + d.getFriendsList().getIndex(i) + ";";
                }
                String dataDriver = d.getId() + ";" + d.getNome() + ";" + d.getUserName() + ";" + d.getLatitude() + ";" + d.getLongitude() + ";" + d.getPassword() + ":" + list;
                out.println("SAVEDRIVER;" + dataDriver);
            }
        });

        this.buttonLogout.setOnAction(e -> {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("OFF"); //PARA THREAD
                rmb.setListening();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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
}
