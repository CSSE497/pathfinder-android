package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

/**
 * The <tt>CommodityListener</tt> interface gives developers access to notifications
 * when a commodity is modified. The notifications also include route notifications.
 *
 * @author David Robinson
 */
public abstract class CommodityListener extends Listener<Commodity> {

    /**
     * Invoked when the commodity receives a routed message.
     *
     * @param route the commodity's new route.
     */
    public void routed(Route route) {
    }

    /**
     * Invoked when the commodity's start location was updated by a message.
     *
     * @param latitude  that the commodity updated to.
     * @param longitude that the commodity updated to.
     */
    public void startLocationUpdated(double latitude, double longitude) {
    }

    /**
     * Invoked when the commodity's end location was updated by a message.
     *
     * @param latitude  that the commodity updated to.
     * @param longitude that the commodity updated to.
     */
    public void endLocationUpdated(double latitude, double longitude) {
    }

    /**
     * Invoked when the commodity's status was updated by a message.
     *
     * @param status that the commodity updated to.
     */
    public void statusUpdated(CommodityStatus status) {
    }

    /**
     * Invoked when the commodity's metadata was updated by a message.
     *
     * @param metadata that the commodity updated to.
     */
    public void metadataUpdated(JsonObject metadata) {
    }

    /**
     * Invoked when the commodity's transport is changed.
     *
     * @param transport carrying the commodity.
     */
    public void transportUpdated(Transport transport) {
    }
}
