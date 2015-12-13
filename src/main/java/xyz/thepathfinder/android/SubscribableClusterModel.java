package xyz.thepathfinder.android;

public enum SubscribableClusterModel {
    COMMODITY(Pathfinder.COMMODITY),
    TRANSPORT(Pathfinder.TRANSPORT);

    private final String model;

    private SubscribableClusterModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return this.model;
    }
}
