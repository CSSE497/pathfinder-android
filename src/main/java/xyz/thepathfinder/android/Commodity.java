package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Interface to the Pathfinder server's commodity API. A commodity may be create
 * by a {@link Cluster} object with the {@link Cluster#createCommodity(JsonObject, PathfinderServices)}
 * method.
 *
 * <p>
 * Be careful with the update methods, they do not update the object immediately.
 * They send the updates to the pathfinder server. If server responds the commodity's
 * fields will then be updated. To listen for updates add a {@link CommodityListener}.
 * </p>
 *
 * @author David Robinson
 * @see Cluster
 * @see CommodityListener
 * @see Transport
 */
public class Commodity extends SubscribableCrudModel<CommodityListener> {

    private Logger logger = Logger.getLogger(Commodity.class.getName());

    /**
     * String used in the model field of the pathfinder requests.
     */
    private static final String MODEL = Pathfinder.COMMODITY;

    /**
     * The pickup latitude of the commodity.
     */
    private double startLatitude;

    /**
     * The pickup longitude of the commodity.
     */
    private double startLongitude;

    /**
     * The drop off latitude of the commodity.
     */
    private double endLatitude;

    /**
     * The drop off longitude of the commodity.
     */
    private double endLongitude;

    /**
     * The current status of this commodity.
     */
    private CommodityStatus status;

    /**
     * The metadata of this commodity.
     */
    private JsonObject metadata;

    /**
     * The route of this commodity.
     */
    private Route route;

    /**
     * Constructs a commodity object with the path specified. When creating the commodity
     * it uses default values, so that requests are fully qualified. The default values
     * are zero for all the locations, the status is inactive, and the metadata is an
     * empty JSON object.
     *
     * @param path     the path to commodity
     * @param services a pathfinder services objects
     */
    protected Commodity(String path, PathfinderServices services) {
        super(path, services);

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(path);
        if (isRegistered) {
            throw new IllegalArgumentException("Commodity path already exists: " + path);
        } else {
            this.getServices().getRegistry().registerModel(this);
        }

        this.startLatitude = 0;
        this.startLongitude = 0;
        this.endLatitude = 0;
        this.endLongitude = 0;
        this.status = CommodityStatus.INACTIVE;
        this.metadata = new JsonObject();

        this.route = null;
    }

    /**
     * Constructs a commodity object with the specified parameters.
     *
     * @param path           the path on the Pathfinder server.
     * @param startLatitude  the pickup latitude of the commodity.
     * @param startLongitude the pickup longitude of the commodity.
     * @param endLatitude    the drop off latitude of the commodity.
     * @param endLongitude   the drop off longitude of the commodity.
     * @param status         the current status of the commodity.
     * @param metadata       a JSON object that holds metadata for the commodity.
     * @param services       a pathfinder services object.
     */
    protected Commodity(String path, double startLatitude, double startLongitude, double endLatitude, double endLongitude, CommodityStatus status, JsonObject metadata, PathfinderServices services) {
        this(path, services);

        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;

        if (status == null) {
            this.status = CommodityStatus.INACTIVE;
        } else {
            this.status = status;
        }

        if (metadata == null) {
            this.metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }

        this.route = null;
    }

    /**
     * Returns a commodity that has been registered with the {@link ModelRegistry} or a new
     * commodity if one hasn't been created with that path.
     *
     * @param path     the path to model on the pathfinder server
     * @param services a pathfinder services object.
     * @return the commodity object created with the path specified.
     * @throws IllegalArgumentException if the path requested is already used by a different model type.
     */
    protected static Commodity getInstance(String path, PathfinderServices services) {
        Commodity commodity = (Commodity) services.getRegistry().getModel(path);

        if (commodity == null) {
            return new Commodity(path, services);
        }

        return commodity;
    }

    /**
     * Returns a commodity that has been registered with the {@link ModelRegistry} or a new
     * commodity if one hasn't been created with that path. The commodity will be updated to
     * reflect the values in JSON object provided.
     *
     * @param commodityJson a JSON object that represents a commodity.
     * @param services      a pathfinder services object.
     * @return the commodity object created with the path specified.
     * @throws IllegalArgumentException if the path requested is already used by a different model type.
     */
    protected static Commodity getInstance(JsonObject commodityJson, PathfinderServices services) {
        if (!Commodity.checkCommodityFields(commodityJson)) {
            throw new IllegalArgumentException("JSON could not be parsed to a commodity");
        }

        String path = Commodity.getPath(commodityJson);
        Commodity commodity = Commodity.getInstance(path, services);

        commodity.notifyUpdate(null, commodityJson);

        return commodity;
    }

    /**
     * Checks a JSON object for a specific field.
     *
     * @param commodityJson a JSON object that represents a commodity.
     * @param field         the field to check for.
     * @return <tt>true</tt> if the JSON object has the field, <tt>false</tt> otherwise.
     */
    private static boolean checkCommodityField(JsonObject commodityJson, String field) {
        return commodityJson.has(field);
    }

    /**
     * Checks if a JSON object can be parsed to a commodity.
     *
     * @param commodityJson JSON object to check.
     * @return <tt>true</tt> if the JSON object can be parsed to a commodity,
     * <tt>false</tt> otherwise.
     */
    private static boolean checkCommodityFields(JsonObject commodityJson) {
        return Commodity.checkCommodityField(commodityJson, "path") &&
                Commodity.checkCommodityField(commodityJson, "startLatitude") &&
                Commodity.checkCommodityField(commodityJson, "startLongitude") &&
                Commodity.checkCommodityField(commodityJson, "endLatitude") &&
                Commodity.checkCommodityField(commodityJson, "endLongitude") &&
                Commodity.checkCommodityField(commodityJson, "status") &&
                Commodity.checkCommodityField(commodityJson, "metadata") &&
                commodityJson.get("metadata").isJsonObject();
    }

    /**
     * Returns the path for a JSON object that represents a commodity.
     *
     * @param commodityJson a JSON object that represents a commodity.
     * @return the path of the object.
     */
    private static String getPath(JsonObject commodityJson) {
        return commodityJson.get("path").getAsString();
    }

    /**
     * Updates the start location of this commodity to specified coordinates.
     * This method updates the location of the commodity on the pathfinder server.
     * The latitude and longitude are not updated in this object by this method.
     *
     * @param startLatitude  The latitude to change the start location to.
     * @param startLongitude The longitude to change the start location to.
     */
    public void updateStartLocation(double startLatitude, double startLongitude) {
        this.update(startLatitude, startLongitude, null, null, null, null);
    }

    /**
     * Returns the current start latitude of the commodity.
     *
     * @return The current start latitude of the commodity.
     */
    public double getStartLatitude() {
        return this.startLatitude;
    }

    /**
     * Sets the start latitude field to a new latitude
     *
     * @param latitude the latitude to change to.
     */
    private void setStartLatitude(double latitude) {
        this.startLatitude = latitude;
    }

    /**
     * Updates the start latitude of this commodity to the specified latitude.
     * This method updates the start latitude of the commodity on the pathfinder server.
     * The start latitude is not updated in this object by this method.
     *
     * @param startLatitude The start latitude to change to.
     */
    public void updateStartLatitude(double startLatitude) {
        this.update(startLatitude, null, null, null, null, null);
    }

    /**
     * Returns the current start longitude of the commodity.
     *
     * @return The current start longitude of the commodity.
     */
    public double getStartLongitude() {
        return this.startLongitude;
    }

    /**
     * Sets the start longitude field to a new longitude
     *
     * @param longitude the longitude to change to.
     */
    private void setStartLongitude(double longitude) {
        this.startLongitude = longitude;
    }

    /**
     * Updates the start longitude of this commodity to the specified longitude.
     * This method updates the start longitude of the commodity on the pathfinder server.
     * The start longitude is not updated in this object by this method.
     *
     * @param startLongitude The start longitude to change to.
     */
    public void updateStartLongitude(double startLongitude) {
        this.update(null, startLongitude, null, null, null, null);
    }

    /**
     * Updates the end location of this commodity to specified coordinates.
     * This method updates the location of the commodity on the pathfinder server.
     * The latitude and longitude are not updated in this object by this method.
     *
     * @param endLatitude  The latitude to change the end location to.
     * @param endLongitude The longitude to change the end location to.
     */
    public void updateEndLocation(double endLatitude, double endLongitude) {
        this.update(null, null, endLatitude, endLongitude, null, null);
    }

    /**
     * Returns the current end latitude of the commodity.
     *
     * @return The current end latitude of the commodity.
     */
    public double getEndLatitude() {
        return this.endLatitude;
    }

    /**
     * Sets the end latitude field to a new latitude
     *
     * @param latitude the latitude to change to.
     */
    private void setEndLatitude(double latitude) {
        this.endLatitude = latitude;
    }

    /**
     * Updates the end latitude of this commodity to the specified latitude.
     * This method updates the end latitude of the commodity on the pathfinder server.
     * The end latitude is not updated in this object by this method.
     *
     * @param endLatitude The end latitude to change to.
     */
    public void updateEndLatitude(double endLatitude) {
        this.update(null, null, endLatitude, null, null, null);
    }

    /**
     * Returns the current end longitude of the commodity.
     *
     * @return The current end longitude of the commodity.
     */
    public double getEndLongitude() {
        return this.endLongitude;
    }

    /**
     * Sets the end longitude field to a new longitude
     *
     * @param longitude the longitude to change to.
     */
    private void setEndLongitude(double longitude) {
        this.endLongitude = longitude;
    }

    /**
     * Updates the end longitude of this commodity to the specified longitude.
     * This method updates the end longitude of the commodity on the pathfinder server.
     * The end longitude is not updated in this object by this method.
     *
     * @param endLongitude The end longitude to change to.
     */
    public void updateEndLongitude(double endLongitude) {
        this.update(null, null, null, endLongitude, null, null);
    }

    /**
     * Returns the current status of the commodity. See {@link CommodityStatus}
     * for the status's available to commodities.
     *
     * @return The current status of the commodity.
     */
    public CommodityStatus getStatus() {
        return this.status;
    }

    /**
     * Returns the enum version of a status from a string.
     *
     * @param status the status as a string.
     * @return the status as an enum.
     */
    private static CommodityStatus getStatus(String status) {
        CommodityStatus[] values = CommodityStatus.values();
        for (int k = 0; k < values.length; k++) {
            if (values[k].equals(status)) {
                return values[k];
            }
        }

        return null;
    }

    /**
     * Sets the status field to a new status
     *
     * @param status the status to change to.
     * @throws IllegalArgumentException when an illegal status is provided
     */
    private void setStatus(CommodityStatus status) {
        if (status != null) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Illegal status");
        }
    }

    /**
     * Sets the status field to a new status
     *
     * @param status the status to change to.
     * @throws IllegalArgumentException when an illegal status is provided
     */
    private void setStatus(String status) {
        this.setStatus(Commodity.getStatus(status));
    }

    /**
     * Updates the status of this commodity to the specified status.
     * This method updates the status of the commodity on the pathfinder server.
     * The status is not updated in this object by this method.
     *
     * @param status The status to change to.
     */
    public void updateStatus(CommodityStatus status) {
        this.update(null, null, null, null, status, null);
    }

    /**
     * Returns the metadata of the commodity in the form of a Json Object.
     *
     * @return The metadata of the commodity.
     */
    public JsonObject getMetadata() {
        return this.metadata;
    }

    /**
     * Sets the metadata field to a new JSON object. If null it is set to
     * an empty JSON object.
     *
     * @param metadata the JSON object to change to.
     */
    private void setMetadata(JsonObject metadata) {
        if (metadata == null) {
            this.metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }
    }

    /**
     * Updates the metadata of this commodity to the specified Json Object.
     * This method updates the metadata of the commodity on the pathfinder server.
     * The metadata is not updated in this object by this method.
     *
     * @param metadata The metadata to change to.
     */
    public void updateMetadata(JsonObject metadata) {
        this.update(null, null, null, null, null, metadata);
    }

    /**
     * Returns the route for this commodity.
     *
     * @return a route.
     */
    public Route getRoute() {
        return this.route;
    }

    /**
     * Sets the route field to a new route
     *
     * @param route the route to change to.
     */
    private void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Sends update requests to the Pathfinder server. If a parameter is null it will
     * not be updated. This method does not update this commodities fields.
     *
     * @param startLatitude  The start latitude to change to.
     * @param startLongitude The start longitude to change to.
     * @param endLatitude    The end latitude to change to.
     * @param endLongitude   The end longitude to change to.
     * @param status         The status to change to.
     * @param metadata       The metadata to change to.
     */
    public void update(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude, CommodityStatus status, JsonObject metadata) {
        JsonObject value = new JsonObject();

        if (startLatitude != null) {
            value.addProperty("startLatitude", startLatitude);
        }

        if (startLongitude != null) {
            value.addProperty("startLongitude", startLongitude);
        }

        if (endLatitude != null) {
            value.addProperty("endLatitude", endLatitude);
        }

        if (endLongitude != null) {
            value.addProperty("endLongitude", endLongitude);
        }

        if (status != null) {
            value.addProperty("status", status.toString());
        }

        if (metadata != null) {
            value.add("metadata", metadata);
        }

        super.update(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModel() {
        return Commodity.MODEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JsonObject createValueJson() {
        JsonObject json = new JsonObject();

        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());
        json.addProperty("startLatitude", this.getStartLatitude());
        json.addProperty("startLongitude", this.getStartLongitude());
        json.addProperty("endLatitude", this.getEndLatitude());
        json.addProperty("endLongitude", this.getEndLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.add("metadata", this.getMetadata());

        return json;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean updateFields(JsonObject json) {
        double prevStartLatitude;
        double prevStartLongitude;
        double prevEndLatitude;
        double prevEndLongitude;
        CommodityStatus prevStatus;
        JsonObject prevMetadata;

        boolean updated = false;

        prevStartLatitude = this.getStartLatitude();
        if (json.has("startLatitude")) {
            this.setStartLatitude(json.get("startLatitude").getAsDouble());
        }

        prevStartLongitude = this.getStartLongitude();
        if (json.has("startLongitude")) {
            this.setStartLongitude(json.get("startLongitude").getAsDouble());
        }

        prevEndLatitude = this.getEndLatitude();
        if (json.has("endLatitude")) {
            this.setEndLatitude(json.get("endLatitude").getAsDouble());
        }

        prevEndLongitude = this.getEndLongitude();
        if (json.has("endLongitude")) {
            this.setEndLongitude(json.get("endLongitude").getAsDouble());
        }

        prevStatus = this.getStatus();
        if (json.has("status")) {
            this.setStatus(Commodity.getStatus(json.get("status").getAsString()));
        }

        prevMetadata = this.getMetadata();
        if (json.has("metadata")) {
            this.setMetadata(json.get("metadata").getAsJsonObject());
        }

        List<CommodityListener> listeners = this.getListeners();

        if (this.getStartLatitude() != prevStartLatitude) {
            for (CommodityListener listener : listeners) {
                listener.startLatitudeUpdated(this.getStartLatitude());
            }
            updated = true;
        }

        if (this.getEndLongitude() != prevStartLongitude) {
            for (CommodityListener listener : listeners) {
                listener.startLongitudeUpdated(this.getStartLongitude());
            }
            updated = true;
        }


        if (this.getEndLatitude() != prevEndLatitude) {
            for (CommodityListener listener : listeners) {
                listener.endLatitudeUpdated(this.getEndLatitude());
            }
            updated = true;
        }

        if (this.getEndLongitude() != prevEndLongitude) {
            for (CommodityListener listener : listeners) {
                listener.endLongitudeUpdated(this.getEndLongitude());
            }
            updated = true;
        }

        if (this.getStatus().equals(prevStatus)) {
            for (CommodityListener listener : listeners) {
                listener.statusUpdated(this.getStatus());
            }
            updated = true;
        }

        if (this.getMetadata().equals(prevMetadata)) {
            for (CommodityListener listener : listeners) {
                listener.metadataUpdated(this.getMetadata());
            }
            updated = true;
        }

        if (updated) {
            String parentPath = this.getParentPath();
            Cluster parentCluster = Cluster.getInstance(parentPath, this.getServices());

            Collection<Commodity> commodities = parentCluster.getCommodities();

            List<ClusterListener> clusterListeners = parentCluster.getListeners();
            for (ClusterListener listener : clusterListeners) {
                listener.commodityUpdated(this);
                listener.commoditiesUpdated(commodities);
            }
        }

        return updated;
    }

    /**
     * {@inheritDoc}
     */
    protected void route(JsonObject json, PathfinderServices services) {
        JsonObject route = json.getAsJsonObject("value");
        this.setRoute(new Route(route, services));
        for (CommodityListener listener : this.getListeners()) {
            listener.routed(this.getRoute());
        }
    }
}
