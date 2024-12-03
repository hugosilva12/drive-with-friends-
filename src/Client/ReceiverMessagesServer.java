package Client;

import ModelClass.DriverSettings;
import ModelClass.HistoryMessage;
import ModelClass.ListMessageDriver;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe responsável por iniciar uma thread que receberá mensagens tcp do servidor
 */
public class ReceiverMessagesServer extends Thread {
    private Socket socket;
    private BufferedReader in;
    private String message;
    private ListMessageDriver lmd;
    private DriverSettings driverSettings;

    private static final int MULTICAST_PORT = 4000;
    private Stage stage;
    private Boolean isRunning = false;


    public ReceiverMessagesServer(Socket socket, ListMessageDriver lmd, DriverSettings driverSettings, Stage stage) throws IOException {
        super("MsgReceiverThread");
        this.lmd = lmd;
        this.socket = socket;
        this.driverSettings = driverSettings;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.stage = stage;
    }

    public void run() {
        try {
            System.out.println("Iniciada a thread message");
            String inputLine;
            this.isRunning = true;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                if (inputLine != null) {
                    if (inputLine.equals("OFF"))
                        break;
                    String[] arraylist = inputLine.split(";");

                    if (arraylist[0].equals("list")) {
                        message = inputLine;
                        getMessageList(inputLine);
                    } else if (arraylist[0].equals("settings")) {
                        this.driverSettings.setId(arraylist[1]);
                        this.driverSettings.setKm(arraylist[2]);
                        this.driverSettings.setAreas(arraylist[3]);

                    } else if (arraylist[0].equals("friends")) {

                        MulticastSocket clientMulticastSocket = new MulticastSocket(MULTICAST_PORT);
                        InetAddress groupMulticast = InetAddress.getByName(arraylist[1]);
                        clientMulticastSocket.joinGroup(groupMulticast);
                        ReceiverMessageMulticast receiverMessageMulticast = new ReceiverMessageMulticast(clientMulticastSocket, arraylist[1], stage);
                        receiverMessageMulticast.start();

                    } else if (arraylist[0].equals("traffic")) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Scene scene = stage.getScene();
                                Parent pane = scene.getRoot();
                                NotificationPane notificationPane = new NotificationPane(pane);
                                notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
                                notificationPane.setText(arraylist[1] + " condutores num raio de 30 kms");
                                scene = new Scene(notificationPane, scene.getWidth(), scene.getWidth());
                                stage.setScene(scene);
                                notificationPane.show();
                            }
                        });
                    } else if (arraylist[0].equals("incidents")) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Scene scene = stage.getScene();
                                Parent pane = scene.getRoot();
                                NotificationPane notificationPane = new NotificationPane(pane);
                                notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
                                notificationPane.setText(arraylist[1] + " incidentes registados hoje em " + arraylist[2]);
                                scene = new Scene(notificationPane, scene.getWidth(), scene.getWidth());
                                stage.setScene(scene);
                                notificationPane.show();
                            }
                        });
                    } else {
                        MulticastSocket clientMulticastSocket = new MulticastSocket(MULTICAST_PORT);
                        InetAddress groupMulticast = InetAddress.getByName("230.0.0.2");
                        clientMulticastSocket.joinGroup(groupMulticast);
                        ReceiverMessageMulticast receiverMessageMulticast = new ReceiverMessageMulticast(clientMulticastSocket, "230.0.0.2", stage);
                        receiverMessageMulticast.start();
                    }
                }
            }
        } catch (UnknownHostException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getMessage() {
        return message;
    }

    public void getMessageList(String dados) {
        String[] array = dados.split(":");
        ArrayList<HistoryMessage> list = new ArrayList();
        for (int i = 0; i < array.length - 1; i++) {
            String[] dev = array[i].split(";");
            HistoryMessage hm = new HistoryMessage(Integer.parseInt(dev[1]), dev[2], dev[4], Boolean.parseBoolean(dev[3]));
            list.add(hm);
        }
        lmd.setHm(list);
    }

    public Boolean getRunning() {
        return isRunning;
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }
}