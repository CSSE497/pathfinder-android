package xyz.thepathfinder.android;

public class PathfinderServices {

    private PathfinderModelRegistry registry;
    private PathfinderConnection connection;

    protected PathfinderServices(PathfinderModelRegistry registry, PathfinderConnection connection) {
        this.registry = registry;
        this.connection = connection;
    }

    public PathfinderModelRegistry getRegistry() {
        return this.registry;
    }

    public PathfinderConnection getConnection() {
        return this.connection;
    }
}
