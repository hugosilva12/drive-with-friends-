package ModelClass;

import java.util.ArrayList;

/**
 * Classe responsável por armazenar a lista de mensagens de um condutor
 */
public class ListMessageDriver {

    private ArrayList<HistoryMessage> hm;

    public ListMessageDriver() {
        this.hm = new ArrayList<HistoryMessage>();
    }

    public ArrayList<HistoryMessage> getHm() {
        return hm;
    }

    public int size() {
        return hm.size();
    }

    public HistoryMessage getIndex(int index) {
        return this.hm.get(index);
    }
    public void removeIndex(int index) {
         this.hm.remove(index);
    }
    public void setHm(ArrayList<HistoryMessage> hm) {
        this.hm = hm;
    }

    public void addHm(HistoryMessage hm) {
        this.hm.add(hm);
    }
}
