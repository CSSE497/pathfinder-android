package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public class Transport extends PathfinderListenable<TransportListener> {

    private PathfinderConnection connection;

    private Long id;
    private Long clusterId;
    private Double longitude;
    private Double latitude;
    private TransportStatus status;
    private JsonObject metadata;

    protected Transport(long clusterId, PathfinderConnection connection) {
        this.clusterId = clusterId;
        this.connection = connection;
    }

    public void connect() {
        //TODO implement
    }

    public void create() {
        //TODO implement
    }

    public void delete() {
        //TODO implement
    }

    public void update() {
        //TODO implement
    }

    public void subscribe() {
        //TODO implement
    }

    public void unsubscribe() {
        //TODO implement
    }

    public Long getId() {
        return this.id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return this.clusterId;
    }

    protected void setClusterId(long clusterId) {
        this.clusterId = clusterId;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public TransportStatus getStatus() {
        return this.status;
    }

    public void setStatus(TransportStatus status) {
        this.status = status;
    }

    public JsonObject getMetadata() {
        return this.metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }
}
