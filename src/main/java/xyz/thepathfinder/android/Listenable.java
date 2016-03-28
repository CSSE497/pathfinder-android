package xyz.thepathfinder.android;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows objects to listen for notifications.
 *
 * @param <E> the listener object type
 * @param <T> the type of the object received by the listener in {@link Listenable#notifyUpdate(String, Object)}
 * @author David Robinson
 */
public abstract class Listenable<E extends Listener, T> {

    /**
     * The list of listeners
     */
    private List<E> listeners;

    /**
     * Creates a listenable object.
     */
    public Listenable() {
        this.listeners = new LinkedList<E>();
    }

    /**
     * Adds a listener to the object.
     *
     * @param listener to add.
     */
    public void addListener(E listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener from the object.
     *
     * @param listener to remove.
     */
    public void removeListener(E listener) {
        this.listeners.remove(listener);
    }

    /**
     * Returns a list of all the current listeners.
     *
     * @return a list of listeners.
     */
    public List<E> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    /**
     * Method called when an update occurs.
     *
     * @param reason for notifying the model.
     * @param arg    object of the updated model.
     * @return <tt>true</tt> if the model was updated, <tt>false</tt> otherwise.
     */
    protected abstract boolean notifyUpdate(String reason, T arg);
}
