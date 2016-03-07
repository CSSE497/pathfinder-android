package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Basic class for dealing with models from the Pathfinder server.
 *
 * @param <E> a type of {@link Listener}
 * @author David Robinson
 */
public abstract class Model<E extends Listener<? extends Model>> extends Listenable<E> {

    private static final Logger logger = LoggerFactory.getLogger(Action.class);

    /**
     * The path of the model.
     */
    private final Path path;

    /**
     * A pathfinder services object to have access to the model registry
     * and the web socket.
     */
    private final PathfinderServices services;

    /**
     * Whether or not the model has connected with the Pathfinder server.
     */
    private boolean isConnected;

    /**
     * Messages saved to be sent later, after the model's path is fully defined.
     */
    private Queue<JsonObject> messageBacklog;

    /**
     * Creates a basic object that all pathfinder models should use.
     *
     * @param path     to the model on the Pathfinder server.
     * @param type     of the model.
     * @param services a pathfinder services object.
     */
    public Model(String path, ModelType type, PathfinderServices services) {
        this.path = new Path(path, type);
        this.services = services;
        this.isConnected = false;

        if(path == null) {
            this.messageBacklog = new LinkedList<JsonObject>();
        }
    }

    /**
     * Returns the path of the model.
     *
     * @return path of the model.
     */
    protected Path getPath() {
        return this.path;
    }

    /**
     * Returns true if the model's path is unknown. This occurs if the model has
     * not been created on the pathfinder server.
     *
     * @return true if the model's path is unknown and false if it is known.
     */
    public boolean isPathUnknown() {
        return this.path.getPathName() == null;
    }

    /**
     * Returns the string of the path of the model.
     *
     * @return the path.
     */
    public String getPathName() {
        return this.path.getPathName();
    }

    /**
     * Set the path of the model. This method may not be called after the path becomes known.
     *
     * @param path of the model.
     *
     * @throws IllegalStateException if the path is already known.
     */
    protected void setPathName(String path) {
        if(this.isPathUnknown()) {
            this.path.setPathName(path);

            for(JsonObject json : this.messageBacklog) {
                json.addProperty("id", this.getPathName());
                this.getServices().getConnection().sendMessage(json.toString());
            }

            this.messageBacklog = null;
        } else {
            logger.error("Illegal State Exception: The path of a model may not be set after becoming known");
            throw new IllegalStateException("The path of a model may not be set after becoming known");
        }
    }

    /**
     * Returns the path of a child of this model.
     *
     * @param name the name to added on to the path.
     * @param type of the model.
     * @return the path of the child.
     */
    protected Path getChildPath(String name, ModelType type) {
        return this.path.getChildPath(name, type);
    }

    /**
     * Returns the path of the parent of this model.
     *
     * @return the path to the parent cluster.
     */
    public Path getParentPath() {
        return this.path.getParentPath();
    }

    /**
     * Returns the name of the model. If the path of this model is
     * <tt>"/default/cluster1/subcluster1/transport3"</tt> the name is
     * <tt>"transport3"</tt>.
     *
     * @return the name of the model.
     */
    public String getName() {
        return this.path.getName();
    }

    /**
     * Returns the parent cluster of this model.
     *
     * @return the parent cluster of this model. If the default cluster it returns <tt>null</tt>.
     */
    public Cluster getParentCluster() {
        Path parentPath = this.getParentPath();
        return Cluster.getInstance(parentPath.getPathName(), services);
    }

    /**
     * Returns the type of the model
     *
     * @return type of model
     */
    public ModelType getModelType() {
        return this.path.getModelType();
    }

    /**
     * Returns the pathfinder services object.
     *
     * @return a pathfinder services object.
     */
    protected PathfinderServices getServices() {
        return this.services;
    }

    /**
     * Sets if the model has connected with the Pathfinder server.
     *
     * @param connected has connected with the Pathfinder server.
     */
    protected void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    /**
     * Returns if the model has connected to the Pathfinder server.
     *
     * @return <tt>true</tt> if the model has connected to the Pathfinder server, <tt>false</tt> otherwise.
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Invokes the model unspecific notifications.
     *
     * @param reason message type
     * @param json   the message
     * @return <tt>true</tt> if model changed in any way, <tt>false</tt> otherwise.
     */
    @SuppressWarnings("unchecked")
    private boolean updateType(String reason, JsonObject json) {
        boolean updated = false;
        JsonObject value = null;

        logger.info("Reason for update is: " + reason + ", " + json);

        if (json.has("value") && !json.has("route")) {
            value = json.getAsJsonObject("value");
        } else if(reason == null){
            value = json;
        }

        logger.info("Value is: " + value);

        if(value != null) {
            updated = this.updateFields(value);
        }

        logger.info("Finished updating " + this.getPathName() + "'s fields.");
        List<E> listeners = this.getListeners();

        if (updated && !"Updated".equals(reason)) {
            logger.info("Model " + this.getPathName() + " updated");
            for (Listener listener : listeners) {
                logger.info("Calling updated");
                listener.updated(this);
                logger.info("Finished Calling updated");
            }
        }

        logger.info("Reason: " + reason);
        if(reason == null) {
            return updated;
        }

        if (reason.equals("Updated")) {
            logger.info("Model " + this.getPathName() + " updated");
            for (Listener listener : listeners) {
                listener.updated(this);
            }
            return updated;
        }

        if (reason.equals("Routed")) {
            logger.info("Model " + this.getPathName() + " routed");
            this.route(json, this.getServices());
            return true;
        }

        if (reason.equals("Model")) {
            logger.info("Model " + this.getPathName() + " connected");
            for (Listener listener : listeners) {
                listener.connected(this);
            }
            return updated;
        }

        if (reason.equals("Subscribed")) {
            logger.info("Model " + this.getPathName() + " subscribed");
            for (Listener listener : listeners) {
                listener.subscribed(this);
            }
            return updated;
        }

        if (reason.equals("RouteSubscribed")) {
            logger.info("Model " + this.getPathName() + " route subscribed");
            for (Listener listener : listeners) {
                listener.routeSubscribed(this);
            }
            return updated;
        }

        if (reason.equals("Unsubscribed")) {
            logger.info("Model " + this.getPathName() + " unsubscribed");
            for (Listener listener : listeners) {
                listener.unsubscribed(this);
            }
            return updated;
        }

        if (reason.equals("RouteUnsubscribed")) {
            logger.info("Model " + this.getPathName() + " route unsubscribed");
            for (Listener listener : listeners) {
                listener.routeUnsubscribed(this);
            }
            return updated;
        }

        if (reason.equals("Created")) {
            logger.info("Model " + this.getPathName() + " created");
            for (Listener listener : listeners) {
                listener.created(this);
            }
            return updated;
        }

        if (reason.equals("Deleted")) {
            logger.info("Model " + this.getPathName() + " deleted");
            for (Listener listener : listeners) {
                this.setConnected(false);
                listener.deleted(this);
            }
            return updated;
        }

        if (reason.equals("Error") && value != null) {
            logger.warn("Model " + this.getPathName() + " received error: " + value.get("reason").getAsString());
            for (Listener listener : listeners) {
                listener.error(value.get("reason").getAsString());
            }
            return updated;
        }

        logger.warn("Invalid message sent to " + this.getPathName() + " with type: " + reason + "\nJson: " + json);
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean notifyUpdate(String reason, JsonObject json) {
/*        if (!json.has("model") || !json.get("model").getAsString().equals(this.getModel())) {
            logger.warn("Invalid model type: " + json + " given to a " + this.getModel());
            return false;
        }*/

        this.setConnected(true);
        return this.updateType(reason, json);
    }

    protected void sendMessage(JsonObject json) {
        if(!this.isPathUnknown()) {
            this.getServices().getConnection().sendMessage(json.toString());
        } else {
            this.messageBacklog.offer(json);
        }
    }

    /**
     * Updates the fields of the model.
     *
     * @param json of the model.
     * @return <tt>true</tt> if model changed in any way, <tt>false</tt> otherwise.
     */
    protected abstract boolean updateFields(JsonObject json);

    /**
     * Updates the models routes.
     *
     * @param json     of the model.
     * @param services pathfinder services object.
     */
    protected abstract void route(JsonObject json, PathfinderServices services);
}
