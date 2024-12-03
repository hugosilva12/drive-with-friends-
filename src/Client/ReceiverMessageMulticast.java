package Client;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Classe responsável por iniciar uma thread que receberá mensagens UDP (multicast) do servidor
 */
public class ReceiverMessageMulticast extends Thread {
    private MulticastSocket multicastSocket;

    private String group;
    private Stage stage;

    public ReceiverMessageMulticast(MulticastSocket multicastSocket, String group, Stage stage) {
        super("ReceiverMessageBroadcast");
        this.multicastSocket = multicastSocket;
        this.group = group;
        this.stage = stage;
    }

    @Override
    public void run() {
        System.out.println("Server Multicast Iniciado");
        DatagramPacket packet;
        byte[] buf = new byte[100];
        packet = new DatagramPacket(buf, buf.length);
        try {
            multicastSocket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Scene scene = stage.getScene();
                    Parent pane = scene.getRoot();
                    NotificationPane notificationPane = new NotificationPane(pane);
                    notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
                    notificationPane.setText(received);
                    scene = new Scene(notificationPane, scene.getWidth(), scene.getWidth());
                    stage.setScene(scene);
                    notificationPane.show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERRO");
        }
        //Deixa o grupo multicast
        try {
            InetAddress groupMulticast = InetAddress.getByName(group);
            multicastSocket.leaveGroup(groupMulticast);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
