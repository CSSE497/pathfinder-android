package xyz.thepathfinder.android;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PathfinderListenable<E extends PathfinderListener> {

    private List<E> listeners;

    public PathfinderListenable() {
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
}
