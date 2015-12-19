package xyz.thepathfinder.android;

/**
 * An enum for the possible status of an {@link Action}.
 */
public enum ActionStatus {
    /**
     * The start of a transports route.
     */
    START("start"),

    /**
     * A commodity pickup will occur at this location.
     */
    PICK_UP("pickup"),

    /**
     * A commodity drop off will occur at this location.
     */
    DROP_OFF("dropoff");

    /**
     * The string representation of the status.
     */
    private final String status;

    /**
     * A constructor to make each possible status.
     * @param status the string associated with the status.
     */
    private ActionStatus(String status) {
        this.status = status;
    }

    /**
     * Checks if this status is the same as the provided status.
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