package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public class Transport extends SubscribableCrudModel<TransportListener> {

    protected static final String MODEL = "Transport";

    private Long id;
    private Long clusterId;
    private Double longitude;
    private Double latitude;
    private TransportStatus status;
    private JsonObject metadata;
    private Route route;

    protected Transport(long clusterId, PathfinderConnection connection) {
        super(connection);
        this.clusterId = clusterId;
    }

    protected Transport(JsonObject transportJson, PathfinderConnection connection) {
        super(connection);

        this.checkTransportFields(transportJson);
        this.setTransportFields(transportJson);
    }

    private void checkTransportField(JsonObject transportJson, String field) {
        if(!transportJson.has(field)) {
            throw new ClassCastException("No " + field + " was found in the JSON");
        }
    }

    private void checkTransportFields(JsonObject transportJson) {
        this.checkTransportField(transportJson, "id");
        this.checkTransportField(transportJson, "latitude");
        this.checkTransportField(transportJson, "longitude");
        this.checkTransportField(transportJson, "status");
        this.checkTransportField(transportJson, "metadata");

        if(!transportJson.get("metadata").isJsonObject()) {
            throw new ClassCastException("Metadata was not a JSON object");
        }
    }

    private void setTransportFields(JsonObject transportJson) {
        this.setId(transportJson.get("id").getAsLong());
        this.setLatitude(transportJson.get("latitude").getAsDouble());
        this.setLongitude(transportJson.get("longitude").getAsDouble());
        this.setStatus(transportJson.get("status").getAsString());
        this.setMetadata(transportJson.get("metadata").getAsJsonObject());
    }

    @Override
    public boolean isConnected() {
        return this.id != null;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return this.clusterId;
    }

    private void setClusterId(long clusterId) {
        this.clusterId = clusterId;
    }

    public void updateLocation(double latitude, double longitude) {
        this.update(latitude, longitude, null, null);
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

    public Double getLatitude() {
        return this.latitude;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void updateLatitude(double latitude) {
        this.update(latitude, null, null, null);
    }

    public TransportStatus getStatus() {
        return this.status;
    }

    private void setStatus(String status) {
        TransportStatus[] values = TransportStatus.values();
        for(int k = 0; k < values.length; k++) {
            if(values[k].equals(status)) {
                this.status = values[k];
                break;
            }
        }
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

    @Override
    protected String getModel() {
        return Transport.MODEL;
    }

    public void update(Double latitude, Double longitude, TransportStatus status, JsonObject metadata) {
        JsonObject value = new JsonObject();

        if(latitude != null) {
            value.addProperty("latitude", latitude);
        }

        if(longitude != null) {
            value.addProperty("longitude", longitude);
        }

        if(status != null) {
            value.addProperty("status", status.toString());
        }

        if(metadata != null) {
            value.add("metadata", metadata);
        }

        super.update(value);
    }

    public void subscribe() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("id", this.getId());

        super.subscribe(model);
    }

    @Override
    protected void create() {
        if(this.getClusterId() == null) {
            throw new IllegalStateException("Cluster Id not set");
        }

        super.create();
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("clusterId", this.getClusterId());
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
