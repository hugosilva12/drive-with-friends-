package ModelClass;

import java.util.ArrayList;

/**
 * Classe responsável por sincronizar o acesso a um arraylist.
 *
 * @param <T> - qualquer tipo de dados
 */

public class SynchronizedArrayList<T> {

    private ArrayList<T> arrayList;

    public SynchronizedArrayList() {
        this.arrayList = new ArrayList<>();
    }

    public synchronized ArrayList<T> get() {
        return this.arrayList;
    }

    public synchronized void add(T o) {
        this.arrayList.add(o);
    }

    public synchronized boolean remove(T o) {
        return this.arrayList.remove(o);
    }

    public synchronized int getSize() {
        return this.arrayList.size();
    }

    public synchronized T getIndex(int index) {
        return this.arrayList.get(index);
    }

    public synchronized boolean isEmpty() {
        return this.arrayList.isEmpty();
    }
}