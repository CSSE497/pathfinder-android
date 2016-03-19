package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Interface to the Pathfinder server's transport API. A transport may be create
 * by a {@link Cluster} object with the {@link Cluster#createTransport(double, double, TransportStatus, JsonObject)}
 * method.
 * </p>
 * <p>
 * Be careful with the update methods, they do not update the object immediately.
 * They send the updates to the pathfinder server. If the server responds the transport's
 * fields will then be updated. To listen for updates add a {@link TransportListener}.
 * </p>
 *
 * @author David Robinson
 * @see Cluster
 * @see TransportListener
 * @see TransportStatus
 */
public class Transport extends SubscribableCrudModel<TransportListener> {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Action.class);

    /**
     * Latitude of the transport.
     */
    private double latitude;

    /**
     * Longitude of the transport.
     */
    private double longitude;

    /**
     * Status of the transport.
     *
     * @see TransportStatus
     */
    private TransportStatus status;

    /**
     * Metadata of the transport.
     */
    private JsonObject metadata;

    /**
     * List of commodities being carried by the transport.
     */
    private List<Commodity> commodities;

    /**
     * Route of the transport.
     */
    private Route route;

    /**
     * The cluster to be created under.
     */
    private String createCluster;

    /**
     * Constructs a transport model. Sets the transport to default values.
     *
     * @param path     of the model.
     * @param services a pathfinder services object.
     * @throws IllegalArgumentException occurs when a transport has already been registered with the same path.
     */
    private Transport(String path, PathfinderServices services) {
        super(path, ModelType.TRANSPORT, services);

        logger.info("Constructing transport by parameters: " + path);

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(new Path(path, ModelType.TRANSPORT));
        if (isRegistered) {
            logger.error("Illegal Argument Exception: Transport path already exists: " + path);
            throw new IllegalArgumentException("Transport path already exists: " + path);
        } else if (path != null) {
            this.getServices().getRegistry().registerModel(this);
        }

        this.latitude = 0;
        this.longitude = 0;
        this.status = TransportStatus.OFFLINE;
        this.metadata = new JsonObject();
        this.commodities = new ArrayList<Commodity>();
        this.route = null;
        this.createCluster = null;
    }

    /**
     * Constructs a transport model with the specified values.
     *
     * @param path        of the model.
     * @param latitude    of the transport.
     * @param longitude   of the transport.
     * @param status      of the transport. If <tt>null</tt> it is set to {@link TransportStatus#OFFLINE}
     * @param metadata    of the transports. If <tt>null</tt> it is set to an empty JsonObject.
     * @param commodities being transported by this transport.
     * @param services    a pathfinder services object.
     */
    private Transport(String path, double latitude, double longitude, TransportStatus status, JsonObject metadata, List<Commodity> commodities, PathfinderServices services) {
        this(path, services);

        this.latitude = latitude;
        this.longitude = longitude;

        if (status == null) {
            this.status = TransportStatus.OFFLINE;
        } else {
            this.status = status;
        }

        if (metadata == null) {
            this.metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }

        this.commodities = commodities;

        this.route = null;
    }

    /**
     * Returns an instance of a transport model. If the transport is in the model registry, it will return that transport.
     * If the transport is not in the model registry a new transport with default values will be created.
     *
     * @param path     of the model.
     * @param services a pathfinder services object.
     * @return a transport.
     */
    public static Transport getInstance(String path, PathfinderServices services) {
        Transport transport = (Transport) services.getRegistry().getModel(new Path(path, ModelType.TRANSPORT));

        if (transport == null && Path.isValidPath(path)) {
            return new Transport(path, services);
        }

        logger.info("Finished getting transport instance: " + transport);

        return transport;
    }

    /**
     * Returns an instance of a transport model. If the transport is in the model registry, it will return that transport.
     * It will call update notifications for all changes to the transport.
     *
     * @param transportJson JSON that represents the transport.
     * @param services      a pathfinder services object.
     * @return a transport.
     * @throws IllegalArgumentException if the JSON doesn't represent a transport.
     */
    protected static Transport getInstance(JsonObject transportJson, PathfinderServices services) {
        if (!Transport.checkTransportFields(transportJson)) {
            logger.error("Illegal Argument Exception: Invalid JSON cannot be parsed to a transport " + transportJson);
            throw new IllegalArgumentException("Invalid JSON cannot be parsed to a transport " + transportJson);
        }

        String path = Transport.getPath(transportJson);
        Transport transport = Transport.getInstance(path, services);

        logger.info("Notifying transport of update: \nCurrent transport: " + transport + "\nNew JSON: " + transportJson);
        transport.notifyUpdate(null, transportJson);

        return transport;
    }

    /**
     * Checks for a field in the JSON.
     *
     * @param transportJson JSON to be checked.
     * @param field         to check for.
     * @return <tt>true</tt> if the JSON has the field. <tt>false</tt> otherwise.
     */
    private static boolean checkTransportField(JsonObject transportJson, String field) {
        return transportJson.has(field);
    }

    /**
     * Checks if the JSON can be parsed to a transport.
     *
     * @param transportJson JSON to be checked.
     * @return <tt>true</tt> if the JSON can be parsed to a transport. <tt>false</tt> otherwise.
     */
    private static boolean checkTransportFields(JsonObject transportJson) {
        return Transport.checkTransportField(transportJson, "id") &&
                Transport.checkTransportField(transportJson, "clusterId") &&
                Transport.checkTransportField(transportJson, "latitude") &&
                Transport.checkTransportField(transportJson, "longitude") &&
                Transport.checkTransportField(transportJson, "status") &&
                Transport.checkTransportField(transportJson, "metadata") &&
                transportJson.get("metadata").isJsonObject() &&
                Transport.checkTransportField(transportJson, "commodities") &&
                transportJson.get("commodities").isJsonArray();
    }

    /**
     * Returns the path of the transport from JSON that represents a transport.
     *
     * @param transportJson JSON that represents a transport
     * @return the path.
     */
    private static String getPath(JsonObject transportJson) {
        String path = transportJson.get("clusterId").getAsString();
        return path + "/" + transportJson.get("id").getAsString();
    }

    /**
     * Converts a string to a {@link TransportStatus} if possible, <tt>null</tt> otherwise.
     *
     * @param status string to convert.
     * @return the status.
     */
    private static TransportStatus getStatus(String status) {
        return TransportStatus.getStatus(status);
    }

    /**
     * Returns the value used in create request to the Pathfinder server
     *
     * @return the value JSON
     */
    @Override
    protected JsonObject createValueJson() {
        JsonObject json = new JsonObject();

        json.addProperty("clusterId", this.createCluster);
        json.addProperty("model", this.getModelType().toString());
        json.addProperty("latitude", this.getLatitude());
        json.addProperty("longitude", this.getLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.add("metadata", this.getMetadata());

        JsonArray commodities = new JsonArray();
        json.add("commodities", commodities);

        return json;
    }

    /**
     * Initializes a transport object with the specified parameters. This method should only be
     * called right before {@link Transport#create()} is called.
     *
     * @param latitude  of the transport.
     * @param longitude of the transport.
     * @param status    of the transport. If <tt>null</tt> it is set to {@link TransportStatus#OFFLINE}
     * @param metadata  of the transports. If <tt>null</tt> it is set to an empty JsonObject.
     * @param cluster   a string path of the cluster to create this transport under.
     */
    protected void initTransport(double latitude, double longitude, TransportStatus status, JsonObject metadata, String cluster) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.metadata = metadata;
        this.createCluster = cluster;
    }

    /**
     * Updates the location of this transport to specified coordinates.
     * This method updates the location of the transport on the pathfinder server.
     * The latitude and longitude are not updated in this object by this method.
     *
     * @param latitude  The latitude to change the location to.
     * @param longitude The longitude to change the location to.
     */
    public void updateLocation(double latitude, double longitude) {
        this.update(latitude, longitude, null, null);
    }

    /**
     * Returns the latitude of this transport.
     *
     * @return the latitude.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Sets the latitude of the transport.
     *
     * @param latitude the of the transport.
     */
    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude of this transport.
     *
     * @return the longitude.
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Sets the longitude of the transport.
     *
     * @param longitude the of the transport.
     */
    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the status of this transport.
     *
     * @return the status.
     */
    public TransportStatus getStatus() {
        return this.status;
    }

    /**
     * Sets the status of the transport.
     *
     * @param status the of the transport.
     */
    private void setStatus(TransportStatus status) {
        this.status = status;
    }

    /**
     * Updates the status of this transport to the specified status.
     * This method updates the status of the transport on the pathfinder server.
     * The status is not updated in this object by this method.
     *
     * @param status The status to change to.
     */
    public void updateStatus(TransportStatus status) {
        this.update(null, null, status, null);
    }

    /**
     * Returns the metadata of this transport.
     *
     * @return the metadata.
     */
    public JsonObject getMetadata() {
        return this.metadata;
    }

    /**
     * Sets the metadata of the transport.
     *
     * @param metadata the of the transport.
     */
    private void setMetadata(JsonObject metadata) {
        if (metadata == null) {
            this.metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }
    }

    /**
     * Updates the metadata of this transport to the specified metadata.
     * This method updates the metadata of the transport on the pathfinder server.
     * The metadata is not updated in this object by this method.
     *
     * @param metadata The metadata to change to.
     */
    public void updateMetadata(JsonObject metadata) {
        this.update(null, null, null, metadata);
    }

    /**
     * Returns an unmodifiable list of the commodities being carried by this transport.
     *
     * @return a list of commodities being carried.
     */
    public List<Commodity> getCommodities() {
        return Collections.<Commodity>unmodifiableList(this.commodities);
    }

    /**
     * Sets the commodities being carried by the transport.
     *
     * @param json JsonArray of commodities
     */
    private void setCommodities(JsonArray json) {
        List<Commodity> commodities = new ArrayList<Commodity>();
        for (JsonElement commodityje : json) {
            Commodity commodity = Commodity.getInstance(commodityje.getAsJsonObject(), this.getServices());
            commodities.add(commodity);
        }

        this.commodities = commodities;
    }

    /**
     * Returns the route of this transport.
     *
     * @return the route.
     */
    public Route getRoute() {
        return this.route;
    }

    /**
     * Sets the route of the transport.
     *
     * @param route the of the transport.
     */
    protected void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Sends update requests to the Pathfinder server. If a parameter is null it will
     * not be updated. This method does not update this transports fields.
     *
     * @param latitude  to update to.
     * @param longitude to update to.
     * @param status    to update to.
     * @param metadata  to update to.
     */
    public void update(Double latitude, Double longitude, TransportStatus status, JsonObject metadata) {
        JsonObject value = new JsonObject();

        if (latitude != null) {
            value.addProperty("latitude", latitude);
        }

        if (longitude != null) {
            value.addProperty("longitude", longitude);
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
    protected boolean updateFields(JsonObject json) {
        double prevLatitude;
        double prevLongitude;
        TransportStatus prevStatus;
        JsonObject prevMetadata;
        List<Commodity> prevCommodities;

        boolean updated = false;

        if (json.has("createCluster")) {
            this.createCluster = json.get("createCluster").getAsString();
        }

        prevLatitude = this.getLatitude();
        if (json.has("latitude")) {
            this.setLatitude(json.get("latitude").getAsDouble());
        }

        prevLongitude = this.getLongitude();
        if (json.has("longitude")) {
            this.setLongitude(json.get("longitude").getAsDouble());
        }

        prevStatus = this.getStatus();
        if (json.has("status")) {
            this.setStatus(Transport.getStatus(json.get("status").getAsString()));
        }

        prevMetadata = this.getMetadata();
        if (json.has("metadata")) {
            this.setMetadata(json.getAsJsonObject("metadata"));
        }

        prevCommodities = this.getCommodities();
        if (json.has("commodities")) {
            this.setCommodities(json.getAsJsonArray("commodities"));
        }

        List<TransportListener> listeners = this.getListeners();

        if (this.getLatitude() != prevLatitude || this.getLongitude() != prevLongitude) {
            logger.info("Transport " + this.getPathName() + " location updated: " + this.getLatitude() + "," + this.getLongitude());
            for (TransportListener listener : listeners) {
                listener.locationUpdated(this.getLatitude(), this.getLongitude());
            }
            updated = true;
        }

        if (!this.getStatus().equals(prevStatus)) {
            logger.info("Transport " + this.getPathName() + " status updated: " + this.getStatus());
            for (TransportListener listener : listeners) {
                listener.statusUpdated(this.getStatus());
            }
            updated = true;
        }

        if (!this.getMetadata().equals(prevMetadata)) {
            logger.info("Transport " + this.getPathName() + " metadata updated: " + this.getMetadata());
            for (TransportListener listener : listeners) {
                listener.metadataUpdated(this.getMetadata());
            }
            updated = true;
        }

        boolean updatedCommodities = false;
        List<Commodity> commodities = this.getCommodities();
        for (int k = 0; k < commodities.size(); k++) {
            if (k == prevCommodities.size() || !commodities.get(k).equals(prevCommodities.get(k))) {
                updatedCommodities = true;
                break;
            }
        }

        if (updatedCommodities) {
            logger.info("Transport " + this.getPathName() + " commodities updated: " + this.getCommodities());
            List<Commodity> commodities2 = this.getCommodities();
            for (TransportListener listener : listeners) {
                listener.commoditiesUpdated(commodities2);
            }
            updated = true;
        }

        Path parentPath = this.getParentPath();
        if (updated && this.getServices().getRegistry().isModelRegistered(parentPath)) {
            Cluster parentCluster = Cluster.getInstance(parentPath.getPathName(), this.getServices());

            boolean added = !parentCluster.getTransportsMap().containsKey(this.getPathName());

            if (added) {
                parentCluster.addTransport(this);
            }

            Collection<Transport> transports = parentCluster.getTransports();

            logger.info("Transport " + this.getPathName() + " calling parent cluster's update");

            List<ClusterListener> clusterListeners = parentCluster.getListeners();
            for (ClusterListener listener : clusterListeners) {
                if (added) {
                    listener.transportAdded(this);
                }

                listener.transportUpdated(this);
                listener.transportsUpdated(transports);
            }
        }

        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void route(JsonObject json, PathfinderServices services) {
        JsonObject route = json.getAsJsonObject("route");

        logger.info("Transport setting route: " + this.getPathName());
        this.route = new Route(route, services);

        logger.info("Transport updating route: " + this.getPathName());
        for (TransportListener listener : this.getListeners()) {
            listener.routed(this.getRoute());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("path", this.getPathName());
        json.addProperty("model", this.getModelType().toString());
        json.addProperty("latitude", this.getLatitude());
        json.addProperty("longitude", this.getLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.add("metadata", this.getMetadata());

        JsonArray commodities = new JsonArray();
        json.add("commodities", commodities);

        if (route != null) {
            json.addProperty("route", this.route.toString());
        }
        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
