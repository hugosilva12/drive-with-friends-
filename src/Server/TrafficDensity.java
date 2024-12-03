package Server;

import ModelClass.Driver;
import ModelClass.SynchronizedArrayList;

import java.util.ArrayList;

/**
 * Classe responsável por verificar a densidade de trânsito numa zona e reportar para os condutores
 */
public class TrafficDensity extends Thread {

    private SynchronizedArrayList<Worker> drivers;
    private ArrayList<Integer> notified;
    private ArrayList<Driver> listOFAllDrivers;
    private boolean listening = true;
    private int numeroDrivers = 0;

    public TrafficDensity(SynchronizedArrayList<Worker> drivers, ArrayList<Driver> listOFAllDrivers) {
        this.drivers = drivers;
        notified = new ArrayList<Integer>();
        this.listOFAllDrivers = listOFAllDrivers;
    }

    @Override
    public void run() {
        System.out.println("Thread Tráfico");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (listening) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notified.removeAll(notified);

            if (drivers.getSize() >= 2) { //N
                for (int i = 0; i < drivers.getSize(); i++) {
                    numeroDrivers = 0;
                    if (drivers.getIndex(i).getId() != -1) {
                        for (int j = 0; j < drivers.getSize(); j++) {
                            if (drivers.getIndex(j).getId() != -1) {
                                if (drivers.getIndex(i).getId() != drivers.getIndex(j).getId()) {
                                    Double[] doubles = new Double[2];
                                    doubles = getLocation((int) drivers.getIndex(i).getId());
                                    Double[] doubleslist = new Double[2];
                                    doubleslist = getLocation((int) drivers.getIndex(j).getId());
                                    double distance = distance(doubles[0], doubles[1], doubleslist[0], doubleslist[1]);
                                    if (distance < 30) {
                                        numeroDrivers++;
                                    }
                                    if (numeroDrivers > 0) {
                                        drivers.getIndex(j).out.println("traffic;" + String.valueOf(numeroDrivers));
                                    }

                                }

                            }
                        }

                    }

                }
            }

        }
    }

    /**
     * Retorna as coordenadas dos condutores online
     *
     * @param id id do condutor
     * @return coordenadas do condutor, null caso condutor não exista
     */
    private Double[] getLocation(int id) {
        for (int i = 0; i < listOFAllDrivers.size(); i++) {
            if (id == listOFAllDrivers.get(i).getId()) {
                return new Double[]{listOFAllDrivers.get(i).getLatitude(), listOFAllDrivers.get(i).getLongitude()};
            }

        }
        return null;
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

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}