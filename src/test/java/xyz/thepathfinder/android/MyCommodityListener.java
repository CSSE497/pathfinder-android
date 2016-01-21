package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyCommodityListener implements CommodityListener {

    private static final Logger logger = Logger.getLogger(MyCommodityListener.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }
    private static final String TAG = "MyCommodityListener";
    private Commodity commodity;
    //private Marker startMarker;
    //private Marker endMarker;

    public MyCommodityListener(Commodity commodity) {
        this.commodity = commodity;
    }

    @Override
    public void routed(Route route) {

    }

    @Override
    public void startLatitudeUpdated(double latitude) {

    }

    @Override
    public void startLongitudeUpdated(double longitude) {

    }

    @Override
    public void endLatitudeUpdated(double latitude) {

    }

    @Override
    public void endLongitudeUpdated(double longitude) {

    }

    @Override
    public void statusUpdated(CommodityStatus status) {

    }

    @Override
    public void metadataUpdated(JsonObject metadata) {

    }

    @Override
    public void connected(Commodity model) {
        logger.info("Connected commodity: " + model.toString());
        //LatLng startPosition = new LatLng(model.getStartLatitude(), model.getStartLongitude());
        //this.startMarker = this.map.addMarker(new MarkerOptions().position(startPosition).title("Commodity start"));

        //LatLng endPosition = new LatLng(model.getEndLatitude(), model.getEndLongitude());
        //this.endMarker = this.map.addMarker(new MarkerOptions().position(endPosition).title("Commodity end"));
    }

    @Override
    public void created(Commodity model) {
        logger.info("Created commodity: " + model.toString());
        //LatLng startPosition = new LatLng(model.getStartLatitude(), model.getStartLongitude());
        //this.startMarker = this.map.addMarker(new MarkerOptions().position(startPosition).title("Commodity start"));

        //LatLng endPosition = new LatLng(model.getEndLatitude(), model.getEndLongitude());
        //this.endMarker = this.map.addMarker(new MarkerOptions().position(endPosition).title("Commodity end"));
    }

    @Override
    public void deleted(Commodity model) {

    }

    @Override
    public void error(String error) {

    }

    @Override
    public void updated(Commodity model) {
        logger.info("Updated: " + model);
        //LatLng startPosition = new LatLng(model.getStartLatitude(), model.getStartLongitude());
        //this.startMarker.setPosition(startPosition);

        //LatLng endPosition = new LatLng(model.getEndLatitude(), model.getEndLongitude());
        //this.endMarker.setPosition(endPosition);
    }

    @Override
    public void routeSubscribed(Commodity model) {

    }

    @Override
    public void routeUnsubscribed(Commodity model) {

    }

    @Override
    public void subscribed(Commodity model) {

    }

    @Override
    public void unsubscribed(Commodity model) {

    }

    public void addMarker(Commodity model) {
        logger.info("Add Marker: " + model.getPath() + " lat: " + model.getStartLatitude() + " long: " + model.getStartLongitude());
        //LatLng startPosition = new LatLng(model.getStartLatitude(), model.getStartLongitude());
        //this.startMarker = this.map.addMarker(new MarkerOptions().position(startPosition).title("Commodity start"));

        //LatLng endPosition = new LatLng(model.getEndLatitude(), model.getEndLongitude());
        //this.endMarker = this.map.addMarker(new MarkerOptions().position(endPosition).title("Commodity end"));
    }
}
