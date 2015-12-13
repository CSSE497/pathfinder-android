package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableModel<E extends PathfinderListener> extends PathfinderListenable<E> {

    public SubscribableModel(String path) {
        super(path);
    }

    public Cluster getParent() {
        String parentPath = this.getParentPath();
        return Cluster.getInstance(parentPath);
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

        PathfinderConnection.getConnection().sendMessage(subscribeRequest.toString());
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

        PathfinderConnection.getConnection().sendMessage(requestJson.toString());
    }

    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
