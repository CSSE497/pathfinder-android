package xyz.thepathfinder.android;

public class PathfinderServices {

    private ModelRegistry registry;
    private Connection connection;

    protected PathfinderServices(ModelRegistry registry, Connection connection) {
        this.registry = registry;
        this.connection = connection;
    }

    public ModelRegistry getRegistry() {
        return this.registry;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
