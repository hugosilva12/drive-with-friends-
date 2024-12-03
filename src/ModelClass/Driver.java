package ModelClass;

/**
 * Classe que armazena os dados de um condutor.
 */

public class Driver {

    private int id;
    private String userName;
    private String nome;
    private String password;
    private double latitude;
    private double longitude;
    private SynchronizedArrayList friendsList;



    public Driver(String nome, String userName, double latitude, double longitude, String password, SynchronizedArrayList friendsList) {
        this.nome = nome;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.friendsList = friendsList;
        this.password = password;

    }
    public Driver(){

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public SynchronizedArrayList getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(SynchronizedArrayList friendsList) {
        this.friendsList = friendsList;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  int getNumberFriends(){
       return this.friendsList.getSize();
    }
    public  int getIndex(int index){
        return (int)this.friendsList.getIndex(index);
    }
    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nome='" + nome + '\'' +
                ", password='" + password + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", friendsList=" + friendsList +
                '}';
    }
}