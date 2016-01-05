package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

/**
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableCrudModel<E extends Listener<? extends Model>> extends SubscribableModel<E> {

    public SubscribableCrudModel(String path, PathfinderServices services) {
        super(path, services);
    }

    public void connect() {
        JsonObject json = this.getMessageHeader("read");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    protected void create() {
        if (this.isConnected()) {
            throw new IllegalStateException("Already created");
        }

        JsonObject json = this.getMessageHeader("create");
        json.add("value", this.createValueJson());

        this.getServices().getConnection().sendMessage(json.toString());
    }

    public void delete() {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("delete");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    protected void update(JsonObject value) {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("update");
        json.add("value", value);

        this.getServices().getConnection().sendMessage(json.toString());
    }
}
