package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

/**
 * Basic class for dealing with models from the Pathfinder server.
 * @param <E> a type of {@link Listener}
 */
public abstract class Model<E extends Listener> extends Listenable<E> {

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
     * Creates a basic object that all pathfinder models should use.
     * @param path to the model on the Pathfinder server.
     * @param services a pathfinder services object.
     */
    public Model(String path, PathfinderServices services) {
        this.path = new Path(path);
        this.services = services;
        this.isConnected = false;
    }

    /**
     * Returns the path of the model.
     * @return the path.
     */
    public String getPath() {
        return this.path.getPath();
    }

    /**
     * Returns the path of a child of this model.
     * @param name the name to added on to the path.
     * @return the path of the child.
     */
    protected String getChildPath(String name) {
        return this.path.getChildPath(name);
    }

    /**
     * Returns the path of the parent of this model.
     * @return the path to the parent cluster.
     */
    public String getParentPath() {
        return this.path.getParentPath();
    }

    /**
     * Returns the parent cluster of this model.
     * @return the parent cluster of this model. If the default cluster it returns <tt>null</tt>.
     */
    public Cluster getParentCluster() {
        String parentPath = this.getParentPath();
        return Cluster.getInstance(parentPath, services);
    }

    /**
     * Returns the pathfinder services object.
     * @return a pathfinder services object.
     */
    protected PathfinderServices getServices() {
        return this.services;
    }

    /**
     * Sets if the model has connected with the Pathfinder server.
     * @param connected has connected with the Pathfinder server.
     */
    protected void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    /**
     * Returns if the model has connected to the Pathfinder server.
     * @return <tt>true</tt> if the model has connected to the Pathfinder server, <tt>false</tt> otherwise.
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Returns the name of the model
     * @return name of model
     */
    protected abstract String getModel();

    /**
     * Returns the value used in create request to the Pathfinder server
     * @return the value JSON
     */
    protected abstract JsonObject createValueJson();

}
