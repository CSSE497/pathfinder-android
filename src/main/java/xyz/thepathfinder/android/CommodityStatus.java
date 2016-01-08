package xyz.thepathfinder.android;

/**
 * An enum for the possible statuses of a {@link Commodity}.
 *
 * @author David Robinson
 */
public enum CommodityStatus {
    /**
     * The commodity is currently not being routed.
     */
    INACTIVE("Inactive"),

    /**
     * The commodity is waiting to be picked up.
     */
    WAITING("Waiting"),

    /**
     * The commodity was picked up by a transport.
     */
    PICKED_UP("PickedUp"),

    /**
     * The commodity was dropped off by a transport.
     */
    DROPPED_OFF("DroppedOff"),

    /**
     * The commodity was cancelled to be picked up.
     */
    CANCELLED("Cancelled");

    /**
     * The string representation of the status.
     */
    private final String status;

    /**
     * A constructor to make each possible status.
     *
     * @param status the string associated with the status.
     */
    private CommodityStatus(String status) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.status;
    }
}
