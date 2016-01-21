package xyz.thepathfinder.android;


import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MyClusterListener implements ClusterListener{

    private static final Logger logger = Logger.getLogger(MyClusterListener.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }
    private static final String TAG = "MyClusterListener";
    private Cluster cluster;

    public MyClusterListener(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void routed(List<Route> routes) {
        logger.info("Cluster routed: " + routes.toString());
    }

    @Override
    public void commodityAdded(Commodity commodity) {
        logger.info("Commodity Added: " + commodity.toString());
        MyCommodityListener listener = new MyCommodityListener(commodity);
        commodity.addListener(listener);
        listener.addMarker(commodity);
    }

    @Override
    public void commodityRemoved(Commodity commodity) {

    }

    @Override
    public void commodityUpdated(Commodity commodity) {

    }

    @Override
    public void commoditiesUpdated(Collection<Commodity> commodities) {

    }

    @Override
    public void subclusterAdded(Cluster cluster) {

    }

    @Override
    public void subclusterRemoved(Cluster cluster) {

    }

    @Override
    public void subclusterUpdated(Cluster cluster) {

    }

    @Override
    public void subclustersUpdated(Collection<Cluster> clusters) {

    }

    @Override
    public void transportAdded(Transport transport) {
        logger.info("Transport Added: " + transport.toString());
        MyTransportListener listener = new MyTransportListener(transport);
        transport.addListener(listener);
        listener.addMarker(transport);
    }

    @Override
    public void transportRemoved(Transport transport) {

    }

    @Override
    public void transportUpdated(Transport transport) {

    }

    @Override
    public void transportsUpdated(Collection<Transport> transports) {

    }

    @Override
    public void connected(Cluster model) {
        logger.info("Read: " + model);
        model.routeSubscribe();
        model.createTransport("Transport1", 39.480957, -87.323207, TransportStatus.ONLINE, new JsonObject()).create();
        model.createCommodity("Commodity1", 39.472386, -87.369459, 39.462877, -87.373676, CommodityStatus.WAITING, new JsonObject()).create();
        model.createCommodity("Commodity2", 39.465752, -87.360129, 39.467546, -87.394185, CommodityStatus.WAITING, new JsonObject()).create();
    }

    @Override
    public void created(Cluster model) {

    }

    @Override
    public void deleted(Cluster model) {

    }

    @Override
    public void error(String error) {

    }

    @Override
    public void updated(Cluster model) {
        logger.info("Updated: " + model.toString());
    }

    @Override
    public void routeSubscribed(Cluster model) {
        logger.info("Route subscribed: " + model.toString());
    }

    @Override
    public void routeUnsubscribed(Cluster model) {

    }

    @Override
    public void subscribed(Cluster model) {

    }

    @Override
    public void unsubscribed(Cluster model) {

    }
}
