package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

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
 * @see Cluster
 * @see CommodityListener
 * @see Transport
 */
public class Commodity extends SubscribableCrudModel<CommodityListener> {

    private static final String MODEL = Pathfinder.COMMODITY;

    private double startLongitude;
    private double startLatitude;
    private double endLongitude;
    private double endLatitude;
    private CommodityStatus status;
    private JsonObject metadata;

    protected Commodity(String path, double startLatitude, double startLongitude, double endLatitude, double endLongitude, CommodityStatus status, JsonObject metadata, PathfinderServices services) {
        super(path, services);

        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;

        if(status == null) {
            this.status = CommodityStatus.INACTIVE;
        } else {
            this.status = status;
        }

        if(metadata == null) {
            this.metadata = new JsonObject();
        } else {
            this.metadata = metadata;
        }

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(path);
        if(isRegistered) {
            throw new IllegalArgumentException("Commodity path already exists: " + path);
        } else {
            this.getServices().getRegistry().registerModel(this);
        }
    }

    protected static Commodity getInstance(String path, PathfinderServices services) {
        return (Commodity) services.getRegistry().getModel(path, Commodity.MODEL);
    }

    protected static Commodity getInstance(JsonObject commodityJson, PathfinderServices services) {
        Commodity.checkCommodityFields(commodityJson);

        String path = Commodity.getPath(commodityJson);
        Commodity commodity = Commodity.getInstance(path, services);

        if(commodity == null) {
            double startLatitude = Commodity.getStartLatitude(commodityJson);
            double startLongitude = Commodity.getStartLongitude(commodityJson);
            double endLatitude = Commodity.getEndLatitude(commodityJson);
            double endLongitude = Commodity.getEndLongitude(commodityJson);
            CommodityStatus status = Commodity.getStatus(commodityJson);
            JsonObject metadata = Commodity.getMetadata(commodityJson);
            commodity = new Commodity(path,
                    startLatitude,
                    startLongitude,
                    endLatitude,
                    endLongitude,
                    status,
                    metadata,
                    services);
        } else {
            commodity.setCommodityFields(commodityJson);
        }

        return commodity;
    }

    private static void checkCommodityField(JsonObject commodityJson, String field) {
        if(!commodityJson.has(field)) {
            throw new ClassCastException("No " + field + " was found in the JSON");
        }
    }

    private static void checkCommodityFields(JsonObject commodityJson) {
        Commodity.checkCommodityField(commodityJson, "path");
        Commodity.checkCommodityField(commodityJson, "startLatitude");
        Commodity.checkCommodityField(commodityJson, "startLongitude");
        Commodity.checkCommodityField(commodityJson, "endLatitude");
        Commodity.checkCommodityField(commodityJson, "endLongitude");
        Commodity.checkCommodityField(commodityJson, "status");
        Commodity.checkCommodityField(commodityJson, "metadata");

        if(!commodityJson.get("metadata").isJsonObject()) {
            throw new ClassCastException("Metadata was not a JSON object");
        }
    }

    private void setCommodityFields(JsonObject commodityJson) {
        this.setStartLatitude(Commodity.getStartLatitude(commodityJson));
        this.setStartLongitude(Commodity.getStartLongitude(commodityJson));
        this.setEndLatitude(Commodity.getEndLatitude(commodityJson));
        this.setEndLongitude(Commodity.getEndLongitude(commodityJson));
        this.setStatus(Commodity.getStatus(commodityJson));
        this.setMetadata(Commodity.getMetadata(commodityJson));
    }

    private static String getPath(JsonObject commodityJson) {
        return commodityJson.get("path").getAsString();
    }

    private static double getStartLatitude(JsonObject commodityJson) {
        return commodityJson.get("startLatitude").getAsDouble();
    }

    private static double getStartLongitude(JsonObject commodityJson) {
        return commodityJson.get("startLongitude").getAsDouble();
    }

    private static double getEndLatitude(JsonObject commodityJson) {
        return commodityJson.get("endLatitude").getAsDouble();
    }

    private static double getEndLongitude(JsonObject commodityJson) {
        return commodityJson.get("endLongitude").getAsDouble();
    }

    private static CommodityStatus getStatus(JsonObject commodityJson) {
        return Commodity.getStatus(commodityJson.get("status").getAsString());
    }

    private static JsonObject getMetadata(JsonObject commodityJson) {
        return commodityJson.get("metadata").getAsJsonObject();
    }

    /**
     * Updates the start location of this commodity to specified coordinates.
     * This method updates the location of the commodity on the pathfinder server.
     * The latitude and longitude are not updated in this object by this method.
     * @param startLatitude The latitude to change the start location to.
     * @param startLongitude The longitude to change the start location to.
     */
    public void updateStartLocation(double startLatitude, double startLongitude) {
        this.update(startLatitude, startLongitude, null, null, null, null);
    }

    /**
     * Returns the current start latitude of the commodity.
     * @return The current start latitude of the commodity.
     */
    public double getStartLatitude() {
        return this.startLatitude;
    }

    private void setStartLatitude(double latitude) {
        this.startLatitude = latitude;
    }

    /**
     * Updates the start latitude of this commodity to the specified latitude.
     * This method updates the start latitude of the commodity on the pathfinder server.
     * The start latitude is not updated in this object by this method.
     * @param startLatitude The start latitude to change to.
     */
    public void updateStartLatitude(double startLatitude) {
        this.update(startLatitude, null, null, null, null, null);
    }

    /**
     * Returns the current start longitude of the commodity.
     * @return The current start longitude of the commodity.
     */
    public double getStartLongitude() {
        return this.startLongitude;
    }

    private void setStartLongitude(double longitude) {
        this.startLongitude = longitude;
    }

    /**
     * Updates the start longitude of this commodity to the specified longitude.
     * This method updates the start longitude of the commodity on the pathfinder server.
     * The start longitude is not updated in this object by this method.
     * @param startLongitude The start longitude to change to.
     */
    public void updateStartLongitude(double startLongitude) {
        this.update(null, startLongitude, null, null, null, null);
    }

    /**
     * Updates the end location of this commodity to specified coordinates.
     * This method updates the location of the commodity on the pathfinder server.
     * The latitude and longitude are not updated in this object by this method.
     * @param endLatitude The latitude to change the end location to.
     * @param endLongitude The longitude to change the end location to.
     */
    public void updateEndLocation(double endLatitude, double endLongitude) {
        this.update(null, null, endLatitude, endLongitude, null, null);
    }

    /**
     * Returns the current end latitude of the commodity.
     * @return The current end latitude of the commodity.
     */
    public double getEndLatitude() {
        return this.endLatitude;
    }

    private void setEndLatitude(double latitude) {
        this.endLatitude = latitude;
    }

    /**
     * Updates the end latitude of this commodity to the specified latitude.
     * This method updates the end latitude of the commodity on the pathfinder server.
     * The end latitude is not updated in this object by this method.
     * @param endLatitude The end latitude to change to.
     */
    public void updateEndLatitude(double endLatitude) {
        this.update(null, null, endLatitude, null, null, null);
    }

    /**
     * Returns the current end longitude of the commodity.
     * @return The current end longitude of the commodity.
     */
    public double getEndLongitude() {
        return this.endLongitude;
    }

    private void setEndLongitude(double longitude) {
        this.endLongitude = longitude;
    }

    /**
     * Updates the end longitude of this commodity to the specified longitude.
     * This method updates the end longitude of the commodity on the pathfinder server.
     * The end longitude is not updated in this object by this method.
     * @param endLongitude The end longitude to change to.
     */
    public void updateEndLongitude(double endLongitude) {
        this.update(null, null, null, endLongitude, null, null);
    }

    /**
     * Returns the current status of the commodity. See {@link CommodityStatus}
     * for the status's available to commodities.
     * @return The current status of the commodity.
     */
    public CommodityStatus getStatus() {
        return this.status;
    }

    private static CommodityStatus getStatus(String status) {
        CommodityStatus[] values = CommodityStatus.values();
        for (int k = 0; k < values.length; k++) {
            if (values[k].equals(status)) {
                return values[k];
            }
        }

        return null;
    }

    private void setStatus(CommodityStatus status) {
        this.status = status;
    }

    private void setStatus(String status) {
        this.setStatus(Commodity.getStatus(status));
    }

    /**
     * Updates the status of this commodity to the specified status.
     * This method updates the status of the commodity on the pathfinder server.
     * The status is not updated in this object by this method.
     * @param status The status to change to.
     */
    public void updateStatus(CommodityStatus status) {
        this.update(null, null, null, null, status, null);
    }

    /**
     * Returns the metadata of the commodity in the form of a Json Object.
     * @return The metadata of the commodity.
     */
    public JsonObject getMetadata() {
        return this.metadata;
    }

    private void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    /**
     * Updates the metadata of this commodity to the specified Json Object.
     * This method updates the metadata of the commodity on the pathfinder server.
     * The metadata is not updated in this object by this method.
     * @param metadata The metadata to change to.
     */
    public void updateMetadata(JsonObject metadata) {
        this.update(null, null, null, null, null, metadata);
    }

    /**
     * Sends update requests to the Pathfinder server. If a parameter is null it will
     * not be updated. This method does not update this commodities fields.
     * @param startLatitude The start latitude to change to.
     * @param startLongitude The start longitude to change to.
     * @param endLatitude The end latitude to change to.
     * @param endLongitude The end longitude to change to.
     * @param status The status to change to.
     * @param metadata The metadata to change to.
     */
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
    protected String getModel() {
        return Commodity.MODEL;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("path", this.getPath());
        json.addProperty("startLatitude", this.getStartLatitude());
        json.addProperty("startLongitude", this.getStartLongitude());
        json.addProperty("endLatitude", this.getEndLatitude());
        json.addProperty("endLongitude", this.getEndLongitude());
        json.addProperty("status", this.getStatus().toString());
        json.add("metadata", this.getMetadata());

        return json;
    }

    @Override
    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }

}
