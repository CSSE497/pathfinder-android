package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;


public class MyTransportListener implements TransportListener{

    private static final Logger logger = Logger.getLogger(MyTransportListener.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }
    private static final String TAG = "MyTransportListener";
    private Transport transport;


    public MyTransportListener(Transport transport) {
        this.transport = transport;
    }

    @Override
    public void routed(Route route) {

    }

    @Override
    public void latitudeUpdated(double latitude) {

    }

    @Override
    public void longitudeUpdated(double longitude) {

    }

    @Override
    public void statusUpdated(TransportStatus status) {

    }

    @Override
    public void metadataUpdated(JsonObject metadata) {

    }

    @Override
    public void connected(Transport model) {
        logger.info("Connected Transport: " + model.toString());
        //LatLng position = new LatLng(model.getLatitude(), model.getLongitude());
        //this.marker = this.map.addMarker(new MarkerOptions().position(position).title("Transport 1"));
    }

    @Override
    public void created(Transport model) {
        logger.info("Created transport: " + model.toString());
        //LatLng position = new LatLng(model.getLatitude(), model.getLongitude());
        //this.marker = this.map.addMarker(new MarkerOptions().position(position).title("Transport 1"));
    }

    @Override
    public void deleted(Transport model) {

    }

    @Override
    public void error(String error) {

    }

    @Override
    public void updated(Transport model) {
        //LatLng position = new LatLng(model.getLatitude(), model.getLongitude());
        //this.marker.setPosition(position);
    }

    @Override
    public void routeSubscribed(Transport model) {

    }

    @Override
    public void routeUnsubscribed(Transport model) {

    }

    @Override
    public void subscribed(Transport model) {

    }

    @Override
    public void unsubscribed(Transport model) {

    }

    public void addMarker(Transport model) {
        logger.info("Add Marker: " + model.getPath() + " lat: " + model.getLatitude() + " long: " + model.getLongitude());
        //LatLng position = new LatLng(model.getLatitude(), model.getLongitude());
        //this.marker = this.map.addMarker(new MarkerOptions().position(position).title("Transport 1"));
    }
}
