package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableModel<E extends Listener> extends Listenable<E> {

    public SubscribableModel(String path, PathfinderServices services) {
        super(path, services);
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
        this.getServices().getConnection().sendMessage(json.toString());
    }

    public void unsubscribe() {
        //TODO implement
        //Not implemented on server
    }

    public void routeSubscribe() {
        JsonObject json = this.getMessageHeader("routeSubscribe");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
