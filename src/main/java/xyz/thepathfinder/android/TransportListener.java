package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * The <tt>TransportListener</tt> interface gives developers access to notifications
 * when a transport is modified. The notifications also include route notifications.
 *
 * @author David Robinson
 */
public abstract class TransportListener extends Listener<Transport> {

    /**
     * Invoked when the transport receives a routed message.
     *
     * @param route the transport's new route.
     */
    public void routed(Route route) {}

    /**
     * Invoked when the transport's location was updated by a message.
     *
     * @param latitude that the transport updated to.
     * @param longitude that the transport updated to.
     */
    public void locationUpdated(double latitude, double longitude) {}

    /**
     * Invoked when the transport's status was updated by a message.
     *
     * @param status that the transport updated to.
     */
    public void statusUpdated(TransportStatus status) {}

    /**
     * Invoked when the transport's metadata was updated by a message.
     *
     * @param metadata that the transport updated to.
     */
    public void metadataUpdated(JsonObject metadata) {}

    /**
     * Invoked when the transport's commodities was updated by a message.
     *
     * @param commodities that the transport is carrying.
     */
    public void commoditiesUpdated(List<Commodity> commodities) {}
}
