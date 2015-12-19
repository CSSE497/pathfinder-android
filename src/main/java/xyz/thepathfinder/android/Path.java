package xyz.thepathfinder.android;

/**
 * Class is used to ease the manipulation of paths to models on the Pathfinder server.
 */
public class Path {

    /**
     * Separator for path names.
     */
    private static final String PATH_SEPARATOR = "/";

    /**
     * Path to the default cluster.
     */
    public static final String DEFAULT_PATH = "/default";

    /**
     * A string representing the path.
     */
    private String path;

    /**
     * Constructs a path to a model. The path may not an empty string.
     * Other requirements are subject to change. If the path is null it
     * is set to the default pa
     * @param path a string representing the path
     * @throws IllegalArgumentException when the path is invalid.
     */
    protected Path(String path) {
        if(!Path.isValidPath(path)) {
            throw new IllegalArgumentException("Path cannot be an empty string");
        }

        if(path == null) {
            this.path = Path.DEFAULT_PATH;
        } else {
            this.path = path;
        }
    }

    /**
     * Returns if the provided path's characters are valid on the Pathfinder server.
     * @param path to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidPath(String path) {
        return !(path != null && path.equals(""));
    }

    /**
     * Returns if the provided name is a valid name.
     * @param name to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidName(String name) {
        return !name.contains(Path.PATH_SEPARATOR);
    }

    /**
     * Returns the child path of this path plus the name provided.
     * @param name to add.
     * @return the child's path.
     * @throws IllegalArgumentException if the name is invalid, see {@link Path#isValidName(String)}.
     */
    public String getChildPath(String name) {
        if(Path.isValidName(name)) {
            return this.path + Path.PATH_SEPARATOR + name;
        } else {
            throw new IllegalArgumentException("Invalid path name: " + name);
        }
    }

    public String getName() {
        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR) + 1;
        return this.path.substring(lastSlashIndex);
    }

    public String getPath() {
        return path;
    }

    public String getParentPath() {
        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR);
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
