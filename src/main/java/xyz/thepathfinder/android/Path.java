package xyz.thepathfinder.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is used to ease the manipulation of paths to models on the Pathfinder server.
 *
 * @author David Robinson
 */
class Path {

    /**
     * Default cluster path.
     */
    protected static final String DEFAULT_PATH = "/root";

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Path.class);

    /**
     * Separator for path names.
     */
    private static final String PATH_SEPARATOR = "/";

    /**
     * Type of the model.
     */
    private final ModelType modelType;

    /**
     * A string representing the path.
     */
    private String path;

    /**
     * Constructs a path to a model. The path may not an empty string.
     * Other requirements are subject to change.
     *
     * @param path      a string representing the path.
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
        return path == null || !(path.equals("") || path.contains(" "));
    }

    /**
     * Returns if the provided name is a valid name.
     *
     * @param name to check.
     * @return <tt>true</tt> if allowed, <tt>false</tt> otherwise.
     */
    public static boolean isValidName(String name) {
        return name != null && !(name.contains(Path.PATH_SEPARATOR) || name.equals("") || name.contains(" "));
    }

    /**
     * Returns the child path of this path plus the name provided.
     *
     * @param name to add.
     * @param type of the model.
     * @return the child's path.
     * @throws IllegalArgumentException if the name is invalid, see {@link Path#isValidName(String)}.
     * @throws IllegalStateException    if the model type isn't a cluster.
     */
    protected Path getChildPath(String name, ModelType type) {
        if (!this.getModelType().equals(ModelType.CLUSTER) || !type.equals(ModelType.CLUSTER)) {
            logger.error("Illegal State Exception: Cannot get a child path name on a non-cluster type");
            throw new IllegalStateException("Cannot get a child path name on a non-cluster type");
        } else if (this.path == null) {
            logger.error("Illegal State Exception: Cannot get a child path with an unknown path, make sure the model has been created.");
            throw new IllegalStateException("Cannot get a child path with an unknown path, make sure the model has been created.");
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
        if (this.path == null) {
            return null;
        }
        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR) + 1;
        return this.path.substring(lastSlashIndex);
    }

    /**
     * Returns the path of the model.
     *
     * @return the path of the model.
     */
    public String getPathName() {
        return this.path;
    }

    /**
     * Set the path of the model. This method may not be called after the path becomes known.
     *
     * @param path of the model.
     * @throws IllegalStateException if the path is already known.
     */
    protected void setPathName(String path) {
        if (this.path == null) {
            this.path = path;
        } else {
            logger.error("Illegal State Exception: The path of a model may not be set after becoming known");
            throw new IllegalStateException("The path of a model may not be set after becoming known");
        }
    }

    /**
     * Returns the parent's path of this path. If the path of this model
     * is <tt>"/default/cluster1/subcluster1/transport3"</tt> the name is
     * <tt>"/default/cluster1/subcluster1"</tt>.
     *
     * @return the path of the parent of this path.
     */
    public Path getParentPath() {
        if (this.path == null) {
            return null;
        }

        int lastSlashIndex = this.path.lastIndexOf(Path.PATH_SEPARATOR);

        if (lastSlashIndex <= 0) {
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
            return this.modelType == otherPath.modelType &&
                    ((this.path == null && otherPath.path == null) || (this.path != null && this.path.equals(otherPath.path)));
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if (this.path == null) {
            return 0;
        }
        return this.path.hashCode();
    }
}
