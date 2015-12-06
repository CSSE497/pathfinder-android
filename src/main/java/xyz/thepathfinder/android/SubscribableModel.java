package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableModel<E extends PathfinderListener> extends PathfinderListenable<E> {

    private boolean isSubscribed;
    private PathfinderConnection connection;

    public SubscribableModel(PathfinderConnection connection) {
        this.isSubscribed = false;
        this.connection = connection;
    }

    public abstract boolean isConnected();
    public abstract Long getId();

    protected abstract JsonObject toJson();
    protected abstract String getModel();

    protected PathfinderConnection getConnection() {
        return this.connection;
    }

    public boolean isSubscribed() {
        return this.isSubscribed;
    }

    public void subscribe(JsonObject value) {
        JsonObject subscribeRequest = new JsonObject();
        subscribeRequest.add("subscribe", value);

        this.getConnection().sendMessage(subscribeRequest.toString());
    }

    public void unsubscribe() {
        //TODO implement
        //Not implemented on server
    }

    public void routeSubscribe() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("id", this.getId());

        JsonObject requestJson = new JsonObject();
        requestJson.add("routeSubscribe", model);

        this.getConnection().sendMessage(requestJson.toString());
    }

    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
