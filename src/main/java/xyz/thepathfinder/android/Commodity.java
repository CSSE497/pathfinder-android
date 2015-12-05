package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public class Commodity extends PathfinderListenable<CommodityListener> {

    private PathfinderConnection connection;

    private Long id;
    private Long clusterId;
    private Double startLongitude;
    private Double startLatitude;
    private Double endLongitude;
    private Double endLatitude;
    private CommodityStatus status;
    private JsonObject metadata;

    protected Commodity(long clusterId, PathfinderConnection connection) {
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

    protected void setClusterId(long id) {
        this.clusterId = id;
    }

    public Double getStartLongitude() {
        return this.startLongitude;
    }

    public void setStartLongitude(double longitude) {
        this.startLongitude = longitude;
    }

    public Double getStartLatitude() {
        return this.startLatitude;
    }

    public void setStartLatitude(double latitude) {
        this.startLatitude = latitude;
    }

    public Double getEndLongitude() {
        return this.endLongitude;
    }

    public void setEndLongitude(double longitude) {
        this.endLongitude = longitude;
    }

    public Double getEndLatitude() {
        return this.endLatitude;
    }

    public void setEndLatitude(double latitude) {
        this.endLatitude = latitude;
    }

    public CommodityStatus getStatus() {
        return this.status;
    }

    public void setStatus(CommodityStatus status) {
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
