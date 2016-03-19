package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum for the possible statuses of a {@link Commodity}.
 *
 * @author David Robinson
 * @see Commodity
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
     * Map of the possible statuses. It maps String of status to CommodityStatus.
     */
    private static final Map<String, CommodityStatus> statuses;

    // Creates the status map.
    static {
        statuses = new HashMap<String, CommodityStatus>();
        for (CommodityStatus status : CommodityStatus.values()) {
            CommodityStatus.statuses.put(status.toString(), status);
        }
    }

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
     * Changes a String to an CommodityStatus. If the status cannot be converted <tt>null</tt> is returned.
     *
     * @param status represented as a String.
     * @return CommodityStatus if status could be converted, <tt>null</tt> otherwise.
     */
    public static CommodityStatus getStatus(String status) {
        return CommodityStatus.statuses.get(status);
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
    @Override
    public String toString() {
        return this.status;
    }
}
