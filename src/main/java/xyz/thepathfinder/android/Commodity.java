package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

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
            this.metadata = new JsonObject();
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

    public void updateStartLocation(double startLatitude, double startLongitude) {
        this.update(startLatitude, startLongitude, null, null, null, null);
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

    public Double getStartLongitude() {
        return this.startLongitude;
    }

    private void setStartLongitude(double longitude) {
        this.startLongitude = longitude;
    }

    public void updateStartLongitude(double startLongitude) {
        this.update(null, startLongitude, null, null, null, null);
    }

    public void updateEndLocation(double endLatitude, double endLongitude) {
        this.update(null, null, endLatitude, endLongitude, null, null);
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

    public Double getEndLongitude() {
        return this.endLongitude;
    }

    private void setEndLongitude(double longitude) {
        this.endLongitude = longitude;
    }

    public void updateEndLongitude(double endLongitude) {
        this.update(null, null, null, endLongitude, null, null);
    }

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

    public void subscribe() {
        JsonObject model = new JsonObject();
        model.addProperty("model", this.getModel());
        model.addProperty("path", this.getPath());

        super.subscribe(model);
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
