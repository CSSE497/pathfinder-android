package xyz.thepathfinder.android;

public enum CommodityStatus {
    INACTIVE("Inactive"),
    WAITING("Waiting"),
    PICKED_UP("PickedUp"),
    DROPPED_OFF("DroppedOff"),
    CANCELLED("Cancelled");

    private final String status;

    private CommodityStatus(String status) {
        this.status = status;
    }

    public boolean equals(String status) {
        return this.status.equals(status);
    }

    public String toString() {
        return this.status;
    }
}
