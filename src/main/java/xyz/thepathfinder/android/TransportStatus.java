package xyz.thepathfinder.android;

public enum TransportStatus {
    OFFLINE("Offline"),
    ONLINE("Online");

    private final String status;

    private TransportStatus(String status) {
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
