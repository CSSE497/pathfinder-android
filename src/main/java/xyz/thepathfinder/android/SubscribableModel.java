package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access to subscribe operations on models.
 *
 * @param <E> Listener type
 * @author David Robinson
 */
public abstract class SubscribableModel<E extends Listener<? extends Model>> extends Model<E> {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(SubscribableModel.class);

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
        } else if(!this.isPathUnknown()) {
            json.addProperty("id", Integer.parseInt(this.getName()));
        }

        json.addProperty("model", this.getModelType().toString());

        return json;
    }

    private void sendSubscribeMessage(String type) {
        JsonObject json = this.getMessageHeader(type);

        if(this.getModelType().equals(ModelType.CLUSTER)) {
            json.remove("id");
            json.addProperty("model", "Vehicle");
            json.addProperty("clusterId", this.getPathName());
            this.sendMessage(json);
            json.addProperty("model", "Commodity");
            this.sendMessage(json);
        } else {
            this.sendMessage(json);
        }
    }

    /**
     * Subscribes to the models updates from the server.
     */
    public void subscribe() {
        this.sendSubscribeMessage("Subscribe");
    }

    /**
     * Unsubcribes from updates from the server.
     */
    public void unsubscribe() {
        this.sendSubscribeMessage("Unsubscribe");
    }

    /**
     * Subscribes to route updates from the server.
     */
    public void routeSubscribe() {
        JsonObject json = this.getMessageHeader("RouteSubscribe");
        this.sendMessage(json);
    }

    /**
     * Unsubcribes from route updates from the server. Not currently supported.
     */
    public void routeUnsubscribe() {
        JsonObject json = this.getMessageHeader("RouteUnsubscribe");
        this.sendMessage(json);
    }
}
