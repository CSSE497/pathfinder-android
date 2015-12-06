package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class SubscribableCrudModel<E extends PathfinderListener> extends SubscribableModel<E> {

    public SubscribableCrudModel(PathfinderConnection connection) {
        super(connection);
    }

    public void connect() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("id", this.getId());

        JsonObject json = new JsonObject();
        json.add("read", model);

        this.getConnection().sendMessage(json.toString());
    }

    public void create() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.add("value", this.toJson());

        JsonObject json = new JsonObject();
        json.add("create", model);

        this.getConnection().sendMessage(json.toString());
    }

    public void delete() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("id", this.getId());

        JsonObject json = new JsonObject();
        json.add("delete", model);

        this.getConnection().sendMessage(json.toString());
    }

    public void update(JsonObject value) {
        //TODO implement
    }
}
