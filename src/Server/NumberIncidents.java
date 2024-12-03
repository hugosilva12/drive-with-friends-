package Server;

import ModelClass.Driver;
import ModelClass.DriverSettings;
import ModelClass.FileJsonOperations;
import ModelClass.SynchronizedArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Classe responsável por verificar o número de incidentes numa zona e reportar para os condutores
 */
public class NumberIncidents extends Thread {

    private SynchronizedArrayList<Worker> drivers;

    private ArrayList<Driver> listOFAllDrivers;
    private ArrayList<DriverSettings> arrayListDriverSettings;

    private FileJsonOperations file;
    private boolean listening = true;

    public NumberIncidents(SynchronizedArrayList<Worker> drivers, ArrayList<DriverSettings> arrayListDriverSettings, FileJsonOperations file) {
        this.drivers = drivers;
        this.listOFAllDrivers = listOFAllDrivers;
        this.arrayListDriverSettings = arrayListDriverSettings;
        this.file = file;
    }

    @Override
    public void run() {
        System.out.println("Thread number Incidents");
        while (listening) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                arrayListDriverSettings = file.getAllSettingsDriver();
            } catch (IOException e) {
                e.printStackTrace();


            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArray = file.getALLIncident();

                for (int i = 0; i < drivers.getSize(); i++) {
                    if (drivers.getIndex(i).getId() != -1) {
                        String resultado = getUserSetting((int) drivers.getIndex(i).getId());
                        if (resultado != null) {
                            String[] cities = resultado.split(":");
                            for (int y = 0; y < cities.length; y++) {
                                int number = getNumberCity(cities[y], jsonArray);
                                if (number > 0) {
                                    drivers.getIndex(i).out.println("incidents;" + String.valueOf(number) + ";" + cities[y]);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Obtém as definições de um utilizador
     *
     * @param id id do utilizador
     * @return definições do utilizador, null caso não existam
     */
    public String getUserSetting(int id) {
        if (arrayListDriverSettings != null) {
            for (int i = 0; i < arrayListDriverSettings.size(); i++) {
                if (String.valueOf(id).equals(arrayListDriverSettings.get(i).getId())) {
                    return arrayListDriverSettings.get(i).getAreas();
                }
            }
        }
        return null;
    }

    /**
     * Obtém número de incidentes numa cidade num determinado dia
     *
     * @param city      cidade a procurar
     * @param jsonArray lista de incidentes
     * @return número de incidentes reportados
     */
    public int getNumberCity(String city, JSONArray jsonArray) {
        int numberCities = 0;
        LocalDate myObj = LocalDate.now();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject userSetting = new JSONObject();
                userSetting = (JSONObject) jsonArray.get(i);

                if (myObj.toString().equals(userSetting.get("date")) && city.equals(userSetting.get("city"))) {
                    numberCities++;
                }
            }
        }
        return numberCities;
    }
}