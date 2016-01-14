package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Interface to the Pathfinder server's transport API. A transport may be create
 * by a {@link Cluster} object with the {@link Cluster#createTransport(String, double, double, TransportStatus, JsonObject)}
 * method.
 *
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

    private static final Logger logger = Logger.getLogger(Transport.class.getName());

    /**
     * String used in the model field of the pathfinder requests.
     */
    private static final String MODEL = Pathfinder.TRANSPORT;

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
     * Route of the transport.
     */
    private Route route;

    /**
     * Constructs a transport model. Sets the transport to default values.
     *
     * @param path of the model.
     * @param services a pathfinder services object.
     */
    protected Transport(String path, PathfinderServices services) {
        super(path, services);

        logger.finest("Constructing transport by parameters: " + path);

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(path);
        if (isRegistered) {
            logger.warning("Illegal Argument Exception: Transport path already exists: " + path);
            throw new IllegalArgumentException("Transport path already exists: " + path);
        } else {
            this.getServices().getRegistry().registerModel(this);
        }

        this.latitude = 0;
        this.longitude = 0;
        this.status = TransportStatus.OFFLINE;
        this.metadata = new JsonObject();
        this.route = null;
    }

    /**
     * Constructs a transport model with the specified values.
     *
     * @param path of the model.
     * @param latitude of the transport.
     * @param longitude of the transport.
     * @param status of the transport. If <tt>null</tt> it is set to {@link TransportStatus#OFFLINE}
     * @param metadata of the transports. If <tt>null</tt> it is set to an empty JsonObject
     * @param services a pathfinder services object.
     */
    protected Transport(String path, double latitude, double longitude, TransportStatus status, JsonObject metadata, PathfinderServices services) {
        this(path, services);

        this.latitude = latitude;
        this.longitude = longitude;

        if(status == null) {
            this.status = TransportStatus.OFFLINE;
        } else {
            this.status = status;
        }

        if(metadata == null) {
            this.metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }

        this.route = null;
    }

    /**
     * Returns an instance of a transport model. If the transport is in the model registry, it will return that transport.
     * If the transport is not in the model registry a new transport with default values will be created.
     *
     * @param path of the model.
     * @param services a pathfinder services object.
     * @return a transport.
     */
    public static Transport getInstance(String path, PathfinderServices services) {
        Transport transport = (Transport) services.getRegistry().getModel(path);

        if (transport == null) {
            return new Transport(path, services);
        }

        logger.finest("Getting transport instance: " + transport);

        return transport;
    }

    /**
     * Returns an instance of a transport model. If the transport is in the model registry, it will return that transport.
     * It will call update notifications for all changes to the transport.
     *
     * @param transportJson JSON that represents the transport.
     * @param services a pathfinder services object.
     * @return a transport.
     * @throws IllegalArgumentException if the JSON doesn't represent a transport.
     */
    protected static Transport getInstance(JsonObject transportJson, PathfinderServices services) {
        if (!Transport.checkTransportFields(transportJson)) {
            logger.warning("Illegal Argument Exception: Invalid JSON cannot be parsed to a transport " + transportJson);
            throw new IllegalArgumentException("Invalid JSON cannot be parsed to a transport " + transportJson);
        }

        String path = Transport.getPath(transportJson);
        Transport transport = Transport.getInstance(path, services);

        logger.finest("Notifying transport of update: \nCurrent transport: " + transport + "\nNew JSON: " + transportJson);
        transport.notifyUpdate(null, transportJson);

        return transport;
    }

    /**
     * Checks for a field in the JSON.
     * @param transportJson JSON to be checked.
     * @param field to check for.
     * @return <tt>true</tt> if the JSON has the field. <tt>false</tt> otherwise.
     */
    private static boolean checkTransportField(JsonObject transportJson, String field) {
        return transportJson.has(field);
    }

    /**
     * Checks if the JSON can be parsed to a transport.
     * @param transportJson JSON to be checked.
     * @return <tt>true</tt> if the JSON can be parsed to a transport. <tt>false</tt> otherwise.
     */
    private static boolean checkTransportFields(JsonObject transportJson) {
        return Transport.checkTransportField(transportJson, "path") &&
                Transport.checkTransportField(transportJson, "latitude") &&
                Transport.checkTransportField(transportJson, "longitude") &&
                Transport.checkTransportField(transportJson, "status") &&
                Transport.checkTransportField(transportJson, "metadata") &&
                !transportJson.get("metadata").isJsonObject();
    }

    /**
     * Returns the path of the transport from JSON that represents a transport.
     *
     * @param transportJson JSON that represents a transport
     * @return the path.
     */
    private static String getPath(JsonObject transportJson) {
        return transportJson.get("path").getAsString();
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
    public Double getLatitude() {
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
     * Updates the latitude of this transport to the specified latitude.
     * This method updates the latitude of the transport on the pathfinder server.
     * The latitude is not updated in this object by this method.
     *
     * @param latitude The latitude to change to.
     */
    public void updateLatitude(double latitude) {
        this.update(latitude, null, null, null);
    }

    /**
     * Returns the longitude of this transport.
     *
     * @return the longitude.
     */
    public Double getLongitude() {
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
     * Updates the longitude of this transport to the specified longitude.
     * This method updates the longitude of the transport on the pathfinder server.
     * The longitude is not updated in this object by this method.
     *
     * @param longitude The longitude to change to.
     */
    public void updateLongitude(double longitude) {
        this.update(null, longitude, null, null);
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
     * Converts a string to a {@link TransportStatus} if possible, <tt>null</tt> otherwise.
     *
     * @param status string to convert.
     * @return the status.
     */
    private static TransportStatus getStatus(String status) {
        TransportStatus[] values = TransportStatus.values();
        for (TransportStatus value : values) {
            if (value.equals(status)) {
                return value;
            }
        }

        return null;
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
        if(metadata == null) {
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
    private void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Sends update requests to the Pathfinder server. If a parameter is null it will
     * not be updated. This method does not update this transports fields.
     *
     * @param latitude to update to.
     * @param longitude to update to.
     * @param status to update to.
     * @param metadata to update to.
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
    protected String getModel() {
        return Transport.MODEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JsonObject createValueJson() {
        JsonObject json = new JsonObject();

        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());
        json.addProperty("latitude", this.getLatitude());
        json.addProperty("longitude", this.getLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.add("metadata", this.getMetadata());

        return json;
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

        boolean updated = false;

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
            this.setMetadata(json.get("metadata").getAsJsonObject());
        }

        List<TransportListener> listeners = this.getListeners();

        if (this.getLatitude() != prevLatitude) {
            logger.finest("Transport " + this.getPath() + " latitude updated: " + this.getMetadata());
            for (TransportListener listener : listeners) {
                listener.latitudeUpdated(this.getLatitude());
            }
            updated = true;
        }

        if (this.getLongitude() != prevLongitude) {
            logger.finest("Transport " + this.getPath() + " longitude updated: " + this.getMetadata());
            for (TransportListener listener : listeners) {
                listener.longitudeUpdated(this.getLongitude());
            }
            updated = true;
        }

        if (this.getStatus().equals(prevStatus)) {
            logger.finest("Transport " + this.getPath() + " status updated: " + this.getMetadata());
            for (TransportListener listener : listeners) {
                listener.statusUpdated(this.getStatus());
            }
            updated = true;
        }

        if (this.getMetadata().equals(prevMetadata)) {
            logger.finest("Transport " + this.getPath() + " metadata updated: " + this.getMetadata());
            for (TransportListener listener : listeners) {
                listener.metadataUpdated(this.getMetadata());
            }
            updated = true;
        }

        String parentPath = this.getParentPath();
        if (updated && this.getServices().getRegistry().isModelRegistered(parentPath)) {
            Cluster parentCluster = Cluster.getInstance(parentPath, this.getServices());
            Collection<Transport> transports = parentCluster.getTransports();

            logger.finest("Transport " + this.getPath() + " calling parent cluster's update");

            List<ClusterListener> clusterListeners = parentCluster.getListeners();
            for (ClusterListener listener : clusterListeners) {
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
        JsonObject route = json.getAsJsonObject("value");

        logger.finest("Transport setting route: " + this.getPath());
        this.route = new Route(route, services);

        logger.finest("Transport updating route: " + this.getPath());
        for (TransportListener listener : this.getListeners()) {
            listener.routed(this.getRoute());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        JsonObject json = this.createValueJson();

        if(route != null) {
            json.addProperty("route", this.route.toString());
        }

        return json.toString();
    }
}
