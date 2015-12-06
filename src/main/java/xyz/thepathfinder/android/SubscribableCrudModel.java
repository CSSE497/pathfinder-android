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

        JsonObject requestJson = new JsonObject();
        requestJson.add("read", model);

        this.getConnection().sendMessage(requestJson.toString());
    }

    public void create() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.add("value", this.toJson());

        JsonObject requestJson = new JsonObject();
        requestJson.add("create", model);

        this.getConnection().sendMessage(requestJson.toString());
    }

    public void delete() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("id", this.getId());

        JsonObject requestJson = new JsonObject();
        requestJson.add("delete", model);

        this.getConnection().sendMessage(requestJson.toString());
    }

    public void update(JsonObject value) {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("id", this.getId());
        model.add("value", value);

        JsonObject requestJson = new JsonObject();
        requestJson.add("update", model);

        this.getConnection().sendMessage(requestJson.toString());
    }
}
