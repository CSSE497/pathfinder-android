package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * Interface to the Pathfinder server's commodity API. A commodity may be create
 * by a {@link Cluster} object with the {@link Cluster#createCommodity(double, double, double, double, CommodityStatus, JsonObject)}
 * method.
 *
 * <p>
 * Be careful with the update methods, they do not update the object immediately.
 * They send the updates to the pathfinder server. If the server responds the commodity's
 * fields will then be updated. To listen for updates add a {@link CommodityListener}.
 * </p>
 *
 * @author David Robinson
 * @see Cluster
 * @see CommodityListener
 * @see CommodityStatus
 * @see Transport
 */
public class Commodity extends SubscribableCrudModel<CommodityListener> {

    private static final Logger logger = LoggerFactory.getLogger(Action.class);

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
     * The transport caring the commodity.
     */
    private Long transportId;

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
     * @throws IllegalArgumentException occurs if the path has already been used to create a commodity.
     */
    protected Commodity(String path, PathfinderServices services) {
        super(path, ModelType.COMMODITY, services);

        logger.info("Constructing commodity by path: " + path);

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(new Path(path, ModelType.COMMODITY));
        if (isRegistered) {
            logger.error("Illegal Argument Exception: Commodity path already exists " + path);
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
        this.transportId = null;
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
     * @param transportId    the id of the transport that picked up the commodity, null if not in a transport.
     * @param services       a pathfinder services object.
     */
    protected Commodity(String path, double startLatitude, double startLongitude, double endLatitude, double endLongitude, CommodityStatus status, JsonObject metadata, Long transportId, PathfinderServices services) {
        this(path, services);

        logger.info("Constructing commodity by parameters: " + path);

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

        this.transportId = transportId;

        this.route = null;
    }

    /**
     * Returns a commodity that has been registered with the {@link ModelRegistry} or a new
     * commodity if one hasn't been created with that path.
     *
     * @param path     the path to model on the pathfinder server
     * @param services a pathfinder services object.
     * @return the commodity object created with the path specified.
     */
    protected static Commodity getInstance(String path, PathfinderServices services) {
        Commodity commodity = (Commodity) services.getRegistry().getModel(new Path(path, ModelType.COMMODITY));

        if (commodity == null && Path.isValidPath(path)) {
            return new Commodity(path, services);
        }

        logger.info("Finished getting commodity instance: " + commodity);

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
     * @throws IllegalArgumentException occurs when the commodity JSON cannot parse to a commodity
     */
    protected static Commodity getInstance(JsonObject commodityJson, PathfinderServices services) {
        if (!Commodity.checkCommodityFields(commodityJson)) {
            logger.error("Illegal Argument Exception: JSON could not be parse to a commodity " + commodityJson);
            throw new IllegalArgumentException("JSON could not be parsed to a commodity " + commodityJson);
        }

        String path = Commodity.getPath(commodityJson);
        Commodity commodity = Commodity.getInstance(path, services);

        logger.info("Notifying commodity of update: \nCurrent commodity: " + commodity + "\nNew JSON: " + commodityJson);
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
        return Commodity.checkCommodityField(commodityJson, "id") &&
                Commodity.checkCommodityField(commodityJson, "clusterId") &&
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
        String path = commodityJson.get("clusterId").getAsString();
        return path + "/" + commodityJson.get("id").getAsString();
        //TODO revert after path update
        //return commodityJson.get("path").getAsString();
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
        this.update(startLatitude, startLongitude, null, null, null, null, null);
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
     * Updates the end location of this commodity to specified coordinates.
     * This method updates the location of the commodity on the pathfinder server.
     * The latitude and longitude are not updated in this object by this method.
     *
     * @param endLatitude  The latitude to change the end location to.
     * @param endLongitude The longitude to change the end location to.
     */
    public void updateEndLocation(double endLatitude, double endLongitude) {
        this.update(null, null, endLatitude, endLongitude, null, null, null);
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
        for(CommodityStatus possibleStatus : CommodityStatus.values()) {
            if (possibleStatus.equals(status)) {
                return possibleStatus;
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
            logger.error("Illegal Argument Exception illegal commodity status: " + status);
            throw new IllegalArgumentException("Illegal commodity status: " + status);
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
        this.update(null, null, null, null, status, null, null);
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
        this.update(null, null, null, null, null, metadata, null);
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
     * Returns the transport carrying the commodity.
     *
     * @return the transport carrying the commodity, null if not being carried.
     */
    public Transport getTransport() {
        Long transportId = this.getTransportId();
        if(transportId == null) {
            return null;
        }

        return Transport.getInstance(this.getParentPath().getPathName() + transportId, this.getServices());
    }

    /**
     * Returns the transport id carrying the commodity.
     *
     * @return a transport id if being carried, null otherwise.
     */
    private Long getTransportId() {
        return this.transportId;
    }

    /**
     * Updates the transport of this commodity to the specified transport and sets the status to picked up.
     * This method updates the transport of the commodity on the pathfinder server.
     * The transport is not updated in this object by this method.
     *
     * @param transport The transport that picked up this commodity.
     */
    public void updatePickedUp(Transport transport) {
        Long id = Long.parseLong(transport.getName());
        this.update(null, null, null, null, CommodityStatus.PICKED_UP, null, id);
    }

    /**
     * Updates the commodity to have a status of dropped off.
     * This method updates the status of the commodity on the pathfinder server.
     * The status is not updated in this object by this method.
     */
    public void updateDroppedOff() {
        this.update(null, null, null, null, CommodityStatus.DROPPED_OFF, null, null);
    }

    /**
     * Sets the transport id field to a new transport.
     *
     * @param transportId of the transport carrying the commodity, null if not being carried.
     */
    private void setTransportId(Long transportId) {
        this.transportId = transportId;
    }

    /**
     * Sends update requests to the Pathfinder server. If a parameter is null it will
     * not be updated. This method does not update this commodities fields, it updates
     * the commodity on the pathfinder server.
     *
     * @param startLatitude  The start latitude to change to.
     * @param startLongitude The start longitude to change to.
     * @param endLatitude    The end latitude to change to.
     * @param endLongitude   The end longitude to change to.
     * @param status         The status to change to.
     * @param metadata       The metadata to change to.
     * @param transportId    The id of the transport that picked up the commodity.
     */
    public void update(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude, CommodityStatus status, JsonObject metadata, Long transportId) {
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

        if (transportId != null) {
            value.addProperty("vehicleId", transportId);
        }

        super.update(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JsonObject createValueJson() {
        JsonObject json = new JsonObject();

        json.addProperty("clusterId", this.getPathName());
        json.addProperty("model", this.getModelType().toString());
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
    @Override
    protected boolean updateFields(JsonObject json) {
        double prevStartLatitude;
        double prevStartLongitude;
        double prevEndLatitude;
        double prevEndLongitude;
        CommodityStatus prevStatus;
        JsonObject prevMetadata;
        Long prevTransportId;

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

        prevTransportId = this.getTransportId();
        if (json.has("vehicleId")) {
            this.setTransportId(json.get("vehicleId").getAsLong());
        } else {
            this.setTransportId(null);
        }

        List<CommodityListener> listeners = this.getListeners();

        if (this.getStartLatitude() != prevStartLatitude || this.getStartLongitude() != prevStartLongitude) {
            logger.info("Commodity " + this.getPath() + " start location updated: " + this.getStartLatitude() + "," + this.getStartLongitude());
            for (CommodityListener listener : listeners) {
                listener.startLocationUpdated(this.getStartLatitude(), this.getStartLongitude());
            }
            updated = true;
        }

        if (this.getEndLatitude() != prevEndLatitude || this.getEndLongitude() != prevEndLongitude) {
            logger.info("Commodity " + this.getPath() + " end location updated: " + this.getEndLatitude() + "," + this.getEndLongitude());
            for (CommodityListener listener : listeners) {
                listener.endLocationUpdated(this.getEndLatitude(), this.getEndLongitude());
            }
            updated = true;
        }

        if (!this.getStatus().equals(prevStatus)) {
            logger.info("Commodity " + this.getPath() + " status updated: " + this.getStatus());
            for (CommodityListener listener : listeners) {
                listener.statusUpdated(this.getStatus());
            }
            updated = true;
        }

        if (!this.getMetadata().equals(prevMetadata)) {
            logger.info("Commodity " + this.getPath() + " metadata updated: " + this.getMetadata());
            for (CommodityListener listener : listeners) {
                listener.metadataUpdated(this.getMetadata());
            }
            updated = true;
        }

        if (this.getTransportId() != null && !this.getTransportId().equals(prevTransportId)) {
            logger.info("Commodity " + this.getPath() + " transport updated: " + this.getTransportId());
            Transport transport = this.getTransport();
            for (CommodityListener listener : listeners) {
                listener.transportUpdated(transport);
            }
            updated = true;
        }

        Path parentPath = this.getParentPath();
        if (updated && this.getServices().getRegistry().isModelRegistered(parentPath)) {
            Cluster parentCluster = Cluster.getInstance(parentPath.getPathName(), this.getServices());

            boolean added = !parentCluster.getCommoditiesMap().containsKey(this.getPathName());

            if(added) {
                parentCluster.addCommodity(this);
            }

            Collection<Commodity> commodities = parentCluster.getCommodities();

            logger.info("Commodity " + this.getPath() + " calling parent cluster's update");

            List<ClusterListener> clusterListeners = parentCluster.getListeners();
            for (ClusterListener listener : clusterListeners) {
                if (added) {
                   listener.commodityAdded(this);
                }

                listener.commodityUpdated(this);
                listener.commoditiesUpdated(commodities);
            }
        }

        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void route(JsonObject json, PathfinderServices services) {
        JsonObject route = json.getAsJsonObject("value");

        logger.info("Commodity setting route: " + this.getPath());
        this.setRoute(new Route(route, services));

        logger.info("Commodity updating route: " + this.getPath());
        for (CommodityListener listener : this.getListeners()) {
            listener.routed(this.getRoute());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        JsonObject json = this.createValueJson();

        if(this.getRoute() != null) {
            json.addProperty("route", this.getRoute().toString());
        }

        return json.toString();
    }
}
