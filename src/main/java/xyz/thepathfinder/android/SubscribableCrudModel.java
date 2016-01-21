package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Access to CRUD and subscribe operations.
 *
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableCrudModel<E extends Listener<? extends Model>> extends SubscribableModel<E> {

    private static final Logger logger = Logger.getLogger(SubscribableCrudModel.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Constructs a subscribable CRUD model.
     *
     * @param path of the model
     * @param services a pathfinder services object.
     */
    public SubscribableCrudModel(String path, PathfinderServices services) {
        super(path, services);
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
     */
    public void create() {
        if (this.isConnected()) {
            logger.warning("Cannot create a connected model: " + this.getPath());
            throw new IllegalStateException("Already created");
        }

        JsonObject json = this.getMessageHeader("Create");

        json.add("value", this.createValueJson());

        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Deletes the model specified by the path on the server.
     */
    public void delete() {
        if (!this.isConnected()) {
            logger.warning("Cannot delete a model not connected: " + this.getPath());
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("Delete");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Updates the model specified by the path on the server.
     *
     * @param value of the update request.
     */
    protected void update(JsonObject value) {
        if (!this.isConnected()) {
            logger.warning("Cannot update a model not connected: " + this.getPath());
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("Update");
        json.add("value", value);

        this.getServices().getConnection().sendMessage(json.toString());
    }
}
