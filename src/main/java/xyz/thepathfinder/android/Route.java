package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Route of the model.
 *
 * @author David Robinson
 */
public class Route {

    /**
     * Transport used for the route.
     */
    private final Transport transport;

    /**
     * Actions for the transport to perform
     */
    private final List<Action> actions;

    /**
     * Creates a route for a transport to perform.
     *
     * @param routeJson JSON of the route.
     * @param services a pathfinder services object.
     */
    protected Route(JsonObject routeJson, PathfinderServices services) {
        this.transport = Route.getTransport(routeJson, services);
        this.actions = Route.getActions(routeJson, services);
    }

    /**
     * Returns the transport to perform the route.
     *
     * @return a transport
     */
    public Transport getTransport() {
        return this.transport;
    }

    /**
     * Returns a list of actions for the transport to perform.
     *
     * @return a list of actions.
     */
    public List<Action> getActions() {
        return this.actions;
    }

    /**
     * Gets the transport from the route JSON.
     *
     * @param json the route JSON.
     * @param services the pathfinder services object.
     * @return a transport
     */
    private static Transport getTransport(JsonObject json, PathfinderServices services) {
        return Transport.getInstance(json.getAsJsonObject("model"), services);
    }

    /**
     * Gets the actions for the transport to perform from the route JSON.
     *
     * @param json the route JSON.
     * @param services the pathfinder services object.
     * @return a list actions for the transport to perform.
     */
    private static List<Action> getActions(JsonObject json, PathfinderServices services) {
        JsonArray actions = json.getAsJsonArray("actions");
        List<Action> list = new ArrayList<Action>();

        for (JsonElement element : actions) {
            list.add(new Action(element.getAsJsonObject(), services));
        }

        return list;
    }
}
