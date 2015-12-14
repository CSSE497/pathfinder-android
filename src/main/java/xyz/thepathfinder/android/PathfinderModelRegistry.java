package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;

public class PathfinderModelRegistry {
    private final Map<String, PathfinderModel> models;

    protected PathfinderModelRegistry() {
        this.models = new HashMap<String, PathfinderModel>();
    }

    protected void registerModel(PathfinderModel model) {
        if(this.models.containsKey(model.getPath())) {
            throw new IllegalArgumentException("Path already exists: " + model.getPath());
        }

        this.models.put(model.getPath(), model);
    }

    protected PathfinderModel unregisterModel(String path) {
        return this.models.remove(path);
    }

    protected boolean isModelRegistered(String path) {
        return this.models.containsKey(path);
    }

    protected PathfinderModel getModel(String path, String modelType) {
        PathfinderModel model = this.models.get(path);
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
