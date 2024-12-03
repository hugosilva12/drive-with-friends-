package ModelClass;

/**
 * Classe que armazena as definições de um condutor
 */
public class DriverSettings {
    private String id;
    private String km;
    private String areas;

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public synchronized String getKm() {
        return km;
    }

    public synchronized void setKm(String km) {
        this.km = km;
    }
}
