package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Access to subscribe operations on models.
 *
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableModel<E extends Listener<? extends Model>> extends Model<E> {

    private static final Logger logger = Logger.getLogger(SubscribableModel.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Constructs a subcribable model.
     *
     * @param path of the model to subscribe to.
     * @param type of the model.
     * @param services a pathfinder services object.
     */
    public SubscribableModel(String path, ModelType type, PathfinderServices services) {
        super(path, type, services);
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

        if(this.getModelType() == ModelType.CLUSTER) {
            json.addProperty("id", this.getPathName());
        } else {
            json.addProperty("id", this.getName());
        }

        json.addProperty("model", this.getModelType().toString());

        return json;
    }

    /**
     * Subscribes to the models updates from the server.
     */
    public void subscribe() {
        JsonObject json = this.getMessageHeader("Subscribe");

        if(this.getModelType().equals(ModelType.CLUSTER)) {
            json.remove("id");
            json.addProperty("model", "Vehicle");
            json.addProperty("clusterId", this.getPathName());
            this.getServices().getConnection().sendMessage(json.toString());
            json.addProperty("model", "Commodity");
        }

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
