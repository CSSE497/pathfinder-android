package xyz.thepathfinder.android;

/**
 * An interface for Listener classes to implement.
 *
 * @author David Robinson
 */
public interface Listener<T extends Model> {
    public void connected(T model);

    public void created(T model);

    public void deleted(T model);

    public void error(String error);

    public void updated(T model);

    public void routeSubscribed(T model);

    public void routeUnsubscribed(T model);

    public void subscribed(T model);

    public void unsubscribed(T model);
}
