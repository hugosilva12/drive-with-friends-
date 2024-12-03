package Server;

import ModelClass.FileJsonOperations;
import ModelClass.SynchronizedArrayList;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Classe responsável por iniciar a thread que funciona como servidor TCP
 */

public class Server {

    private static SynchronizedArrayList<Worker> drivers = new SynchronizedArrayList<Worker>();
    private static ServerSocket serverSocket;
    private static final int  port = 2048;
    private static NodoCentral nodoCentral;
    private static FileJsonOperations file;
    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Erro na porta: " + port + ".");
            System.exit(-1);
        }
        file = new FileJsonOperations();

        //Init server for tcp comunications
        ServerThread serverThread =  new ServerThread(serverSocket,drivers,nodoCentral,file);
        serverThread.start();

    }
}