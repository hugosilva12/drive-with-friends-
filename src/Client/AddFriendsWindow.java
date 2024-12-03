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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Classe Responsável pelas ações de adicionar/remover amigos
 */
public class AddFriendsWindow extends Application {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 500;

    private static Stage friendsStage;
    private static Scene friendsScene;

    private TextField inputAmigo, inputRemoveAmigo;
    private Label title, titleRemoverAmigos, titleUsers, titleFriends;


    private ListView listView, listViewFriends;
    private Button addButton, settingsButton, buttonHome, buttonLocalization, buttonMessageList, updateButton, logoutButton, buttonAtualizaLocalizacao, buttonAdicionarAmigo, buttonSendIncidentCommunity, buttonAddFriend, buttonRemoveFriend;

    private ListMessageDriver lmd;
    private Driver d;
    private Socket socket;

    private PrintWriter out;
    private ReceiverMessageBroadcast rmb;
    private FileJsonOperations fileJsonOperations;
    //Array UserNames
    private ArrayList<Driver> userAplication = new ArrayList<>();
    private DriverSettings driverSettings;
    private ReceiverMessagesServer receiverMessagesServer;

    public AddFriendsWindow(Socket socket, ListMessageDriver lmd, Driver d, DriverSettings driverSettings, ReceiverMessageBroadcast rmb, ReceiverMessagesServer receiverMessagesServer) {
        this.d = d;
        this.lmd = lmd;
        this.socket = socket;
        this.fileJsonOperations = new FileJsonOperations();
        this.userAplication = new ArrayList<>();
        this.driverSettings = driverSettings;
        this.rmb = rmb;
        this.receiverMessagesServer = receiverMessagesServer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.friendsStage = primaryStage;

        this.title = new Label();
        this.title.setText("Adicionar Amigos");
        this.title.setFont(new Font(30));


        this.titleRemoverAmigos = new Label();
        this.titleRemoverAmigos.setText("Remover amigos");
        this.titleRemoverAmigos.setFont(new Font(30));

        this.titleUsers = new Label();
        this.titleUsers.setText("Utilizadores");
        this.titleUsers.setFont(new Font(20));

        this.titleFriends = new Label();
        this.titleFriends.setText("Amigos");
        this.titleFriends.setFont(new Font(20));


        userAplication = fileJsonOperations.getAllUsernamesJsonFile();
        updateListView();

        styleButtons();
        listenerStyleButtons();

        VBox vBoxtitle = new VBox(10);
        vBoxtitle.setAlignment(Pos.CENTER);
        vBoxtitle.setPadding(new Insets(-10, 0, 30, 0));
        vBoxtitle.getChildren().addAll(this.title);

        VBox vBoxtitleRemove = new VBox(10);
        vBoxtitleRemove.setAlignment(Pos.CENTER);
        vBoxtitleRemove.setPadding(new Insets(0, 0, 30, 0));
        vBoxtitleRemove.getChildren().addAll(this.titleRemoverAmigos);

        HBox friendsHBox = new HBox(10);
        friendsHBox.setAlignment(Pos.CENTER);
        friendsHBox.setPadding(new Insets(0, 40, 30, 40));
        friendsHBox.getChildren().addAll(this.titleUsers, this.listView, this.inputAmigo);


        HBox removefriendsHBox = new HBox(10);
        removefriendsHBox.setAlignment(Pos.CENTER);
        removefriendsHBox.setPadding(new Insets(0, 40, 30, 40));
        removefriendsHBox.getChildren().addAll(this.titleFriends, this.listViewFriends, this.inputRemoveAmigo);

        VBox btnVBox = new VBox(10);
        btnVBox.setAlignment(Pos.CENTER);
        btnVBox.getChildren().addAll(vBoxtitle, friendsHBox, this.addButton, vBoxtitleRemove, removefriendsHBox, this.buttonRemoveFriend);


        //ToolBar
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(10, 0, 15, 20));
        toolBar.getItems().add(this.buttonHome);
        toolBar.getItems().add(this.buttonSendIncidentCommunity);
        toolBar.getItems().add(this.buttonAddFriend);
        toolBar.getItems().add(this.buttonLocalization);
        toolBar.getItems().add(this.buttonMessageList);
        toolBar.getItems().add(this.settingsButton);
        toolBar.getItems().add(this.logoutButton);
        VBox vBox = new VBox(toolBar);

        BorderPane bp = new BorderPane();
        bp.setTop(vBox);
        bp.setCenter(btnVBox);

        this.friendsScene = new Scene(bp, WIDTH, HEIGHT);
        this.friendsStage.setScene(this.friendsScene);
        this.friendsStage.show();

    }

    void styleButtons() {
        //buttons
        this.buttonHome = new Button("Home");
        this.buttonHome.setMaxWidth(150);
        this.buttonHome.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonLocalization = new Button("Localização");
        this.buttonLocalization.setMaxWidth(150);
        this.buttonLocalization.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonSendIncidentCommunity = new Button("ChatComunidade");
        this.buttonSendIncidentCommunity.setMaxWidth(150);
        this.buttonSendIncidentCommunity.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonAddFriend = new Button("ChatAmigos");
        this.buttonAddFriend.setMaxWidth(150);
        this.buttonAddFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonMessageList = new Button("MessageList");
        this.buttonMessageList.setMaxWidth(150);
        this.buttonMessageList.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.settingsButton = new Button("Def Raio");
        this.settingsButton.setMaxWidth(150);
        this.settingsButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.updateButton = new Button("Atualizar localização");
        this.updateButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.updateButton.setMaxWidth(200);

        // Logout button
        this.logoutButton = new Button("Logout");
        this.logoutButton.setMaxHeight(20);
        this.logoutButton.setStyle("-fx-background-color:#A52A2A;-fx-text-fill: white;-fx-font-size: 16px;");

        this.addButton = new Button("Adicionar");
        this.addButton.setMaxWidth(150);
        this.addButton.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

        this.buttonRemoveFriend = new Button("Remover");
        this.buttonRemoveFriend.setMaxWidth(150);
        this.buttonRemoveFriend.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");

    }

    /**
     * Exibe alerta de erro a adicionar amigo
     */
    public void displayMSG() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erro");
        alert.setHeaderText("Amigo não adicionado");
        alert.setContentText("Amigo não existe ou já está adicionado!");
        alert.showAndWait();
    }

    /**
     * Exibe alerta de amigo adicionado com sucesso
     */
    public void displayMSGFriendADD() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText("Amigo  adicionado");
        alert.setContentText("Amigo pode enviar mensagens pra ele");
        alert.showAndWait();
    }

    public void displayMSGFriendRemoved() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText("Amigo  removido");
        alert.setContentText("Não enviará mais mensagens pra este user");
        alert.showAndWait();
    }

    /**
     * Define as ações que os botões devem executar quando clicados
     */
    public void listenerStyleButtons() {

        this.addButton.setOnAction(e -> {
            Boolean added = false;
            if (!inputAmigo.getText().isEmpty()) {
                for (int i = 0; i < userAplication.size(); i++) {
                    if (inputAmigo.getText().equals(userAplication.get(i).getUserName())) {
                        if (!isFriend(userAplication.get(i).getId()) && d.getId() != userAplication.get(i).getId()) { //Adiciona amigos
                            d.getFriendsList().add(userAplication.get(i).getId());
                            this.listViewFriends.getItems().add(getUsername(userAplication.get(i).getId()));
                            added = true;
                            inputAmigo.setText("");
                            displayMSGFriendADD();
                        }
                    }
                }
                if (added == false) {
                    displayMSG();
                } else {
                    saveFriendsEdits(); // guarda a informação dos gajos
                }
            }
        });

        this.buttonRemoveFriend.setOnAction(e -> {

            if (!inputRemoveAmigo.getText().isEmpty()) {
                int idRemover = getID(inputRemoveAmigo.getText());
                if (idRemover != -1) {
                    d.getFriendsList().remove(idRemover);
                    inputRemoveAmigo.setText("");
                    updateListViewWhenFriendRemoved();
                    displayMSGFriendRemoved();
                    saveFriendsEdits(); // guarda a informação dos gajos
                }

            } else {
                displayMSG();
            }

        });

        this.buttonLocalization.setOnAction(e -> {
            try {
                LocalizationWindow localizationWindow = new LocalizationWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                localizationWindow.start(this.friendsStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        this.buttonSendIncidentCommunity.setOnAction(e -> {
            try {
                ChatCommunityWindow ccWindow = new ChatCommunityWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                ccWindow.start(this.friendsStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonAddFriend.setOnAction(e -> {
            try {
                ChatFriendsWindow cfWindow = new ChatFriendsWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                cfWindow.start(this.friendsStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonHome.setOnAction(e -> {
            try {
                HomeWindow hWindow = new HomeWindow(socket, d, lmd, driverSettings, rmb, receiverMessagesServer);
                hWindow.start(this.friendsStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.buttonMessageList.setOnAction(e -> {
            try {
                MessageListWindow mlWindow = new MessageListWindow(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                mlWindow.start(this.friendsStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        this.settingsButton.setOnAction(e -> {
            try {
                SettingArea sa = new SettingArea(socket, lmd, d, driverSettings, rmb, receiverMessagesServer);
                sa.start(this.friendsStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });


        this.logoutButton.setOnAction(e -> {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(this.d.toString());
                rmb.setListening();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Login loginMenu = new Login();
            try {
                loginMenu.start(this.friendsStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    /**
     * Obtém o id de um condutor dado o seu username
     *
     * @param username a procurar
     * @return retorna o id do condutor, -1 caso não exista
     */
    public int getID(String username) {
        for (int j = 0; j < userAplication.size(); j++) {
            if (userAplication.get(j).getUserName().equals(username)) {
                return userAplication.get(j).getId();
            }
        }
        return -1;
    }

    /**
     * Obtém o username de um condutor dado o seu id
     *
     * @param id id a procurar
     * @return username do condutor, null caso não exista
     */
    public String getUsername(int id) {
        for (int j = 0; j < userAplication.size(); j++) {
            if (userAplication.get(j).getId() == id) {
                return userAplication.get(j).getUserName();
            }
        }
        return null;
    }

    /**
     * Atualiza a lista de amigos de um condutor
     */
    public void updateListView() {
        //Add amigos
        this.inputAmigo = new TextField();

        this.listView = new ListView();
        this.listView.setMaxWidth(100);
        this.listView.setMaxHeight(40);
        for (int i = 0; i < userAplication.size(); i++) {
            if (!userAplication.get(i).equals(d.getUserName())) {
                this.listView.getItems().add(userAplication.get(i).getUserName());
            }

        }
        //Remove
        this.listViewFriends = new ListView();
        this.listViewFriends.setMaxWidth(100);
        this.listViewFriends.setMaxHeight(40);
        this.inputRemoveAmigo = new TextField();
        for (int i = 0; i < d.getFriendsList().getSize(); i++) {
            this.listViewFriends.getItems().add(getUsername((Integer) d.getFriendsList().getIndex(i)));
        }

    }

    /**
     * Atualiza a lista de amigos quando é o utilizador remove um amigo
     */
    public void updateListViewWhenFriendRemoved() {
        this.listViewFriends.getItems().clear();
        for (int i = 0; i < d.getFriendsList().getSize(); i++) {
            this.listViewFriends.getItems().add(getUsername((Integer) d.getFriendsList().getIndex(i)));
        }
    }

    /**
     * Verifica através de um id se dois condutores já são amigos
     *
     * @param id id do condutor
     * @return true se amigo estiver na lista, false caso contrário
     */
    public boolean isFriend(int id) {
        for (int i = 0; i < d.getFriendsList().getSize(); i++) {
            if (d.getFriendsList().getIndex(i).equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Esta função  guarda os dados do condutor quando este fecha a aplicação
     */
    public void saveFriendsEdits() {
        ///informa nodo para escrever no ficheiro
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        String list = "";
        for (int j = 0; j < d.getFriendsList().getSize(); j++) {
            list = list + d.getFriendsList().getIndex(j) + ";";
        }
        String dataDriver = d.getId() + ";" + d.getNome() + ";" + d.getUserName() + ";" + d.getLatitude() + ";" + d.getLongitude() + ";" + d.getPassword() + ":" + list;
        out.println("SAVEDRIVER;" + dataDriver);
    }
}

