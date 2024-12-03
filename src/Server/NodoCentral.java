package Server;

import ModelClass.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Protocolo de comunicação responsável pela gestão da aplicação, é nesta classe que é validado o login e o registo.
 * São também criados os grupos Broadcast e Multicast para envio de notificações
 */
public class NodoCentral {

    private ArrayList<Driver> listOFAllDrivers;
    private ArrayList<HistoryMessage> listOfMessages;
    private FileJsonOperations file;
    private SynchronizedArrayList<Worker> drivers;
    private ArrayList<DriverSettings> arrayListDriverSettings;
    private TrafficDensity trafficDensity;
    private NumberIncidents numberIncidents;

    /**
     * Construtor da classe NodoCentral
     *
     * @param file    objeto que permite a leitura e escrita em ficheiros
     * @param drivers lista de condutores conectados
     */
    public NodoCentral(FileJsonOperations file, SynchronizedArrayList<Worker> drivers) {
        this.file = file;
        this.listOfMessages = new ArrayList<HistoryMessage>();
        this.drivers = drivers;
        new BroadcastSender(drivers).start();
        this.arrayListDriverSettings = new ArrayList<DriverSettings>();
        try {
            this.listOFAllDrivers = file.getAllUsernamesJsonFile();
            this.trafficDensity = new TrafficDensity(drivers, listOFAllDrivers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.numberIncidents = new NumberIncidents(drivers, arrayListDriverSettings, file);
        this.numberIncidents.start();

        this.trafficDensity.start();
    }

    /**
     * Classe responsável por executar a função do nodo de acordo com a solicitação enviada pelo driver
     *
     * @param methode método a executar no nodo central
     * @param message mensagem enviada pelo driver
     * @param drivers lista de drivers conectados
     * @return resposta para o cliente
     * @throws IOException caso alguma operação de leitura/escrita de ficheiro falhe
     */
    public String processInput(String methode, String message, SynchronizedArrayList<Worker> drivers) throws IOException {

        if (methode.equals("FORALL")) { // envia pra comunidade
            Driver driver = null;
            String[] dev = message.split(";");
            try {
                synchronized (file) {
                    driver = file.getDriverForNode(dev[1]);
                }
                synchronized (file) {
                    listOFAllDrivers = file.getAllUsernamesJsonFile();
                }
                synchronized (file) {
                    this.arrayListDriverSettings = file.getAllSettingsDriver();
                }

                writeIncident(driver);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            getKMSDistance(driver, dev[2], drivers);
            sendMessageForAllDrivers(dev[2], "230.0.0.2");
            writeMessageToFile(listOfMessages);
            listOfMessages.removeAll(listOfMessages);
            return "MESSAGESEND/";


        } else if (methode.equals("SAVEDRIVER")) { // guardar alterações do driver
            saveInfoDrivers(message);
            return "INFOSAVE/";

        } else if (methode.equals("LOGIN")) { // login

            return "LOGIN/" + verifyLoginDriver(message);

        } else if (methode.equals("REGISTAR")) { // registar

            return "REGISTAR/" + registDriver(message);

        } else if (methode.equals("DEFINEAREA")) { //Guarda novos dados preferencias
            String[] dev = message.split(";");
            defineAreaNotification(message);
            return "LOADSETTING/" + getAreaNotification(dev[1]);

        } else if (methode.equals("LOADMESSAGE")) {//Carrega mensagens do user

            String[] dev = message.split(";");
            try {
                return "LOADMESSAGE/" + getMessageList(dev[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (methode.equals("FORFRIENDS")) { // envia pros amigos
            Driver driver = null;
            String[] dev = message.split(";");

            driver = file.getDriverForNode(dev[1]);

            listOFAllDrivers = file.getAllUsernamesJsonFile();

            //System.out.println("Friends" + driver.getNumberFriends());

            sendMessageFriends(driver, dev[2], drivers);

            sendMessageForAllDrivers(dev[2], "230.0.0.3");
            writeMessageToFile(listOfMessages);
            listOfMessages.removeAll(listOfMessages);
            return "MESSAGESEND/";

        } else if (methode.equals("LOADSETTING")) { //Preferencias do driver

            String[] dev = message.split(";");
            return "LOADSETTING/" + getAreaNotification(dev[1]);

        } else if (methode.equals("CHANGESTATUSMESSAGE")) {
            String[] dev = message.split(";");
            updateStateMessages(dev[1]);
            try {
                return "LOADMESSAGE/" + getMessageList(dev[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "null";
    }

    /**
     * Guarda a informação de um driver em ficheiro
     *
     * @param infoDriver informação do condutor
     */
    public void saveInfoDrivers(String infoDriver) {

        String[] dados = infoDriver.split(":");
        String[] dev = dados[0].split(";");

        String[] amigos = dados[1].split(";");

        SynchronizedArrayList list = new SynchronizedArrayList();
        if (!dados[1].equals("null")) {
            for (int j = 0; j < amigos.length; j++) {
                list.add(Integer.valueOf(amigos[j]));
            }
        }
        Driver driver = new Driver(dev[2], dev[3], Double.valueOf(dev[4]), Double.valueOf(dev[5]), dev[6], list);
        int idax = Integer.parseInt(dev[1]);
        driver.setId(idax);

        try {
            file.updateDataDriver(driver);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Seleciona os condutores que irão receber a notificação de um incidente reportado para a comunidade
     *
     * @param driver  driver que emitiu acidente
     * @param message mensagem enviada pelo driver
     * @param drivers lista de drivers conectados
     */
    public synchronized void getKMSDistance(Driver driver, String message, SynchronizedArrayList<Worker> drivers) {
        Double distanciaBetweenDrivers;
        ArrayList<Integer> lista = new ArrayList<Integer>();
        ArrayList<Integer> notOnlineDrivers = new ArrayList<Integer>();
        ArrayList<Integer> onlineDrivers = new ArrayList<Integer>();
        for (int i = 0; i < listOFAllDrivers.size(); i++) {

            if (driver.getId() != listOFAllDrivers.get(i).getId()) {
                distanciaBetweenDrivers = distance(driver.getLatitude(), driver.getLongitude(), listOFAllDrivers.get(i).getLatitude(), listOFAllDrivers.get(i).getLongitude());
                System.out.println("Distancia  " + distanciaBetweenDrivers);
                System.out.println(String.valueOf(listOFAllDrivers.get(i).getId()) + "     Distancia a receber " + getDistance(String.valueOf(listOFAllDrivers.get(i).getId())));
                if (distanciaBetweenDrivers < getDistance(String.valueOf(listOFAllDrivers.get(i).getId()))) {
                    lista.add(listOFAllDrivers.get(i).getId());
                }
            }
        }
        Boolean isOnline = false;
        for (int i = 0; i < lista.size(); i++) {
            isOnline = false;
            for (int j = 0; j < drivers.getSize(); j++) {
                if (lista.get(i) == drivers.getIndex(j).getId()) {
                    isOnline = true;
                    onlineDrivers.add(lista.get(i)); //Juntam-se ao grupo multicast para receber a mensagem
                    drivers.getIndex(j).out.println("Nova Mensagem do driver" + driver.getId());
                }
            }
            if (!isOnline) {
                notOnlineDrivers.add(lista.get(i));
            }
        }

        buildMessageList(driver.getUserName(), message, onlineDrivers, notOnlineDrivers);
    }

    /**
     * Cria lista de mensagens a guardar em ficheiro, selecionando os clientes que foram notificados(online no momento) e os que não foram
     *
     * @param user           emissor da mensagem
     * @param message        mensagem
     * @param onlineDrivers  lista de condutores online
     * @param offlineDrivers lista de condutores offline
     */
    public void buildMessageList(String user, String message, ArrayList<Integer> onlineDrivers, ArrayList<Integer> offlineDrivers) {

        for (int i = 0; i < onlineDrivers.size(); i++) {
            HistoryMessage hm = new HistoryMessage(onlineDrivers.get(i), user, message, true);
            listOfMessages.add(hm);
        }

        for (int i = 0; i < offlineDrivers.size(); i++) {
            HistoryMessage hm = new HistoryMessage(offlineDrivers.get(i), user, message, false);
            listOfMessages.add(hm);
        }

    }

    /**
     * Obtem a distância em kms entre duas coordenadas
     *
     * @param lat1 latitude de uma localização
     * @param lon1 longitude de uma localização
     * @param lat2 latitude da segunda localização
     * @param lon2 longitude da segunda localização
     * @return distância em kms
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    /**
     * Envia a mensagem recebida de um cliente para todos os users que estejam a menos de 1 km dele
     *
     * @param message mensagem a enviar
     */
    public void sendMessageForAllDrivers(String message, String port) {
        //Thread
        MulticastSender multicastSender = new MulticastSender(message, port);
        multicastSender.start();

        try {//Espera terminar o envio
            multicastSender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para cálculo da distância
     *
     * @param deg valor para efetuar operação matemática
     * @return resultado da operação
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Método auxiliar para cálculo da distância
     *
     * @param rad valor para efetuar operação matemática
     * @return resultado da operação
     */
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Esta função cria, se possível, uma conta para aceder à aplicação
     *
     * @param infoDriver informação inserida pelo utilizador
     * @return 1 caso conta seja criada com sucesso, 0 se username já existir na aplicação
     */
    public int registDriver(String infoDriver) {
        Boolean isRegisted = null;
        Driver d = null;
        //Obtem dados
        String[] dev = null;
        dev = infoDriver.split(";");
        String username = dev[1];
        String name = dev[2];
        String password = dev[3];
        String double1 = dev[4];
        String double2 = dev[5];

        Double latitude = Double.parseDouble(double1);
        Double longitude = Double.parseDouble(double2);
        SynchronizedArrayList listFriends = new SynchronizedArrayList();
        d = new Driver(name, username, latitude, longitude, password, listFriends);

        try {
            isRegisted = file.addDriverToJsonFile(d);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        //Informa se registou com suceso
        if (isRegisted) {
            return 1;
        } else {
            return 0;
        }

    }

    /**
     * Esta função verifica se os dados inseridos por um utilizador são válidos para autenticação na aplicação
     *
     * @param infoDriver informação inserida pelo utilizador
     * @return retorna os dados de utilizador caso o login seja válido, null caso contrário.
     */
    public String verifyLoginDriver(String infoDriver) {
        Driver driver;
        //Obtem dados
        String[] dev = null;
        dev = infoDriver.split(";");


        synchronized (file) {
            driver = file.existUser(dev[1], dev[2]);
        }

        if (driver == null) {
            return "null";
        } else {
            String list = "";
            if (driver.getFriendsList().getSize() == 0) {
                list = "null";
            }
            for (int i = 0; i < driver.getFriendsList().getSize(); i++) {
                list = list + driver.getFriendsList().getIndex(i) + ";";
            }
            return driver.getId() + ";" + driver.getNome() + ";" + driver.getUserName() + ";" + driver.getLatitude() + ";" + driver.getLongitude() + ";" + driver.getPassword() + ":" + list;
        }
    }

    /**
     * Escreve a lista de mensagens em ficheiro
     *
     * @param hm lista de mensagens a escrever
     * @throws IOException execção caso algo ocorra durante a escrita em ficheiro
     */
    public void writeMessageToFile(ArrayList<HistoryMessage> hm) throws IOException {
        if (!hm.isEmpty()) {
            file.writeMessageToJSONFile(hm);
        }
    }

    /**
     * Obtem a lista de mensagens de um driver
     *
     * @param id id do cliente que se pretende obter a lista
     * @return lista de mensagens
     * @throws IOException    falha na leitura do ficheiro
     * @throws ParseException falha na leitura da lista
     */
    public String getMessageList(String id) throws IOException, ParseException {
        JSONArray lista = null;
        lista = file.getMessageList();
        String listaMensagens = "list;";
        if (lista != null) {
            for (int k = 0; k < lista.size(); k++) {
                JSONObject driver;
                driver = (JSONObject) lista.get(k);

                if (driver.get("id").toString().equals(id)) {
                    listaMensagens = listaMensagens + driver.get("id") + ";" + driver.get("username") + ";" + driver.get("isNotified") + ";" + driver.get("message") + ":" + "list;";
                }
            }

        }
        return listaMensagens;
    }

    /**
     * Envia mensagens de um driver para os seus amigos via multicast
     *
     * @param driver  emissor da mensagem
     * @param message mensagem a enviar
     * @param drivers lista de condutores atualmente conectados
     */
    public synchronized void sendMessageFriends(Driver driver, String message, SynchronizedArrayList<Worker> drivers) {
        Boolean isOnline = false;
        ArrayList<Integer> notOnlineDrivers = new ArrayList<Integer>();
        ArrayList<Integer> onlineDrivers = new ArrayList<Integer>();
        for (int i = 0; i < driver.getFriendsList().getSize(); i++) {
            for (int j = 0; j < drivers.getSize(); j++) {
                if ((int) driver.getFriendsList().getIndex(i) == (int) drivers.getIndex(j).getId()) {
                    isOnline = true;
                    onlineDrivers.add((int) drivers.getIndex(j).getId());
                    drivers.getIndex(j).out.println("friends;230.0.0.3;" + driver.getId());
                }
            }
            if (!isOnline) {
                notOnlineDrivers.add((int) driver.getFriendsList().getIndex(i));
            }
        }
        buildMessageList(driver.getUserName(), message, onlineDrivers, notOnlineDrivers);
    }

    /**
     * Atualiza o estado das mensagens de um driver para "lidas"
     *
     * @param id id do driver do qual o estado das mensagens será atualizado
     */
    public void updateStateMessages(String id) {
        try {
            file.updateStateMessages(id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Armazena em ficheiro as alterações realizadas às definições pelo driver
     *
     * @param message dados do driver
     */
    public void defineAreaNotification(String message) {
        String[] data = message.split(";");
        try {
            file.writeSettingArea(data[1], data[2], data[3]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega de ficheiro as definições de um driver
     *
     * @param id id do driver a procurar
     * @return caso existam retorna as definições armazenadas no ficheiro, em caso de não existem retorna valores default
     */
    public String getAreaNotification(String id) {
        try {
            return file.getSettingDriver(id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "1;null";
    }

    /**
     * Obtem distância que o driver pretende receber a notificação
     *
     * @param id id do driver a procurar
     * @return distância das preferências, 1 caso não tenha predefenido
     */
    public int getDistance(String id) {
        if (arrayListDriverSettings != null) {
            for (int i = 0; i < arrayListDriverSettings.size(); i++) {
                if (id.equals(arrayListDriverSettings.get(i).getId()))
                    return Integer.parseInt(arrayListDriverSettings.get(i).getKm());

            }
        }
        return 1;
    }

    /**
     * Escreve em ficheiro a cidade e data em que um condutor reportou um incidente para a comunidade
     *
     * @param driver condutor que reportou o acidente
     */
    public void writeIncident(Driver driver) {
        LocalDate myObj = LocalDate.now();
        String longitude = Double.toString(driver.getLongitude());
        String latitude = Double.toString(driver.getLatitude());
        GetCityThread getCityThread = new GetCityThread(file, latitude, longitude);
        getCityThread.start();
    }
}