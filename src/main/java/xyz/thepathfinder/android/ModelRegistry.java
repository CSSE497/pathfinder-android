package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <tt>ModelRegistry</tt> keeps track of all {@link Model}s created by
 * the Pathfinder SDK.
 *
 * @author David Robinson
 */
class ModelRegistry {

    private static final Logger logger = LoggerFactory.getLogger(Action.class);

    /**
     * Map to all the {@link Model}s created by the SDK. The keys are the string
     * version of paths.
     */
    private final Map<Path, Model> models;

    /**
     * List of models that have unknown paths, occurs on create with commodities and transports.
     */
    private final List<Model> createBacklog;

    /**
     * Constructs a ModelRegistry object with an empty registry of {@link Model}s.
     */
    protected ModelRegistry() {
        this.models = new HashMap<Path, Model>();
        this.createBacklog = new ArrayList<Model>();
    }

    /**
     * Adds a {@link Model} to the registry.
     *
     * @param model the model to be added to the registry.
     * @throws IllegalStateException the path has already been used by another
     *                               model.
     * @throws IllegalArgumentException if the model's path is unknown.
     */
    protected void registerModel(Model model) {
        if (this.models.containsKey(model.getPath())) {
            logger.error("Illegal State Exception: path already exists" + model.getPathName());
            throw new IllegalStateException("Path already exists: " + model.getPathName());
        } else if(model.isPathUnknown()) {
            logger.error("Illegal Argument Exception: Cannot register a model with an unknown path.");
            throw new IllegalArgumentException("Cannot register a model with an unknown path.");
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

    /**
     * Adds a {@link Model} to the create backlog.
     *
     * @param model model to be added.
     */
    protected void addCreateBacklog(Model model) {
        this.createBacklog.add(model);
    }

    protected void removeCreateBacklog(Model model) {
        this.createBacklog.remove(model);
    }

    /**
     * Finds a model in the create backlog. Compares the json provided to the values
     * of the models in the backlog to see if any match and returns a matching model
     * if found.
     *
     * @param json of model to be found.
     * @param type of the model.
     * @return model if found, <tt>null</tt> otherwise.
     */
    protected Model findInCreateBacklog(JsonObject json, ModelType type) {
        long id = json.remove("id").getAsLong();
        String clusterId = json.remove("clusterId").getAsString();
        json.addProperty("path", (String) null);

        for(Model model : this.createBacklog) {
            JsonObject modelJson = model.toJson();
            modelJson.remove("model");
            if(model.getModelType() == type && json.equals(modelJson)) {
                json.remove("path");
                json.addProperty("id", id);
                json.addProperty("clusterId", clusterId);
                return model;
            }
        }

        json.remove("path");
        json.addProperty("id", id);
        json.addProperty("clusterId", clusterId);

        return null;
    }
}
