package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

/**
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableModel<E extends Listener<? extends Model>> extends Model<E> {

    public SubscribableModel(String path, PathfinderServices services) {
        super(path, services);
    }

    public JsonObject getMessageHeader(String type) {
        JsonObject json = new JsonObject();

        json.addProperty("message", type);
        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());

        return json;
    }

    public void subscribe() {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("subscribe");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    public void unsubscribe() {
        //TODO implement
        //Not implemented on server
    }

    public void routeSubscribe() {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("routeSubscribe");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
