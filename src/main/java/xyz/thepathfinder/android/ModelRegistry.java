package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <tt>ModelRegistry</tt> keeps track of all {@link Model}s created by
 * the Pathfinder SDK.
 *
 * @author David Robinson
 */
class ModelRegistry {

    private static final Logger logger = Logger.getLogger(ModelRegistry.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Map to all the {@link Model}s created by the SDK. The keys are the string
     * version of paths.
     */
    private final Map<Path, Model> models;

    /**
     * Constructs a ModelRegistry object with an empty registry of {@link Model}s.
     */
    protected ModelRegistry() {
        this.models = new HashMap<Path, Model>();
    }

    /**
     * Adds a {@link Model} to the registry.
     *
     * @param model the model to be added to the registry.
     * @throws IllegalStateException the path has already been used by another
     *                               model.
     */
    protected void registerModel(Model model) {
        if (this.models.containsKey(model.getPath())) {
            logger.warning("Illegal State Exception: path already exists" + model.getPathName());
            //throw new IllegalStateException("Path already exists: " + model.getPathName());
        }

        this.models.put(model.getPath(), model);
    }

    /**
     * Removes a {@link Model} from the registry.
     *
     * @param path to the model.
     * @return the model removed.
     */
    protected Model unregisterModel(Path path) {
        return this.models.remove(path);
    }

    /**
     * Returns if a {@link Model} has been registered with the specified path.
     *
     * @param path of the model to check.
     * @return <tt>true</tt> if a model has been registered with that path,
     * <tt>false</tt> otherwise.
     */
    protected boolean isModelRegistered(Path path) {
        return this.models.containsKey(path);
    }

    /**
     * Returns the {@link Model} associated with the specified path in the registry.
     *
     * @param path to the model.
     * @return the model associate with the path specified. If no model is associated
     * with a path it returns <tt>null</tt>.
     */
    protected Model getModel(Path path) {
        logger.info("Model requested: " + path.getPathName() + " Type: " + path.getModelType());
        return this.models.get(path);
    }
}
