package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access to CRUD and subscribe operations.
 *
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableCrudModel<E extends Listener<? extends Model>> extends SubscribableModel<E> {

    private static final Logger logger = LoggerFactory.getLogger(Action.class);
    /**
     * Constructs a subscribable CRUD model.
     *
     * @param path of the model.
     * @param type of the model.
     * @param services a pathfinder services object.
     */
    public SubscribableCrudModel(String path, ModelType type, PathfinderServices services) {
        super(path, type, services);
    }

    /**
     * Reads the model specified by the path from the server.
     */
    public void connect() {
        JsonObject json = this.getMessageHeader("Read");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Creates the model at the path specified on the server.
     *
     * @param value JsonObject that represents the model to create
     * @param checkConnected check if the model is connected.
     */
    protected void create(JsonObject value, boolean checkConnected) {
        if (checkConnected && this.isConnected()) {
            logger.warn("Cannot create connected model " + this.getPathName() + " the model already exists, ignoring request.");
            return;
        }

        JsonObject json = this.getMessageHeader("Create");
        json.addProperty("model", value.get("model").getAsString());

        json.add("value", value);

        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Deletes the model specified by the path on the server.
     */
    public void delete() {
        JsonObject json = this.getMessageHeader("Delete");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Updates the model specified by the path on the server.
     *
     * @param value of the update request.
     */
    protected void update(JsonObject value) {

        JsonObject json = this.getMessageHeader("Update");
        json.add("value", value);

        this.getServices().getConnection().sendMessage(json.toString());
    }
}
