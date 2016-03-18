package xyz.thepathfinder.android;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum for the possible statuses of an {@link Action}.
 *
 * @author David Robinson
 */
public enum ActionStatus {

    /**
     * The start of a transports route.
     */
    START("Start"),

    /**
     * A commodity pickup will occur at this location.
     */
    PICK_UP("PickUp"),

    /**
     * A commodity drop off will occur at this location.
     */
    DROP_OFF("DropOff");

    private static final Map<String, ActionStatus> statuses;

    static {
        statuses = new HashMap<String, ActionStatus>();
        for(ActionStatus status: ActionStatus.values()) {
            ActionStatus.statuses.put(status.toString(), status);
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
    private ActionStatus(String status) {
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
    @Override
    public String toString() {
        return this.status;
    }

    public static ActionStatus getStatus(String status) {
        return ActionStatus.statuses.get(status);
    }
}