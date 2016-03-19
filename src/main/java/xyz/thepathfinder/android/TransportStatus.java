package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum for the possible statuses of a {@link Transport}.
 *
 * @author David Robinson
 * @see Transport
 */
public enum TransportStatus {

    /**
     * The transport is currently inactive and not accepting routes.
     */
    OFFLINE("Offline"),

    /**
     * The transport is currently active and accepting routes.
     */
    ONLINE("Online");

    /**
     * Map of the possible statuses. It maps String of status -> TransportStatus.
     */
    private static final Map<String, TransportStatus> statuses;

    // Creates the status map.
    static {
        statuses = new HashMap<String, TransportStatus>();
        for (TransportStatus status : TransportStatus.values()) {
            TransportStatus.statuses.put(status.toString(), status);
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
    private TransportStatus(String status) {
        this.status = status;
    }

    /**
     * Changes a String to an TransportStatus. If the status cannot be converted <tt>null</tt> is returned.
     *
     * @param status represented as a String.
     * @return TransportStatus if status could be converted, <tt>null</tt> otherwise.
     */
    public static TransportStatus getStatus(String status) {
        return TransportStatus.statuses.get(status);
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
