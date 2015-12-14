package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableModel<E extends PathfinderListener> extends PathfinderListenable<E> {

    public SubscribableModel(String path) {
        super(path);
    }

    public JsonObject getMessageHeader(String type) {
        JsonObject json = new JsonObject();

        json.addProperty("type", type);
        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());

        return json;
    }

    public void subscribe(JsonObject value) {
        JsonObject json = this.getMessageHeader("subscribe");
        PathfinderConnection.getConnection().sendMessage(json.toString());
    }

    public void unsubscribe() {
        //TODO implement
        //Not implemented on server
    }

    public void routeSubscribe() {
        JsonObject json = this.getMessageHeader("routeSubscribe");
        PathfinderConnection.getConnection().sendMessage(json.toString());
    }

    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
