package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;

public class ModelRegistry {
    private final Map<String, Model> models;

    protected ModelRegistry() {
        this.models = new HashMap<String, Model>();
    }

    protected void registerModel(Model model) {
        if(this.models.containsKey(model.getPath())) {
            throw new IllegalArgumentException("Path already exists: " + model.getPath());
        }

        this.models.put(model.getPath(), model);
    }

    protected Model unregisterModel(String path) {
        return this.models.remove(path);
    }

    protected boolean isModelRegistered(String path) {
        return this.models.containsKey(path);
    }

    protected Model getModel(String path, String modelType) {
        Model model = this.models.get(path);
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
