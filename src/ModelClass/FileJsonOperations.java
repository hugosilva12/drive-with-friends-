package ModelClass;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

/**
 * Classe responsável pelas operações de escrita/leitura de dados no ficheiro
 */
public class FileJsonOperations {

    private final File f, fMessage, fSettings, fIncident;
    private JSONParser jsonParser;
    private FileWriter fWriter;
    private FileReader fileReader;


    public FileJsonOperations() {
        this.jsonParser = new JSONParser();
        this.f = new File("src/UserInfo/Drivers.json");
        this.fMessage = new File("src/UserInfo/Messages.json");
        this.fSettings = new File("src/UserInfo/Settings.json");
        this.fIncident = new File("src/UserInfo/Incidents.json");
    }

    /**
     * Cria um ficheiro
     *
     * @param path caminho onde o ficheiro será criado
     * @return true caso o ficheiro tenha sido criado com sucesso, false caso contrário
     */
    public boolean createFile(String path) {
        try {
            File myObj = new File(path);
            if (myObj.createNewFile()) {
                return true;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método responsável por gravar as informações do condutor num ficheiro JSON
     *
     * @param d dados do condutor
     * @return true se os dados foram escritos com sucesso
     * @throws IOException caso algo falha na escrita em ficheiro
     */
    public synchronized boolean addDriverToJsonFile(Driver d) throws IOException {
        JSONParser jsonParser = new JSONParser();
        boolean exists = f.exists();
        if (exists) {
            try {
                //Obter lista e converte em json array
                JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(f.getPath()));

                JSONArray listDrivers = (JSONArray) obj.get("DriversRegisted");

                //Driver add to json object
                boolean found = verifyIfDriverExistsInFile(d.getUserName(), listDrivers);
                if (found == true) {
                    return false;
                } else {
                    JSONObject newDriver = new JSONObject();
                    newDriver.put("id", listDrivers.size() + 1);
                    newDriver.put("username", d.getUserName());
                    newDriver.put("password", d.getPassword());
                    newDriver.put("name", d.getNome());
                    newDriver.put("latitude", d.getLatitude());
                    newDriver.put("longitude", d.getLongitude());
                    //ADD LIST OF FRIENDS
                    JSONArray listofcontainers = new JSONArray();
                    for (int j = 0; j < d.getNumberFriends(); j++) {
                        listofcontainers.add(d.getIndex(j));
                    }
                    newDriver.put("idFriends", listofcontainers);
                    listDrivers.add(newDriver);
                    JSONObject objWrite = new JSONObject();
                    objWrite.put("DriversRegisted", listDrivers);

                    //Write in file
                    fWriter = new FileWriter(f.getPath());
                    fWriter.write(objWrite.toJSONString());
                    fWriter.close();
                }

            } catch (IOException | org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
        } else {
            boolean iscreate = createFile("src/UserInfo/Drivers.json");
            if (iscreate) {
                JSONArray listDrivers = new JSONArray();
                JSONObject newDriver = new JSONObject();
                newDriver.put("id", listDrivers.size() + 1);
                newDriver.put("username", d.getUserName());
                newDriver.put("password", d.getPassword());
                newDriver.put("name", d.getNome());
                newDriver.put("latitude", d.getLatitude());
                newDriver.put("longitude", d.getLongitude());
                listDrivers.add(newDriver);

                JSONObject objWrite = new JSONObject();
                objWrite.put("DriversRegisted", listDrivers);
                JSONArray listofcontainers = new JSONArray();
                for (int j = 0; j < d.getNumberFriends(); j++) {
                    listofcontainers.add(d.getIndex(j));
                }
                newDriver.put("idFriends", listofcontainers);
                //Write in file
                fWriter = new FileWriter(f.getPath());
                fWriter.write(objWrite.toJSONString());
                fWriter.close();

            }

        }
        return true;
    }

    /**
     * Verifica se existe se já existe um  username com o mesmo nome
     *
     * @param username    username a pesquisar
     * @param listDrivers lista de condutores
     * @return true se estiver, false se não existir
     */
    public synchronized boolean verifyIfDriverExistsInFile(String username, JSONArray listDrivers) {
        int i = 0;
        JSONObject driver;
        while (i < listDrivers.size()) {
            driver = (JSONObject) listDrivers.get(i);
            if (driver.get("username").equals(username)) {
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * Função utilizada para o login, verifica se um username e uma password correspondem a um condutor registado
     *
     * @param contentUsername username a procurar no ficheiro
     * @param contentPassword password a procurar no ficheiro
     * @return dados do condutor, null se os inputs não corresponderem a um condutor
     */
    public synchronized Driver existUser(String contentUsername, String contentPassword) {
        File f = new File("src/UserInfo/Drivers.json");
        if (f.exists()) {
            try {
                JSONObject obj = null;
                obj = (JSONObject) jsonParser.parse(new FileReader(f.getPath()));

                JSONArray arrayOfDrivers = (JSONArray) obj.get("DriversRegisted");

                boolean find = false;
                JSONObject user = null;

                for (int i = 0; !find && i < arrayOfDrivers.size(); i++) {
                    user = (JSONObject) arrayOfDrivers.get(i);

                    if (contentUsername.equals(user.get("username")) && contentPassword.equals(user.get("password")))
                        find = true;
                }

                if (find) {
                    JSONArray listofcontainers;
                    SynchronizedArrayList array = new SynchronizedArrayList();
                    Object objFriends = user.get("idFriends");
                    listofcontainers = (JSONArray) objFriends;
                    for (int i = 0; i < listofcontainers.size(); i++) {
                        Long idDriver = (Long) listofcontainers.get(i);
                        array.add(idDriver.intValue());
                    }

                    Driver driver = new Driver(user.get("name").toString(), user.get("username").toString(), (Double) user.get("latitude"),
                            (Double) user.get("longitude"), user.get("password").toString(), array);
                    int id = Integer.parseInt(user.get("id").toString());
                    driver.setId(id);
                    return driver;
                }

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Obtém todos os utilizadores da aplicação
     *
     * @return lista com todos os condutores registados na aplicação, null de não existir nenhum
     * @throws IOException caso algo falhe durante a leitura de ficheiro
     */
    public synchronized ArrayList<Driver> getAllUsernamesJsonFile() throws IOException {
        JSONObject obj = null;
        if (f.exists()) {
            try {
                obj = (JSONObject) jsonParser.parse(new FileReader(f.getPath()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<Driver> lista = new ArrayList<>();
            JSONArray listDrivers = (JSONArray) obj.get("DriversRegisted");
            for (int i = 0; i < listDrivers.size(); i++) {
                JSONObject user = (JSONObject) listDrivers.get(i);
                Driver driver = new Driver(user.get("name").toString(), user.get("username").toString(), (Double) user.get("latitude"),
                        (Double) user.get("longitude"), user.get("password").toString(), null);
                int id = Integer.parseInt(user.get("id").toString());
                driver.setId(id);
                lista.add(driver);
            }
            return lista;
        }
        return null;
    }

    /**
     * Atualiza em ficheiro os dados de um condutor
     *
     * @param d dados do condutor
     * @throws IOException caso algo falhe durante a escrita em ficheiro
     */
    public synchronized void updateDataDriver(Driver d) throws IOException {
        File f = new File("src/UserInfo/Drivers.json");
        if (f.exists()) {
            //Procura o Driver
            try {
                JSONObject obj = null;
                obj = (JSONObject) jsonParser.parse(new FileReader(f.getPath()));

                JSONArray arrayOfDrivers = (JSONArray) obj.get("DriversRegisted");

                boolean find = false;
                JSONObject user = null;

                for (int i = 0; !find && i < arrayOfDrivers.size(); i++) {
                    user = (JSONObject) arrayOfDrivers.get(i);

                    if (d.getUserName().equals(user.get("username")))
                        find = true;
                }
                //Econtrou
                if (find) {
                    JSONArray listAux;
                    arrayOfDrivers.remove(user);
                    //Creat object
                    JSONObject newDriver = new JSONObject();
                    newDriver.put("id", d.getId());
                    newDriver.put("username", d.getUserName());
                    newDriver.put("password", d.getPassword());
                    newDriver.put("name", d.getNome());
                    newDriver.put("latitude", d.getLatitude());
                    newDriver.put("longitude", d.getLongitude());

                    listAux = new JSONArray();
                    for (int j = 0; j < d.getNumberFriends(); j++) {
                        listAux.add(d.getIndex(j));
                    }
                    newDriver.put("idFriends", listAux);

                    arrayOfDrivers.add(newDriver);
                    JSONObject objWrite = new JSONObject();
                    objWrite.put("DriversRegisted", arrayOfDrivers);

                    //Write in file
                    fWriter = new FileWriter(f.getPath());
                    fWriter.write(objWrite.toJSONString());
                    fWriter.close();
                }

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    /**
     * Carrega de ficheiro a informação de um condutor
     *
     * @param id id do condutor a pesquisar
     * @return objeto condutor, null se não encontrar
     */
    public synchronized Driver getDriverForNode(String id) {
        try {
            JSONObject obj = null;
            obj = (JSONObject) jsonParser.parse(new FileReader(f.getPath()));

            JSONArray arrayOfDrivers = (JSONArray) obj.get("DriversRegisted");

            boolean find = false;
            JSONObject user = null;

            for (int i = 0; !find && i < arrayOfDrivers.size(); i++) {
                user = (JSONObject) arrayOfDrivers.get(i);

                if (id.equals(user.get("id").toString()))
                    find = true;
            }


            if (find == true) {
                JSONArray listofcontainers;
                SynchronizedArrayList array = new SynchronizedArrayList();
                Object objFriends = user.get("idFriends");
                listofcontainers = (JSONArray) objFriends;




                for (int i = 0; i < listofcontainers.size(); i++) {
                    Long idDriver = (Long) listofcontainers.get(i);

                    array.add(idDriver.intValue());
                }

                Driver driver = new Driver(user.get("name").toString(), user.get("username").toString(), (Double) user.get("latitude"),
                        (Double) user.get("longitude"), user.get("password").toString(), array);
                int idax = Integer.parseInt(user.get("id").toString());
                driver.setId(idax);
                return driver;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Escreve uma lista de mensagens em ficheiro JSON
     *
     * @param hm lista de mensagens a escrever
     * @throws IOException caso algo falhe durante a escrita em ficheiro
     */
    public synchronized void writeMessageToJSONFile(ArrayList<HistoryMessage> hm) throws IOException {
        boolean exists = fMessage.exists();
        if (exists) {
            try {
                //Obter lista e converte em json array
                JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fMessage.getPath()));

                JSONArray listDrivers = (JSONArray) obj.get("Message");

                for (int i = 0; i < hm.size(); i++) {
                    JSONObject jObj = new JSONObject();
                    jObj.put("id", hm.get(i).getId());
                    jObj.put("username", hm.get(i).getUsername());
                    jObj.put("message", hm.get(i).getMessage());
                    jObj.put("isNotified", hm.get(i).getNotified());
                    listDrivers.add(jObj);
                }
                JSONObject objWrite = new JSONObject();
                objWrite.put("Message", listDrivers);

                //Write in file
                fWriter = new FileWriter(fMessage.getPath());
                fWriter.write(objWrite.toJSONString());
                fWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            boolean iscreate = createFile("src/UserInfo/Messages.json");
            if (iscreate) {
                JSONArray listDrivers = new JSONArray();
                for (int i = 0; i < hm.size(); i++) {
                    JSONObject jObj = new JSONObject();
                    jObj.put("id", hm.get(i).getId());
                    jObj.put("username", hm.get(i).getUsername());
                    jObj.put("message", hm.get(i).getMessage());
                    jObj.put("isNotified", hm.get(i).getNotified());
                    listDrivers.add(jObj);
                }
                JSONObject objWrite = new JSONObject();
                objWrite.put("Message", listDrivers);
                //Write in file
                fWriter = new FileWriter(fMessage.getPath());
                fWriter.write(objWrite.toJSONString());
                fWriter.close();
            }

        }
    }

    public synchronized JSONArray getMessageList() throws IOException, ParseException {
        boolean exists = fMessage.exists();
        if (exists) {
            fileReader = new FileReader(fMessage.getPath());
            BufferedReader br = new BufferedReader(fileReader);
            //Obter lista e converte em json array
            JSONObject obj = (JSONObject) jsonParser.parse(fileReader);
            JSONArray listDrivers = (JSONArray) obj.get("Message");
            fileReader.close();
            return listDrivers;
        }
        return null;
    }

    /**
     * Marca no ficheiro as mensagens de um condutor como "lidas"
     *
     * @param id id do qual as mensagens serão marcadas como "lidas"
     * @throws IOException    se ocorrer caso algo falhe na escrita/leitura em ficheiro
     * @throws ParseException se ocorrer alguma falha na conversão dos dados do ficheiro para JSON
     */
    public synchronized void updateStateMessages(String id) throws IOException, ParseException {
        boolean exists = fMessage.exists();
        if (exists) {

            //Obter lista e converte em json array
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fMessage.getPath()));

            JSONArray listDrivers = (JSONArray) obj.get("Message");
            JSONObject user;
            if (listDrivers != null) {
                for (int i = 0; i < listDrivers.size(); i++) {
                    user = (JSONObject) listDrivers.get(i);
                    if (user.get("id").toString().equals(id)) {
                        user.put("isNotified", true);
                    }

                }
                JSONObject objWrite = new JSONObject();
                objWrite.put("Message", listDrivers);

                //Write in file
                fWriter = new FileWriter(fMessage.getPath());
                fWriter.write(objWrite.toJSONString());
                fWriter.close();
            }

        }
    }

    /**
     * Escreve as definições do utilzador em ficheiro
     *
     * @param id   id do condutor
     * @param km   raio de quilometros definido pelo utilizador
     * @param list lista de cidades para o qual o utilizador pretende ser notificado
     * @throws IOException    se ocorrer caso algo falhe na escrita/leitura em ficheiro
     * @throws ParseException se ocorrer alguma falha na conversão dos dados do ficheiro para JSON
     */
    public synchronized void writeSettingArea(String id, String km, String list) throws IOException, ParseException {
        boolean exists = fSettings.exists();
        boolean settingsIsExist = false;
        JSONObject userSetting = new JSONObject();
        if (exists) {
            //Obter lista e converte em json array
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fSettings.getPath()));

            JSONArray listDrivers = (JSONArray) obj.get("Settings");
            for (int i = 0; i < listDrivers.size(); i++) {
                userSetting = (JSONObject) listDrivers.get(i);
                if (userSetting.get("id").toString().equals(id)) {
                    userSetting.put("km", km);
                    userSetting.put("zones", list);
                    settingsIsExist = true;
                }
            }
            if (!settingsIsExist) {
                userSetting.put("id", id);
                userSetting.put("km", km);
                userSetting.put("zones", list);
                listDrivers.add(userSetting);
            }

            JSONObject objWrite = new JSONObject();
            objWrite.put("Settings", listDrivers);

            //Write in file
            fWriter = new FileWriter(fSettings.getPath());
            fWriter.write(objWrite.toJSONString());
            fWriter.close();


        } else {
            boolean iscreate = createFile("src/UserInfo/Settings.json");
            if (iscreate) {

                userSetting.put("id", id);
                userSetting.put("km", km);
                userSetting.put("zones", list);
                JSONObject objWrite = new JSONObject();
                JSONArray listSettings = new JSONArray();
                listSettings.add(userSetting);
                objWrite.put("Settings", listSettings);
                //Write in file
                fWriter = new FileWriter(fSettings.getPath());
                fWriter.write(objWrite.toJSONString());
                fWriter.close();
            }


        }
    }

    /**
     * Obtém as definições de um cliente
     *
     * @param id id do cliente
     * @return as definições do cliente se existir, "1;null" caso não existem
     * @throws IOException    caso a operação de leitura do ficheiro falhe
     * @throws ParseException caso algo falhe na conversão para JSON
     */
    public synchronized String getSettingDriver(String id) throws IOException, ParseException {
        boolean exists = fSettings.exists();
        if (exists) {
            JSONObject userSetting = new JSONObject();
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fSettings.getPath()));

            JSONArray listDrivers = (JSONArray) obj.get("Settings");
            for (int i = 0; i < listDrivers.size(); i++) {

                userSetting = (JSONObject) listDrivers.get(i);
                if (userSetting.get("id").toString().equals(id)) {
                    return userSetting.get("km").toString() + ";" + userSetting.get("zones");
                }
            }
        }
        return "1;null";
    }

    public synchronized ArrayList<DriverSettings> getAllSettingsDriver() throws IOException, ParseException {
        boolean exists = fSettings.exists();
        ArrayList<DriverSettings> arrayList = new ArrayList<>();

        if (exists) {
            JSONObject userSetting = new JSONObject();
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fSettings.getPath()));

            JSONArray listDrivers = (JSONArray) obj.get("Settings");
            for (int i = 0; i < listDrivers.size(); i++) {
                DriverSettings driverSettings = new DriverSettings();
                userSetting = (JSONObject) listDrivers.get(i);
                driverSettings.setId(userSetting.get("id").toString());
                driverSettings.setKm(userSetting.get("km").toString());
                driverSettings.setAreas(userSetting.get("zones").toString());
                arrayList.add(driverSettings);
            }
            return arrayList;
        }
        return null;
    }

    public synchronized void writeIncident(String city, String date) throws IOException, ParseException {
        boolean exists = fIncident.exists();

        JSONArray listIncidents = new JSONArray();
        if (exists) {
            JSONObject userSetting = new JSONObject();
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fIncident.getPath()));
            listIncidents = (JSONArray) obj.get("Incidents");

            userSetting.put("city", city);
            userSetting.put("date", date);
            listIncidents.add(userSetting);

            JSONObject objWrite = new JSONObject();
            objWrite.put("Incidents", listIncidents);

            //Write in file
            fWriter = new FileWriter(fIncident.getPath());
            fWriter.write(objWrite.toJSONString());
            fWriter.close();
        } else {
            boolean iscreate = createFile("src/UserInfo/Incidents.json");
            if (iscreate) {
                JSONObject userSetting = new JSONObject();
                userSetting.put("city", city);
                userSetting.put("date", date);
                listIncidents.add(userSetting);

                JSONObject objWrite = new JSONObject();
                objWrite.put("Incidents", listIncidents);


                //Write in file
                fWriter = new FileWriter(fIncident.getPath());
                fWriter.write(objWrite.toJSONString());
                fWriter.close();
            }

        }
    }

    /**
     * Obtém a lista total de incidentes reportados
     *
     * @return JSONArray com todos os incidentes reportados
     * @throws IOException    caso a operação de leitura do ficheiro falhe
     * @throws ParseException caso algo falhe na conversão para JSON
     */
    public synchronized JSONArray getALLIncident() throws IOException, ParseException {
        boolean exists = fIncident.exists();

        JSONArray listIncidents = new JSONArray();
        if (exists) {
            JSONObject userSetting = new JSONObject();
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(fIncident.getPath()));
            return listIncidents = (JSONArray) obj.get("Incidents");

        } else {
            return null;

        }
    }
}
