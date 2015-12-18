package xyz.thepathfinder.android;

/**
 * Class is used to ease the manipulation of paths to models on the Pathfinder server.
 */
public class Path {

    /**
     * Path to the default cluster
     */
    public static String DEFAULT_PATH = "/default";

    /**
     * A string representing the path
     */
    private String path;

    /**
     * Constructs a path to a model. The path may not an empty string.
     * Other requirements are subject to change. If the path is null it
     * is set to the default path.
     * @param path a string representing the path
     * @throws IllegalArgumentException when the path is invalid.
     */
    public Path(String path) {
        if(!Path.isValidPath(path)) {
            throw new IllegalArgumentException("Path cannot be an empty string");
        }

        if(path == null) {
            this.path = Path.DEFAULT_PATH;
        } else {
            this.path = path;
        }
    }

    public static boolean isValidPath(String path) {
        return !(path != null && path.equals(""));
    }

    public static boolean isValidName(String name) {
        return !name.contains("/");
    }

    public String getChildPath(String name) {
        if(Path.isValidName(name)) {
            return this.path + "/" + name;
        } else {
            throw new IllegalArgumentException("Invalid path name: " + name);
        }
    }

    public String getName() {
        int lastSlashIndex = this.path.lastIndexOf('/') + 1;
        return this.path.substring(lastSlashIndex);
    }

    public String getPath() {
        return path;
    }

    public String getParentPath() {
        int lastSlashIndex = this.path.lastIndexOf('/');
        return this.path.substring(0, lastSlashIndex);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Path) {
            Path otherPath = (Path) o;
            return this.path.equals(otherPath.path);
        }
        return false;
    }
}
