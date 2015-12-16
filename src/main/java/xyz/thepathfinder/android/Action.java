package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public class Action {

    private final ActionStatus status;
    private final double latitude;
    private final double longitude;
    private final SubscribableCrudModel model;

    protected Action(JsonObject actionJson, PathfinderServices services) {
        this.status = Action.getStatus(actionJson);
        this.latitude = Action.getLatitude(actionJson);
        this.longitude = Action.getLongitude(actionJson);
        this.model = Action.getModel(actionJson, services);
    }

    public ActionStatus getStatus() {
        return this.status;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public SubscribableCrudModel getModel() {
        return this.model;
    }

    private static ActionStatus getStatus(JsonObject json) {
        return Action.getStatus(json.get("action").getAsString());
    }

    private static ActionStatus getStatus(String status) {
        ActionStatus[] values = ActionStatus.values();
        for (int k = 0; k < values.length; k++) {
            if (values[k].equals(status)) {
                return values[k];
            }
        }

        return null;
    }

    private static double getLatitude(JsonObject json) {
        return json.get("latitude").getAsDouble();
    }

    private static double getLongitude(JsonObject json) {
        return json.get("longitude").getAsDouble();
    }

    private static SubscribableCrudModel getModel(JsonObject json, PathfinderServices services) {
        JsonObject model = json.getAsJsonObject("model");
        String type = model.get("model").getAsString();

        if(type.equals(Pathfinder.TRANSPORT)) {
           return Transport.getInstance(model, services);
        } else if(type.equals(Pathfinder.COMMODITY)) {
            return Commodity.getInstance(json, services);
        }

        throw new IllegalArgumentException("Illegal model type in action creation: " + type);
    }
}
