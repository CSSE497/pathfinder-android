package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Actions represent transport and commodity actions, such as picking up a commodity,
 * dropping off a commodity, or a transport start location. Actions occur at specific
 * locations and an associated model.
 *
 * @author David Robinson
 */
public class Action {

    private static final Logger logger = Logger.getLogger(Action.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Represents the type of action to occur at that location.
     */
    private final ActionStatus status;

    /**
     * The latitude the action occurs at.
     */
    private final double latitude;

    /**
     * The longitude the action occurs at.
     */
    private final double longitude;

    /**
     * The model associated with the action.
     */
    private final SubscribableCrudModel model;

    /**
     * Constructs an Action with a JSON object that represents an Action.
     *
     * @param actionJson JSON object that represents an Action.
     * @param services   a pathfinder services object.
     */
    protected Action(JsonObject actionJson, PathfinderServices services) {
        logger.info("Constructing action: " + actionJson.toString());

        this.status = Action.getStatus(actionJson);
        this.latitude = Action.getLatitude(actionJson);
        this.longitude = Action.getLongitude(actionJson);

        if(!this.status.equals(ActionStatus.START)) {
            this.model = Action.getModel(actionJson, services);
        } else {
            this.model = null;
        }

        logger.info("Done constructing action: " + this.toString());
    }

    /**
     * Returns the status of the action.
     *
     * @return the status
     */
    public ActionStatus getStatus() {
        return this.status;
    }

    /**
     * The latitude that the action occurs at.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * The longitude that the action occurs at.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Returns the model associated with this action. It can either be a
     * transport or a commodity.
     *
     * @return the model associate with the action.
     */
    public SubscribableCrudModel getModel() {
        return this.model;
    }

    /**
     * Returns the status of an action in the form a JSON object.
     *
     * @param json a JSON object that represents an action.
     * @return the status of the action.
     */
    private static ActionStatus getStatus(JsonObject json) {
        return Action.getStatus(json.get("action").getAsString());
    }

    /**
     * Returns the status of an action in the form of a string.
     *
     * @param status a string with the status of the action
     * @return an ActionStatus that represents the string.
     */
    private static ActionStatus getStatus(String status) {
        ActionStatus[] values = ActionStatus.values();
        for (int k = 0; k < values.length; k++) {
            if (values[k].equals(status)) {
                return values[k];
            }
        }

        return null;
    }

    /**
     * Returns the latitude of an action in the form a JSON object.
     *
     * @param json a JSON object that represents an action.
     * @return the latitude of the action.
     */
    private static double getLatitude(JsonObject json) {
        return json.get("latitude").getAsDouble();
    }

    /**
     * Returns the longitude of an action in the form a JSON object.
     *
     * @param json a JSON object that represents an action.
     * @return the longitude of the action.
     */
    private static double getLongitude(JsonObject json) {
        return json.get("longitude").getAsDouble();
    }

    /**
     * Returns the model of an action in the form a JSON object.
     *
     * @param json     a JSON object that represents an action.
     * @param services a pathfinder services object.
     * @return the model of the action.
     * @throws IllegalArgumentException when the model isn't a transport or commodity.
     */
    private static SubscribableCrudModel getModel(JsonObject json, PathfinderServices services) {
        JsonObject model = json.getAsJsonObject("commodity");
        //String type = model.get("commodity").getAsString();

        //if (type.equals(Pathfinder.TRANSPORT)) {
        //    return Transport.getInstance(model, services);
        //} else if (type.equals(Pathfinder.COMMODITY)) {
            return Commodity.getInstance(model, services);
        //}

        //throw new IllegalArgumentException("Illegal model type in action creation: " + type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        JsonObject json = new JsonObject();

        json.addProperty("latitude", this.getLatitude());
        json.addProperty("longitude", this.getLongitude());
        json.addProperty("status", this.getStatus().toString());
        if(this.getModel() != null) {
            json.addProperty("model", this.getModel().getPathName());
        }
        return json.toString();
    }
}
