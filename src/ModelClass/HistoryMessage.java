package ModelClass;

/**
 *  Classe responsável pelo armazenamento de uma mensagem
 */

public class HistoryMessage {

    private int id;
    private String username;
    private String message;
    private Boolean isNotified;

    public HistoryMessage(int id, String username, String message, Boolean isNotified) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.isNotified = isNotified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getNotified() {
        return isNotified;
    }

    public void setNotified(Boolean notified) {
        isNotified = notified;
    }

    @Override
    public String toString() {
        return "HistoryMessage{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", isNotified=" + isNotified +
                '}';
    }
}