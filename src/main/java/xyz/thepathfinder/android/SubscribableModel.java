package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableModel<E extends PathfinderListener> extends PathfinderListenable<E> {

    private boolean isSubscribed;
    private PathfinderConnection connection;

    public SubscribableModel(PathfinderConnection connection) {
        this.isSubscribed = false;
        this.connection = connection;
    }

    protected PathfinderConnection getConnection() {
        return this.connection;
    }

    public boolean isSubscribed() {
        return this.isSubscribed;
    }

    public void subscribe() {
        //TODO implement
    }

    public void unsubscribe() {
        //TODO implement
    }

    public abstract boolean isConnected();
    public abstract Long getId();

    protected abstract JsonObject toJson();
    protected abstract String getModel();
}
