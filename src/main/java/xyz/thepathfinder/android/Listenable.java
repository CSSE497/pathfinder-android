package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public abstract class Listenable<E extends PathfinderListener> extends Model {

    private List<E> listeners;

    public Listenable(String path, PathfinderServices services) {
        super(path, services);
        this.listeners = new LinkedList<E>();
    }

    public void addListener(E listener) {
        this.listeners.add(listener);
    }

    public void removeListener(E listener) {
        this.listeners.remove(listener);
    }

    public List<E> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    protected abstract void notifyUpdate(JsonObject json);
}
