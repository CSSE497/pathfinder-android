package xyz.thepathfinder.android;

/**
 * Gives access to the model repository and the connection to the pathfinder server.
 *
 * @author David Robinson
 */
class PathfinderServices {

    /**
     * Holds access to all the models
     */
    private ModelRegistry registry;

    /**
     * Connection to the pathfinder server
     */
    private Connection connection;

    /**
     * Constructs a pathfinder services object.
     *
     * @param registry   model registry use to store access to all the models.
     * @param connection to the pathfinder server
     */
    protected PathfinderServices(ModelRegistry registry, Connection connection) {
        this.registry = registry;
        this.connection = connection;
    }

    /**
     * Returns the model registry.
     *
     * @return the model registry.
     */
    protected ModelRegistry getRegistry() {
        return this.registry;
    }

    /**
     * Returns the connection to the pathfinder server.
     *
     * @return the connection to the pathfinder server.
     */
    protected Connection getConnection() {
        return this.connection;
    }
}
