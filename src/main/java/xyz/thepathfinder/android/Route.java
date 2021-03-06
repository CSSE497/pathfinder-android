package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Route of the model.
 *
 * @author David Robinson
 */
public class Route {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Route.class);

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
     * @param services  a pathfinder services object.
     */
    protected Route(JsonObject routeJson, PathfinderServices services) {
        logger.info("Parsing route: " + routeJson.toString());
        this.transport = Route.getTransport(routeJson, services);
        this.actions = Route.getActions(routeJson, services);
    }

    /**
     * Gets the transport from the route JSON.
     *
     * @param json     the route JSON.
     * @param services the pathfinder services object.
     * @return a transport
     */
    private static Transport getTransport(JsonObject json, PathfinderServices services) {
        logger.info("Route getting transport: " + json.toString());
        return Transport.getInstance(json.getAsJsonObject("transport"), services);
    }

    /**
     * Gets the actions for the transport to perform from the route JSON.
     *
     * @param json     the route JSON.
     * @param services the pathfinder services object.
     * @return a list actions for the transport to perform.
     */
    private static List<Action> getActions(JsonObject json, PathfinderServices services) {
        logger.info("route getting actions: " + json.toString());
        JsonArray actions = json.getAsJsonArray("actions");
        List<Action> list = new ArrayList<Action>();

        for (JsonElement element : actions) {
            logger.info("route adding action: " + element.toString());
            list.add(new Action(element.getAsJsonObject(), services));
        }

        return list;
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        JsonObject json = new JsonObject();

        json.addProperty("transport", this.getTransport().getPathName());
        json.addProperty("actions", this.getActions().toString());

        return json.toString();
    }
}
