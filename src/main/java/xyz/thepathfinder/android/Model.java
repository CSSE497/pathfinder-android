package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic class for dealing with models from the Pathfinder server.
 *
 * @param <E> a type of {@link Listener}
 * @author David Robinson
 */
public abstract class Model<E extends Listener<? extends Model>> extends Listenable<E> {

    private Logger logger = Logger.getLogger(Model.class.getName());

    /**
     * The path of the model.
     */

    private Path path;
    //TODO revert after path update
    //private final Path path;

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
     * Creates a basic object that all pathfinder models should use.
     *
     * @param path     to the model on the Pathfinder server.
     * @param services a pathfinder services object.
     */
    public Model(String path, PathfinderServices services) {
        this.path = new Path(path);
        this.services = services;
        this.isConnected = false;
    }

    /**
     * Returns the path of the model.
     *
     * @return the path.
     */
    public String getPath() {
        return this.path.getPath();
    }

    /**
     * Returns the path of a child of this model.
     *
     * @param name the name to added on to the path.
     * @return the path of the child.
     */
    protected String getChildPath(String name) {
        return this.path.getChildPath(name);
    }

    /**
     * Returns the path of the parent of this model.
     *
     * @return the path to the parent cluster.
     */
    public String getParentPath() {
        return this.path.getParentPath();
    }

    /**
     * Returns the parent cluster of this model.
     *
     * @return the parent cluster of this model. If the default cluster it returns <tt>null</tt>.
     */
    public Cluster getParentCluster() {
        String parentPath = this.getParentPath();
        return Cluster.getInstance(parentPath, services);
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

        if (json.has("value")) {
            value = json.getAsJsonObject("value");
            updated = this.updateFields(value);
        }

        List<E> listeners = this.getListeners();

        if (updated && !reason.equals("Updated")) {
            for (Listener listener : listeners) {
                listener.updated(this);
            }
        }

        if (reason.equals("Updated")) {
            for (Listener listener : listeners) {
                listener.updated(this);
            }
            return updated;
        }

        if (reason.equals("Routed")) {
            this.route(json, this.getServices());
            return true;
        }

        if (reason.equals("Model")) {
            for (Listener listener : listeners) {
                listener.connected(this);
            }
            return updated;
        }

        if (reason.equals("Subscribed")) {
            for (Listener listener : listeners) {
                listener.subscribed(this);
            }
            return updated;
        }

        if (reason.equals("RouteSubscribed")) {
            for (Listener listener : listeners) {
                listener.routeSubscribed(this);
            }
            return updated;
        }

        if (reason.equals("Unsubscribed")) {
            for (Listener listener : listeners) {
                listener.unsubscribed(this);
            }
            return updated;
        }

        if (reason.equals("RouteUnsubscribed")) {
            for (Listener listener : listeners) {
                listener.routeUnsubscribed(this);
            }
            return updated;
        }

        if (reason.equals("Created")) {
            for (Listener listener : listeners) {
                listener.created(this);
            }
            return updated;
        }

        if (reason.equals("Deleted")) {
            for (Listener listener : listeners) {
                this.setConnected(false);
                listener.deleted(this);
            }
            return updated;
        }

        if (reason.equals("Error") && value != null) {
            logger.log(Level.WARNING, "Error received: " + json);
            for (Listener listener : listeners) {
                listener.error(value.get("reason").getAsString());
            }
            return updated;
        }

        logger.log(Level.WARNING, "Invalid message type: " + reason + "\nJson: " + json);
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean notifyUpdate(String reason, JsonObject json) {
        if (!json.has("model") || !json.get("model").getAsString().equals(this.getModel())) {
            logger.log(Level.WARNING, "Invalid model type: " + json);
            return false;
        }

        this.setConnected(true);
        if (reason != null) {
            return this.updateType(reason, json);
        } else {
            if (this.updateFields(json)) {
                List<E> listeners = this.getListeners();
                for (Listener listener : listeners) {
                    listener.updated(this);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the name of the model
     *
     * @return name of model
     */
    protected abstract String getModel();

    /**
     * Returns the value used in create request to the Pathfinder server
     *
     * @return the value JSON
     */
    protected abstract JsonObject createValueJson();

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

    //TODO remove after path update
    protected void setPath(String path) {
        this.getServices().getRegistry().unregisterModel(this.path.getPath());
        this.path = new Path(path);
        this.getServices().getRegistry().registerModel(this);
    }
}
