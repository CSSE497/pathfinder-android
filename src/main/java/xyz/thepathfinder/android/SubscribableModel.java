package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableModel<E extends PathfinderListener> extends PathfinderListenable<E> {

    private PathfinderConnection connection;

    public SubscribableModel(String path, PathfinderConnection connection) {
        super(path);
        this.connection = connection;
    }

    public Cluster getParent() {
        String parentPath = this.getParentPath();
        return Cluster.getInstance(parentPath, this.getConnection());
    }

    protected PathfinderConnection getConnection() {
        return this.connection;
    }

    public void addMessageReceiver() {
        // FIXME: 12/12/15
    }

    public void removeMessageReceiver() {
        // FIXME: 12/12/15
    }

    public void subscribe(JsonObject value) {
        //TODO uncomment this

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
        model.addProperty("path", this.getPath());

        JsonObject requestJson = new JsonObject();
        requestJson.add("routeSubscribe", model);

        this.getConnection().sendMessage(requestJson.toString());
    }

    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
