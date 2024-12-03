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

/**
 * Classe responsável por iniciar uma thread que receberá mensagens UDP ( broadcast) do servidor
 */
public class ReceiverMessageBroadcast extends Thread {

    private MulticastSocket multicastSocket;

    private boolean listening = true;
    private Stage stage;
    private final String BROADCAST_ADDRESS = "230.0.0.1";
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ReceiverMessageBroadcast(MulticastSocket multicastSocket, Stage stage) {
        super("ReceiverMessageBroadcast");
        this.multicastSocket = multicastSocket;
        this.stage = stage;
    }

    @Override
    public void run() {
        while (listening) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                this.multicastSocket.receive(datagramPacket);

                if (datagramPacket != null) {
                    String serverMsgReceived = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                    if (listening) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Scene scene = stage.getScene();
                                Parent pane = scene.getRoot();
                                NotificationPane notificationPane = new NotificationPane(pane);
                                notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
                                notificationPane.setText(serverMsgReceived);
                                scene = new Scene(notificationPane, scene.getWidth(), scene.getWidth());
                                stage.setScene(scene);
                                notificationPane.show();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            multicastSocket.leaveGroup(InetAddress.getByName(BROADCAST_ADDRESS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.multicastSocket.close();
        this.interrupt();
    }

    public void setListening() {
        this.listening = false;
    }

}