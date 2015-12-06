package xyz.thepathfinder.android;

public enum SubscribableClusterModel {
    COMMODITY("Commodity"),
    TRANSPORT("Transport");

    private final String model;

    private SubscribableClusterModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return this.model;
    }
}
