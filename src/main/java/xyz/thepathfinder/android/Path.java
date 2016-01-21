package xyz.thepathfinder.android;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class is used to ease the manipulation of paths to models on the Pathfinder server.
 *
 * @author David Robinson
 */
class Path {

    private static final Logger logger = Logger.getLogger(Path.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Separator for path names.
     */
    private static final String PATH_SEPARATOR = "/";

    /**
     * A string representing the path.
     */
    private String path;

    /**
     * Constructs a path to a model. The path may not an empty string.
     * Other requirements are subject to change.
     *
     * @param path a string representing the path
     * @throws IllegalArgumentException when the path is invalid.
     */
    protected Path(String path) {
        if (!Path.isValidPath(path)) {
            logger.warning("Illegal Argument Exception: Illegal path name " + path);
            throw new IllegalArgumentException("Illegal path name " + path);
        }

        this.path = path;
    }

    /**
     * Returns if the provided path's characters are valid on the Pathfinder server.
     *
     * @param path to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidPath(String path) {
        return !(path == null || path.equals(""));
    }

    /**
     * Returns if the provided name is a valid name.
     *
     * @param name to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidName(String name) {
        return !name.contains(Path.PATH_SEPARATOR);
    }

    /**
     * Returns the child path of this path plus the name provided.
     *
     * @param name to add.
     * @return the child's path.
     * @throws IllegalArgumentException if the name is invalid, see {@link Path#isValidName(String)}.
     */
    public String getChildPath(String name) {
        if (Path.isValidName(name)) {
            return this.path + Path.PATH_SEPARATOR + name;
        } else {
            logger.warning("Illegal Argument Exception: Illegal path name " + name);
            throw new IllegalArgumentException("Illegal path name: " + name);
        }
    }

    /**
     * Returns the name of the model. If the path of this model is
     * <tt>"/default/cluster1/subcluster1/transport3"</tt> the name is
     * <tt>"transport3"</tt>.
     *
     * @return the name of the model.
     */
    public String getName() {
        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR) + 1;
        return this.path.substring(lastSlashIndex);
    }

    /**
     * Returns the path of the model.
     *
     * @return the path of the model.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the parent's path of this path. If the path of this model
     * is <tt>"/default/cluster1/subcluster1/transport3"</tt> the name is
     * <tt>"/default/cluster1/subcluster1"</tt>.
     *
     * @return the path of the parent of this path.
     */
    public String getParentPath() {
        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR);

        if(lastSlashIndex == -1) {
            return "";
        }

        return this.path.substring(0, lastSlashIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Path) {
            Path otherPath = (Path) o;
            return this.path.equals(otherPath.path);
        }
        return false;
    }
}
