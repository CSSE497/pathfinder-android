package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

/**
 * The <tt>TransportListener</tt> interface gives developers access to notifications
 * when a transport is modified. The notifications also include route notifications.
 *
 * @author David Robinson
 */
public class TransportListener extends Listener<Transport> {

    /**
     * Invoked when the transport receives a routed message.
     *
     * @param route the transport's new route.
     */
    public void routed(Route route) {}

    /**
     * Invoked when the transport's latitude was updated by a message.
     *
     * @param latitude that the transport updated to.
     */
    public void latitudeUpdated(double latitude) {}

    /**
     * Invoked when the transport's longitude was updated by a message.
     *
     * @param longitude that the transport updated to.
     */
    public void longitudeUpdated(double longitude) {}

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
}
