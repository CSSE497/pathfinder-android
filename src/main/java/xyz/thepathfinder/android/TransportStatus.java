package xyz.thepathfinder.android;

/**
 * An enum for the possible statuses of a {@link Transport}.
 *
 * @author David Robinson
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
     * Checks if this status is the same as the provided status.
     *
     * @param status a string of status
     * @return <tt>true</tt> if the status are the same, <tt>false</tt> otherwise.
     */
    public boolean equals(String status) {
        return this.status.equals(status);
    }

    /**
     * Checks if this status is the same as the provided status.
     *
     * @param status of the transport
     * @return <tt>true</tt> if the status are the same, <tt>false</tt> otherwise.
     */
    public boolean equals(TransportStatus status) {
        return this.status.equals(status.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.status;
    }
}
