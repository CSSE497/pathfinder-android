package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author David Robinson
 */
public class Transport extends SubscribableCrudModel<TransportListener> {

    private Logger logger = Logger.getLogger(Transport.class.getName());
    private static final String MODEL = Pathfinder.TRANSPORT;

    private double longitude;
    private double latitude;
    private TransportStatus status;
    private JsonObject metadata;
    private Route route;

    protected Transport(String path, PathfinderServices services) {
        super(path, services);

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(path);
        if (isRegistered) {
            throw new IllegalArgumentException("Transport path already exists: " + path);
        } else {
            this.getServices().getRegistry().registerModel(this);
        }

        this.latitude = 0;
        this.longitude = 0;
        this.status = TransportStatus.OFFLINE;
        this.metadata = null;
        this.route = null;
    }

    protected Transport(String path, double latitude, double longitude, TransportStatus status, JsonObject metadata, PathfinderServices services) {
        this(path, services);

        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.metadata = metadata;
    }

    public static Transport getInstance(String path, PathfinderServices services) {
        Transport transport = (Transport) services.getRegistry().getModel(path);

        if (transport == null) {
            return new Transport(path, services);
        }

        return transport;
    }

    protected static Transport getInstance(JsonObject transportJson, PathfinderServices services) {
        if (!Transport.checkTransportFields(transportJson)) {
            throw new ClassCastException("Invalid JSON cannot be parsed to a Transport");
        }

        String path = Transport.getPath(transportJson);
        Transport transport = Transport.getInstance(path, services);

        transport.notifyUpdate(null, transportJson);

        return transport;
    }

    private static boolean checkTransportField(JsonObject transportJson, String field) {
        return transportJson.has(field);
    }

    private static boolean checkTransportFields(JsonObject transportJson) {
        return Transport.checkTransportField(transportJson, "path") &&
                Transport.checkTransportField(transportJson, "latitude") &&
                Transport.checkTransportField(transportJson, "longitude") &&
                Transport.checkTransportField(transportJson, "status") &&
                Transport.checkTransportField(transportJson, "metadata") &&
                !transportJson.get("metadata").isJsonObject();
    }


    private static String getPath(JsonObject transportJson) {
        return transportJson.get("path").getAsString();
    }

    public void updateLocation(double latitude, double longitude) {
        this.update(latitude, longitude, null, null);
    }

    public Double getLatitude() {
        return this.latitude;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void updateLatitude(double latitude) {
        this.update(latitude, null, null, null);
    }

    public Double getLongitude() {
        return this.longitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void updateLongitude(double longitude) {
        this.update(null, longitude, null, null);
    }

    public TransportStatus getStatus() {
        return this.status;
    }

    private void setStatus(TransportStatus status) {
        this.status = status;
    }

    private static TransportStatus getStatus(String status) {
        TransportStatus[] values = TransportStatus.values();
        for (TransportStatus value : values) {
            if (value.equals(status)) {
                return value;
            }
        }

        return null;
    }

    public void updateStatus(TransportStatus status) {
        this.update(null, null, status, null);
    }

    public JsonObject getMetadata() {
        return this.metadata;
    }

    private void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public void updateMetadata(JsonObject metadata) {
        this.update(null, null, null, metadata);
    }

    public Route getRoute() {
        return this.route;
    }

    private void setRoute(Route route) {
        this.route = route;
    }

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
            for (TransportListener listener : listeners) {
                listener.latitudeUpdated(this.getLatitude());
            }
            updated = true;
        }

        if (this.getLongitude() != prevLongitude) {
            for (TransportListener listener : listeners) {
                listener.longitudeUpdated(this.getLongitude());
            }
            updated = true;
        }

        if (this.getStatus().equals(prevStatus)) {
            for (TransportListener listener : listeners) {
                listener.statusUpdated(this.getStatus());
            }
            updated = true;
        }

        if (this.getMetadata().equals(prevMetadata)) {
            for (TransportListener listener : listeners) {
                listener.metadataUpdated(this.getMetadata());
            }
            updated = true;
        }

        if (updated) {
            String parentPath = this.getParentPath();
            Cluster parentCluster = Cluster.getInstance(parentPath, this.getServices());

            Collection<Transport> transports = parentCluster.getTransports();

            List<ClusterListener> clusterListeners = parentCluster.getListeners();
            for (ClusterListener listener : clusterListeners) {
                listener.transportUpdated(this);
                listener.transportsUpdated(transports);
            }
        }

        return updated;
    }

    protected void route(JsonObject json, PathfinderServices services) {
        JsonObject route = json.getAsJsonObject("value");
        this.route = new Route(route, services);
        for (TransportListener listener : this.getListeners()) {
            listener.routed(this.getRoute());
        }
    }
}
