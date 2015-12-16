package xyz.thepathfinder.android;

public enum ActionStatus {
    START("start"),
    PICK_UP("pickup"),
    DROP_OFF("dropoff");

    private final String status;

    private ActionStatus(String status) {
        this.status = status;
    }

    public boolean equals(String status) {
        return this.status.equals(status);
    }

    @Override
    public String toString() {
        return this.status;
    }
}