package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;

public class PathfinderModelRegistry {
    private static final Map<String, PathfinderModel> models = new HashMap<String, PathfinderModel>();

    protected static void registerModel(PathfinderModel model) {
        if(models.containsKey(model.getPath())) {
            throw new IllegalArgumentException("Path already exists: " + model.getPath());
        }

        models.put(model.getPath(), model);
    }

    protected static PathfinderModel unregisterModel(String path) {
        return models.remove(path);
    }

    protected static boolean isModelRegistered(String path) {
        return models.containsKey(path);
    }

    protected static PathfinderModel getModel(String path, String modelType) {
        PathfinderModel model = models.get(path);
        if(model == null) {
            return model;
        }

        if(model.getModel() != modelType) {
            throw new IllegalArgumentException("Model requested is a different model type, requested type: " +
                    modelType + " registered type: " + model.getModel() + " path: " + path);
        }

        return model;
    }
}
