package Server;

import ModelClass.Driver;
import ModelClass.SynchronizedArrayList;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe BroadcastSender responsável pelo envio dos avisos da proteção civil para todos os condutores
 */

public class BroadcastSender extends Thread {

    private boolean listening = true;
    private DatagramSocket broadcastSocket;
    private final String BROADCAST_ADDRESS = "230.0.0.1";
    private final int PORT_NUMBER = 3000;
    private SynchronizedArrayList<Worker> listOFAllDrivers;

    public BroadcastSender(SynchronizedArrayList<Worker> drivers ) {
        try {
            this.broadcastSocket = new DatagramSocket();
            this.broadcastSocket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.listOFAllDrivers = drivers;
    }


    @Override
    public void run() {
        System.out.println("Servidor Broadcast iniciado!");
        while (listening) {
            if (!listOFAllDrivers.isEmpty()) { //Não enviar se tiver vazio
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    String serverMsg = gerarSMS();
                    byte[] buf = serverMsg.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(BROADCAST_ADDRESS), PORT_NUMBER);
                    this.broadcastSocket.send(packet);

                } catch (UnknownHostException | SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{ //Verifica de 30 em 30 segundos se existe novo cliente
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        this.broadcastSocket.close();
    }

    /**
     * Gera um número inteiro entre 1 e 4
     *
     * @return retorna o número gerado
     */
    public int geraValor() {
        Random r = new Random();
        int low = 1;
        int high = 4;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    /**
     * Escolhe uma mensagem para enviar aos clientes
     *
     * @return mensagem a enviar
     */
    public String gerarSMS() {
        int escolha = geraValor();
        String sms = "";

        switch (escolha) {
            case 1:
                sms = "Se beber por favor não conduza !";
                break;
            case 2:
                sms = "Com Chuva intensa modere a velocidade!";
                break;
            default:
                sms = "Não exceda o limite de velocidade!";
                break;

        }
        return sms;
    }
}