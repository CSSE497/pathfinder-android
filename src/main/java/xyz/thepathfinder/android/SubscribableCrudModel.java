package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access to CRUD and subscribe operations.
 *
 * @param <T> Model type
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableCrudModel<T extends SubscribableCrudModel<T, E>, E extends ModelListener<T>> extends SubscribableModel<T, E> {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(SubscribableCrudModel.class);

    /**
     * Constructs a subscribable CRUD model.
     *
     * @param path     of the model.
     * @param type     of the model.
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
        this.sendMessage(json);
    }

    /**
     * Creates the model at the path specified on the server.
     */
    public void create() {
        if (this.isConnected()) {
            logger.warn("Cannot create connected model " + this.getPathName() + " the model already exists, ignoring request.");
            return;
        }

        JsonObject value = this.createValueJson();
        JsonObject json = this.getMessageHeader("Create");
        json.addProperty("model", value.get("model").getAsString());

        json.add("value", value);

        if (this.isPathUnknown()) {
            this.getServices().getRegistry().addCreateBacklog(this);
        }

        this.getServices().getConnection().sendMessage(json.toString()); // needs to bypass the unknown path check.
    }

    /**
     * Deletes the model specified by the path on the server.
     */
    public void delete() {
        JsonObject json = this.getMessageHeader("Delete");
        this.sendMessage(json);
    }

    /**
     * Updates the model specified by the path on the server.
     *
     * @param value of the update request.
     */
    protected void update(JsonObject value) {

        JsonObject json = this.getMessageHeader("Update");
        json.add("value", value);

        this.sendMessage(json);
    }

    /**
     * Returns the JSON needed to create the object on the Pathfinder server.
     *
     * @return JSON needed to create the object on the Pathfinder server.
     */
    protected abstract JsonObject createValueJson();
}
