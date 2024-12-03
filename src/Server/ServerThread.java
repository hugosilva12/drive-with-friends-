package Server;

import ModelClass.FileJsonOperations;
import ModelClass.SynchronizedArrayList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe  intermedia entre o cliente e o nodo central que permite a comunicação via TCP. Possui suporte para vários clientes
 */
public class ServerThread extends Thread {
    boolean listening = true;
    private ServerSocket serverSocket = null;
    private SynchronizedArrayList<Worker> drivers;
    private NodoCentral nodoCentral;
    private FileJsonOperations file;

    public ServerThread(ServerSocket serverSocket, SynchronizedArrayList<Worker> drivers, NodoCentral nodoCentral, FileJsonOperations file) {
        this.serverSocket = serverSocket;
        this.drivers = drivers;
        this.nodoCentral = nodoCentral;
        this.file = file;
    }


    @Override
    public void run() {
        this.nodoCentral = new NodoCentral(file, drivers);
        while (listening) {
            Worker driver = null;
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                driver = new Worker(socket, drivers, this.nodoCentral, file);
                drivers.add(driver);
                driver.start();
                System.out.println("Cliente conectado:  " + socket.getInetAddress().getHostAddress() + " " + socket.getPort());

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}