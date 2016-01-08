package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Robinson
 */
class Route {

    private final Transport transport;
    private final List<Action> actions;

    protected Route(JsonObject routeJson, PathfinderServices services) {
        this.transport = Route.getTransport(routeJson, services);
        this.actions = Route.getActions(routeJson, services);
    }

    public Transport getTransport() {
        return this.transport;
    }

    public List<Action> getActions() {
        return this.actions;
    }

    private static Transport getTransport(JsonObject json, PathfinderServices services) {
        return Transport.getInstance(json.getAsJsonObject("model"), services);
    }

    private static List<Action> getActions(JsonObject json, PathfinderServices services) {
        JsonArray actions = json.getAsJsonArray("actions");
        List<Action> list = new ArrayList<Action>();

        for (JsonElement element : actions) {
            list.add(new Action(element.getAsJsonObject(), services));
        }

        return list;
    }
}
