package xyz.thepathfinder.android;

/**
 * An enum for the possible status of an {@link Action}.
 */
public enum ActionStatus {
    START("start"),
    PICK_UP("pickup"),
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