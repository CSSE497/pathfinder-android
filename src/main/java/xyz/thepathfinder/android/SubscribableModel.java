package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.logging.Logger;

/**
 * Access to subscribe operations on models.
 *
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableModel<E extends Listener<? extends Model>> extends Model<E> {

    private static final Logger logger = Logger.getLogger(SubscribableModel.class.getName());

    /**
     * Constructs a subcribable model.
     *
     * @param path of the model to subscribe to.
     * @param services a pathfinder services object.
     */
    public SubscribableModel(String path, PathfinderServices services) {
        super(path, services);
    }

    /**
     * Returns the header of the message to send to the pathfinder server.
     *
     * @param type of the message
     * @return the message header.
     */
    public JsonObject getMessageHeader(String type) {
        JsonObject json = new JsonObject();

        json.addProperty("message", type);

        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());

        return json;
    }

    /**
     * Subscribes to the models updates from the server.
     */
    public void subscribe() {
        if (!this.isConnected()) {
            logger.warning("Cannot subscribe to a model you are not connected to");
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("Subscribe");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Unsubcribes from updates from the server. Not currently supported.
     */
    public void unsubscribe() {
        //TODO implement
        //Not implemented on server
    }

    /**
     * Subscribes to route updates from the server.
     */
    public void routeSubscribe() {
        if (!this.isConnected()) {
            logger.warning("Cannot route subscribe to a model you are not connected to");
            throw new IllegalStateException("Not connected to object on Pathfinder server");
        }

        JsonObject json = this.getMessageHeader("RouteSubscribe");
        this.getServices().getConnection().sendMessage(json.toString());
    }

    /**
     * Unsubcribes from route updates from the server. Not currently supported.
     */
    public void routeUnsubscribe() {
        //TODO implement
        //Not implemented on server
    }
}
