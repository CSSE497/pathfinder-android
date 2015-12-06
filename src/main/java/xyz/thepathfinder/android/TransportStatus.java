package xyz.thepathfinder.android;

public enum TransportStatus {
    OFFLINE("Offline"),
    ONLINE("Online");

    private final String status;

    private TransportStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
