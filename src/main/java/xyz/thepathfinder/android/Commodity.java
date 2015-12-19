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

    /**
     * String used in the model field of the pathfinder requests.
     */
    private static final String MODEL = Pathfinder.COMMODITY;

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
     * The route of this commodity.
     */
    private Route route;

    /**
     * Constructs a commodity object with the path specified. When creating the commodity
     * it uses default values, so that requests are fully qualified. The default values
     * are zero for all the locations, the status is inactive, and the metadata is an
     * empty JSON object.
     * @param path the path to commodity
     * @param services a pathfinder services objects
     */
    protected Commodity(String path, PathfinderServices services) {
        super(path, services);

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(path);
        if(isRegistered) {
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

        this.route = null;
    }

    /**
     * Constructs a commodity object with the specified parameters.
     * @param path the path on the Pathfinder server.
     * @param startLatitude the pickup latitude of the commodity.
     * @param startLongitude the pickup longitude of the commodity.
     * @param endLatitude the drop off latitude of the commodity.
     * @param endLongitude the drop off longitude of the commodity.
     * @param status the current status of the commodity.
     * @param metadata a JSON object that holds metadata for the commodity.
     * @param services a pathfinder services object.
     */
    protected Commodity(String path, double startLatitude, double startLongitude, double endLatitude, double endLongitude, CommodityStatus status, JsonObject metadata, PathfinderServices services) {
        this(path, services);

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

        this.route = null;
    }

    /**
     * Returns a commodity that has been registered with the {@link ModelRegistry} or a new
     * commodity if one hasn't been created with that path.
     * @param path the path to model on the pathfinder server
     * @param services a pathfinder services object.
     * @return the commodity object created with the path specified.
     * @throws ClassCastException if the path requested is already used by a different model type.
     */
    protected static Commodity getInstance(String path, PathfinderServices services) {
        Commodity commodity = (Commodity) services.getRegistry().getModel(path, Commodity.MODEL);

        if(commodity == null) {
            return new Commodity(path, services);
        }

        return commodity;
    }

    /**
     * Returns a commodity that has been registered with the {@link ModelRegistry} or a new
     * commodity if one hasn't been created with that path. The commodity will be updated to
     * reflect the values in JSON object provided.
     * @param commodityJson a JSON object that represents a commodity.
     * @param services a pathfinder services object.
     * @return the commodity object created with the path specified.
     * @throws ClassCastException if the path requested is already used by a different model type.
     */
    protected static Commodity getInstance(JsonObject commodityJson, PathfinderServices services) {
        Commodity.checkCommodityFields(commodityJson);

        String path = Commodity.getPath(commodityJson);
        Commodity commodity = Commodity.getInstance(path, services);

        commodity.setCommodityFields(commodityJson);

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

    public Route getRoute() {
        return this.route;
    }

    private void setRoute(Route route) {
        this.route = route;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModel() {
        return Commodity.MODEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JsonObject createValueJson() {
        JsonObject json = new JsonObject();

        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());
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
    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }

}
