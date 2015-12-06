package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public class Commodity extends SubscribableCrudModel<CommodityListener> {

    protected static final String MODEL = "Commodity";

    private Long id;
    private Long clusterId;
    private Double startLongitude;
    private Double startLatitude;
    private Double endLongitude;
    private Double endLatitude;
    private CommodityStatus status;
    private JsonObject metadata;

    protected Commodity(long clusterId, PathfinderConnection connection) {
        super(connection);
        this.clusterId = clusterId;
    }

    protected Commodity(JsonObject json, PathfinderConnection connection) {
        super(connection);
        //TODO check json
        //TODO initialize object
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

    private void setClusterId(long id) {
        this.clusterId = id;
    }

    public void updateStartLocation(double startLatitude, double startLongitude) {
        this.update(startLatitude, startLongitude, null, null, null, null);
    }

    public Double getStartLongitude() {
        return this.startLongitude;
    }

    private void setStartLongitude(double longitude) {
        this.startLongitude = longitude;
    }

    public void updateStartLongitude(double startLongitude) {
        this.update(null, startLongitude, null, null, null, null);
    }

    public Double getStartLatitude() {
        return this.startLatitude;
    }

    private void setStartLatitude(double latitude) {
        this.startLatitude = latitude;
    }

    public void updateStartLatitude(double startLatitude) {
        this.update(startLatitude, null, null, null, null, null);
    }

    public void updateEndLocation(double endLatitude, double endLongitude) {
        this.update(null, null, endLatitude, endLongitude, null, null);
    }

    public Double getEndLongitude() {
        return this.endLongitude;
    }

    private void setEndLongitude(double longitude) {
        this.endLongitude = longitude;
    }

    public void updateEndLongitude(double endLongitude) {
        this.update(null, null, null, endLongitude, null, null);
    }

    public Double getEndLatitude() {
        return this.endLatitude;
    }

    private void setEndLatitude(double latitude) {
        this.endLatitude = latitude;
    }

    public void updateEndLatitude(double endLatitude) {
        this.update(null, null, endLatitude, null, null, null);
    }

    public CommodityStatus getStatus() {
        return this.status;
    }

    private void setStatus(CommodityStatus status) {
        this.status = status;
    }

    public void updateStatus(CommodityStatus status) {
        this.update(null, null, null, null, status, null);
    }

    public JsonObject getMetadata() {
        return this.metadata;
    }

    private void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public void updateMetadata(JsonObject metadata) {
        this.update(null, null, null, null, null, metadata);
    }

    @Override
    protected String getModel() {
        return Transport.MODEL;
    }

    public void update(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude, CommodityStatus status, JsonObject metadata) {
        JsonObject value = new JsonObject();

        if(startLatitude != null) {
            value.addProperty("startLatitude", startLatitude);
        }

        if(startLongitude != null) {
            value.addProperty("startLongitude", startLongitude);
        }

        if(endLatitude != null) {
            value.addProperty("endLatitude", endLatitude);
        }

        if(endLongitude != null) {
            value.addProperty("endLongitude", endLongitude);
        }

        if(status != null) {
            value.addProperty("status", status.toString());
        }

        if(metadata != null) {
            value.add("metadata", metadata);
        }

        super.update(value);
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("startLatitude", this.getStartLatitude());
        json.addProperty("startLongitude", this.getStartLongitude());
        json.addProperty("endLatitude", this.getEndLatitude());
        json.addProperty("endLongitude", this.getEndLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.addProperty("clusterId", this.getClusterId());
        json.add("metadata", this.getMetadata());

        return json;
    }

    @Override
    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }

}
