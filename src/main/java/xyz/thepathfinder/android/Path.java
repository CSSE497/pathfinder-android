package xyz.thepathfinder.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is used to ease the manipulation of paths to models on the Pathfinder server.
 *
 * @author David Robinson
 */
class Path {

    private static final Logger logger = LoggerFactory.getLogger(Action.class);

    /**
     * Separator for path names.
     */
    private static final String PATH_SEPARATOR = "/";

    /**
     * Default cluster path.
     */
    protected static final String DEFAULT_PATH = "/root";

    /**
     * A string representing the path.
     */
    private String path;

    /**
     * Type of the model.
     */
    private ModelType modelType;

    /**
     * Constructs a path to a model. The path may not an empty string.
     * Other requirements are subject to change.
     *
     * @param path a string representing the path.
     * @param modelType type of the model.
     * @throws IllegalArgumentException when the path is invalid.
     */
    protected Path(String path, ModelType modelType) {
        if (!Path.isValidPath(path)) {
            logger.error("Illegal Argument Exception: Illegal path name " + path);
            throw new IllegalArgumentException("Illegal path name " + path);
        }

        this.path = path;
        this.modelType = modelType;
    }

    /**
     * Returns if the provided path's characters are valid on the Pathfinder server.
     *
     * @param path to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidPath(String path) {
        return !(path == null || path.equals("") || path.contains(" "));
    }

    /**
     * Returns if the provided name is a valid name.
     *
     * @param name to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidName(String name) {
        return !(name.contains(Path.PATH_SEPARATOR) || name.equals("") || name.contains(" "));
    }

    /**
     * Returns the child path of this path plus the name provided.
     *
     * @param name to add.
     * @param type of the model.
     * @return the child's path.
     * @throws IllegalArgumentException if the name is invalid, see {@link Path#isValidName(String)}.
     * @throws IllegalStateException if the model type isn't a cluster.
     */
    protected Path getChildPath(String name, ModelType type) {
        if(!this.getModelType().equals(ModelType.CLUSTER) || !type.equals(ModelType.CLUSTER)) {
            logger.error("Illegal State Exception: Cannot get a child path name on a non-cluster type");
            throw new IllegalStateException("Cannot get a child path name on a non-cluster type");
        } else if (Path.isValidName(name)) {
            return new Path(this.path + Path.PATH_SEPARATOR + name, type);
        } else {
            logger.error("Illegal Argument Exception: Illegal path name " + name);
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
    public String getPathName() {
        return path;
    }

    /**
     * Returns the parent's path of this path. If the path of this model
     * is <tt>"/default/cluster1/subcluster1/transport3"</tt> the name is
     * <tt>"/default/cluster1/subcluster1"</tt>.
     *
     * @return the path of the parent of this path.
     */
    public Path getParentPath() {
        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR);

        if(lastSlashIndex <= 0) {
            return null;
        }

        return new Path(this.path.substring(0, lastSlashIndex), ModelType.CLUSTER);
    }

    /**
     * Returns the type of the model.
     *
     * @return the type of the model.
     */
    public ModelType getModelType() {
        return this.modelType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Path) {
            Path otherPath = (Path) o;
            return this.modelType == otherPath.modelType && this.path.equals(otherPath.path);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
}
