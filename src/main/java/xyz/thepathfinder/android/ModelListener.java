package xyz.thepathfinder.android;

/**
 * An interface for Listener classes to implement.
 *
 * @param <T> Type of the model receiving the {@link ModelListener} updates.
 * @author David Robinson
 */
public class ModelListener<T extends Model> implements Listener {

    /**
     * Invoked when the model receives a connected message.
     *
     * @param model connected to, with updated fields.
     */
    public void connected(T model) {
    }

    /**
     * Invoked when the model receives a created message.
     *
     * @param model created, with updated fields.
     */
    public void created(T model) {
    }

    /**
     * Invoked when the model receives a deleted message.
     *
     * @param model deleted to, with updated fields.
     */
    public void deleted(T model) {
    }

    /**
     * Invoked when the model receives a error message.
     *
     * @param error message as a String.
     */
    public void error(String error) {
    }

    /**
     * Invoked when the model receives an updated message.
     *
     * @param model updated, with updated fields.
     */
    public void updated(T model) {
    }

    /**
     * Invoked when the model receives a route subscribed message.
     *
     * @param model route subscribed.
     */
    public void routeSubscribed(T model) {
    }

    /**
     * Invoked when the model receives a route unsubscribed message.
     *
     * @param model route unsubscribed.
     */
    public void routeUnsubscribed(T model) {
    }

    /**
     * Invoked when the model receives a subscribed message.
     *
     * @param model subscribed to.
     */
    public void subscribed(T model) {
    }

    /**
     * Invoked when the model receives an unsubscribed message.
     *
     * @param model unsubscribed from.
     */
    public void unsubscribed(T model) {
    }
}
