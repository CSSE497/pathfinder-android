package xyz.thepathfinder.android;

/**
 * An enum for the possible status of a {@link Commodity}.
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
     * Checks if this status is the same as the provided status.
     *
     * @param status a string of status
     * @return <tt>true</tt> if the status are the same, <tt>false</tt> otherwise.
     */
    public boolean equals(String status) {
        return this.status.equals(status);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.status;
    }
}
