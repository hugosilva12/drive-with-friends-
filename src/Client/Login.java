package Client;

import ModelClass.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import static javafx.scene.paint.Color.rgb;

/**
 *  Classe responsável por gerir as ações da janela "Registrar"
 */

public class Login extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int BROADCAST_PORT = 3000;


    private static Stage loginWindow;
    private static Scene mainMenuScene;

    private Button btnRegist, btnLogin;

    // Labels of the Page
    private Label lblTitulo, lblUsername, lblPassword;

    // BorderPane of the Page
    private BorderPane borderPane;

    // TextFields of the Page
    private TextField username;
    private PasswordField password;

    private ImageView imageView;

    private Boolean LOGIN = false;

    //Threads
    private ReceiverMessageBroadcast rmb;
    private ReceiverMessagesServer receiverMessagesServer;
    private HomeWindow homeWindow;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        launch(args);
    }

    public void displayMSG() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erro");
        alert.setHeaderText("Falha no Login!");
        alert.setContentText("Verifique a password e o username!");
        alert.showAndWait();
    }

    public void displayMSGInput() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erro");
        alert.setHeaderText("Falha no Login!");
        alert.setContentText("Campos Inválidas");
        alert.showAndWait();
    }

    @Override
    public void start(Stage firstStage) throws FileNotFoundException, Exception {
        this.loginWindow = firstStage;

        this.socket = new Socket("localhost", 2048);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        styleButtons();
        listennerActionsButtons();
        setOnCloseRequest();

        FileInputStream inputstream = new FileInputStream("imgs/1.png");
        Image image = new Image(inputstream);
        this.imageView = new ImageView(image);


        // Sets the image position
        imageView.setX(50);
        imageView.setY(25);
        imageView.setFitHeight(100);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        //Display dos campos de informação do form
        lblTitulo = new Label();
        lblTitulo.setText("Bem Vindo à Drive With Friends!");
        lblTitulo.setTextFill(rgb(255, 0, 0));
        lblTitulo.setFont(new Font(23));
        lblTitulo.setStyle("-fx-font-weight: bold");

        lblUsername = new Label();
        lblUsername.setText("UserName: ");
        lblUsername.setTextFill(Color.BLACK);
        lblUsername.setFont(new Font(17));
        lblUsername.setStyle("-fx-font-weight: bold");

        lblPassword = new Label();
        lblPassword.setText("Password: ");
        lblPassword.setTextFill(Color.BLACK);
        lblPassword.setFont(new Font(17));
        lblPassword.setStyle("-fx-font-weight: bold");

        this.username = new TextField();
        this.password = new PasswordField();

        // Containers
        VBox tituloContainer = new VBox(10);
        tituloContainer.setAlignment(Pos.CENTER);
        tituloContainer.getChildren().addAll(lblTitulo, imageView);

        HBox usernameContainer = new HBox(10);
        usernameContainer.setAlignment(Pos.CENTER);
        usernameContainer.setPadding(new Insets(30, 75, 0, 10));
        usernameContainer.getChildren().addAll(this.lblUsername, this.username);

        HBox passContainer = new HBox(10);
        passContainer.setAlignment(Pos.CENTER);
        passContainer.setPadding(new Insets(0, 75, -30, 10));
        passContainer.getChildren().addAll(this.lblPassword, this.password);


        // Central Buttons Container
        VBox pageButtons = new VBox(50);
        pageButtons.setAlignment(Pos.CENTER);
        pageButtons.getChildren().addAll(usernameContainer, passContainer, this.btnLogin, this.btnRegist, tituloContainer);

        // Border Pane Layout
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(30, 10, 0, 10));
        borderPane.setCenter(pageButtons);
        borderPane.setTop(tituloContainer);
        borderPane.setBackground(new Background(new BackgroundFill(rgb(247, 247, 247), CornerRadii.EMPTY, Insets.EMPTY)));


        //Main Menu Scene
        mainMenuScene = new Scene(borderPane, WIDTH, HEIGHT);
        loginWindow.setScene(mainMenuScene);
        loginWindow.setTitle("Drive With Friends");
        loginWindow.show();

    }

    /**
     * Define as ações que os botões devem executar quando clicados
     */
    void listennerActionsButtons() {

        this.btnLogin.setOnAction(e -> {

            if (username.getText().isEmpty() || this.password.getText().isEmpty()) {
                displayMSGInput();
            } else {

                out.println("LOGIN;" + username.getText() + ";" + this.password.getText() + ";");
                int i = 0;
                String inputLine = null;
                try {
                    while ((inputLine = in.readLine()) != null) {
                        i++;
                        if (i == 1) {
                            break;
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                System.out.println(inputLine);
                if (inputLine.equals("null")) {
                    displayMSG();
                } else {
                    String[] dados = inputLine.split(":");
                    String[] dev = dados[0].split(";");
                    System.out.println("Dados:" + dados[1]);
                    String[] amigos = dados[1].split(";");
                    SynchronizedArrayList list = new SynchronizedArrayList();
                    if (!dados[1].equals("null")) {
                        for (int j = 0; j < amigos.length; j++) {
                            list.add(Integer.valueOf(amigos[j]));
                        }
                    }
                    Driver driver = new Driver(dev[1], dev[2], Double.valueOf(dev[3]),
                            Double.valueOf(dev[4]), dev[5], list);
                    int idax = Integer.parseInt(dev[0]);
                    driver.setId(idax);

                    //Objetos Partilhados
                    ListMessageDriver lmd = new ListMessageDriver();
                    DriverSettings driverSettings = new DriverSettings();
                    //Criar home


                    loadMessages(idax);
                    try {
                        //Conexao Client para BroadCast
                        MulticastSocket driversBroadcastSocket = new MulticastSocket(BROADCAST_PORT);
                        InetAddress groupBroadcast = InetAddress.getByName("230.0.0.1");
                        driversBroadcastSocket.joinGroup(groupBroadcast);


                        //Thread for broadcast
                        rmb = new ReceiverMessageBroadcast(driversBroadcastSocket, loginWindow);

                        this.receiverMessagesServer = new ReceiverMessagesServer(socket, lmd, driverSettings, loginWindow);
                        homeWindow = new HomeWindow(socket, driver, lmd, driverSettings, rmb, receiverMessagesServer);
                        homeWindow.start(loginWindow);
                        rmb.start();
                        receiverMessagesServer.start();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        //Botões
        this.btnRegist.setOnAction(e -> {
            Register registerPage = new Register();
            try {
                registerPage.start(loginWindow);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    /**
     * Inicia e define estilo dos butões
     */
    void styleButtons() {

        this.btnLogin = new Button("Login");
        this.btnLogin.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
        this.btnLogin.setMaxWidth(150);

        this.btnRegist = new Button("Criar Conta");
        this.btnRegist.setMaxWidth(150);
        this.btnRegist.setStyle("-fx-background-color:#FF0000;-fx-text-fill: white;-fx-font-size: 16px;");
    }

    public void setOnCloseRequest() {
        this.loginWindow.setOnCloseRequest(event -> {
            out.println("OFF"); //PARA THREAD
            System.exit(0);
        });
    }

    /**
     * Envia pedido ao nodo para que este envie as mensagens do condutor
     * @param id id do condutor
     */
    public void loadMessages(int id) {
        out.println("LOADMESSAGE;" + id);
    }
}