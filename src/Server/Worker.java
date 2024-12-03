package Server;

import ModelClass.Driver;
import ModelClass.FileJsonOperations;
import ModelClass.SynchronizedArrayList;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe responsável por executar a thread que recebe/envia  mensagens entre nó central e o condutor
 */
public class Worker extends Thread {
    private int id;

    @Override
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Socket socket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;
    private SynchronizedArrayList<Worker> drivers;
    private NodoCentral nodoCentral;
    private FileJsonOperations file;


    public Worker(Socket socket, SynchronizedArrayList<Worker> drivers, NodoCentral nodoCentral, FileJsonOperations file) {
        super("WorkerThread");
        this.socket = socket;
        this.drivers = drivers;
        this.id = -1;
        this.nodoCentral = nodoCentral;
        this.file = file;
    }

    public void run() {

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int size = this.drivers.getSize();
            if (size != 0) {
                String inputLine;
                String[] dev = null;
                while ((inputLine = in.readLine()) != null) {

                    if (inputLine.equals("OFF"))
                        break;

                    dev = inputLine.split(";");

                    //Comunica com o nodo central (protocolo)

                    String respost = nodoCentral.processInput(dev[0], inputLine, drivers);
                    //Resposta do Nodo Central
                    String[] arrayRespost = respost.split("/");

                    if (arrayRespost[0].equals("LOGIN")) {
                        if (!arrayRespost[1].equals("null")) {
                            String[] dados = arrayRespost[1].split(":");
                            String[] idThread = dados[0].split(";");
                            int idax = Integer.parseInt(idThread[0]);
                            this.id = idax;
                        }
                        out.println(arrayRespost[1]);
                    } else if (arrayRespost[0].equals("REGISTAR")) {
                        out.println(arrayRespost[1]);
                    } else if (arrayRespost[0].equals("LOADMESSAGE")) {
                        out.println(arrayRespost[1]);
                    } else if (dev[0].equals("LOADSETTING")) {
                        out.println("settings;" + String.valueOf(this.id) + ";" + arrayRespost[1]);
                    }

                }
                this.drivers.remove(this);
                out.close();
                in.close();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
