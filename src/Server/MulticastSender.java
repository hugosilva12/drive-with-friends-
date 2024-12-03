package Server;

import java.io.IOException;
import java.net.*;

/**
 * Classe responsável por executar a thread que enviará mensagens multicast aos condutores
 */
public class MulticastSender extends Thread {
    private DatagramSocket datagramSocket;
    private String messageToSend;
    private String IPGroup;

    public MulticastSender(String messageToSend, String IPGroup) {
        try {
            this.datagramSocket = new DatagramSocket(4445);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.messageToSend = messageToSend;
        this.IPGroup = IPGroup;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] buf = messageToSend.getBytes();
        InetAddress group = null;
        try {
            group = InetAddress.getByName(IPGroup);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4000);
        try {
            this.datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.datagramSocket.close();
    }
}

