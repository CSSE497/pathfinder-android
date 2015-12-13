package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Transport extends SubscribableCrudModel<TransportListener> {

    protected static final String MODEL = Pathfinder.TRANSPORT;

    private static final Map<String, Transport> transports = new HashMap<String, Transport>();

    private double longitude;
    private double latitude;
    private TransportStatus status;
    private JsonObject metadata;
    private Route route;

    private boolean isConnected;

    protected Transport(String path, double latitude, double longitude, TransportStatus status, JsonObject metadata) {
        super(path);

        this.latitude = latitude;
        this.longitude = longitude;

        if (status == null) {
            this.status = TransportStatus.OFFLINE;
        } else {
            this.status = status;
        }

        if (metadata == null) {
            metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }

        Transport transport = Transport.getInstance(path);
        if (transport != null) {
            throw new IllegalArgumentException("Transport path already exists: " + path);
        } else {
            Transport.transports.put(path, this);
        }

        this.isConnected = false;
    }

    public static Transport getInstance(String path) {
        return (Transport) PathfinderModelRegistry.getModel(path, Transport.MODEL);
    }

    protected static Transport getInstance(JsonObject transportJson) {
        Transport.checkTransportFields(transportJson);

        String path = Transport.getPath(transportJson);
        Transport transport = Transport.getInstance(path);

        if (transport != null) {
            double latitude = Transport.getLatitude(transportJson);
            double longitude = Transport.getLongitude(transportJson);
            TransportStatus status = Transport.getStatus(transportJson);
            JsonObject metadata = Transport.getMetadata(transportJson);
            transport = new Transport(path,
                    latitude,
                    longitude,
                    status,
                    metadata);
        } else {
            transport.setTransportFields(transportJson);
        }

        return transport;
    }

    private static void checkTransportField(JsonObject transportJson, String field) {
        if (!transportJson.has(field)) {
            throw new ClassCastException("No " + field + " was found in the JSON");
        }
    }

    private static void checkTransportFields(JsonObject transportJson) {
        Transport.checkTransportField(transportJson, "path");
        Transport.checkTransportField(transportJson, "latitude");
        Transport.checkTransportField(transportJson, "longitude");
        Transport.checkTransportField(transportJson, "status");
        Transport.checkTransportField(transportJson, "metadata");

        if (!transportJson.get("metadata").isJsonObject()) {
            throw new ClassCastException("Metadata was not a JSON object");
        }
    }

    private void setTransportFields(JsonObject transportJson) {
        this.setLatitude(Transport.getLatitude(transportJson));
        this.setLongitude(Transport.getLongitude(transportJson));
        this.setStatus(Transport.getStatus(transportJson));
        this.setMetadata(Transport.getMetadata(transportJson));
    }

    private static String getPath(JsonObject transportJson) {
        return transportJson.get("path").getAsString();
    }

    private static double getLatitude(JsonObject transportJson) {
        return transportJson.get("latitude").getAsDouble();
    }

    private static double getLongitude(JsonObject transportJson) {
        return transportJson.get("longitude").getAsDouble();
    }

    private static TransportStatus getStatus(JsonObject transportJson) {
        return Transport.getStatus(transportJson.get("status").getAsString());
    }

    private static JsonObject getMetadata(JsonObject transportJson) {
        return transportJson.get("metadata").getAsJsonObject();
    }

    @Override
    public boolean isConnected() {
        return this.isConnected;
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
        for (int k = 0; k < values.length; k++) {
            if (values[k].equals(status)) {
                return values[k];
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

    public void subscribe() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("path", this.getPath());

        super.subscribe(model);
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("latitude", this.getLatitude());
        json.addProperty("longitude", this.getLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.add("metadata", this.getMetadata());

        return json;
    }

    @Override
    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }
}
