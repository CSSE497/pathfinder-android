package xyz.thepathfinder.android;

/**
 * An enum for the possible types of {@link Model}s.
 *
 * @author David Robinson
 * @see Cluster
 * @see Commodity
 * @see Transport
 */
public enum ModelType {

    /**
     * Cluster type.
     */
    CLUSTER("Cluster"),

    /**
     * Commodity type.
     */
    COMMODITY("Commodity"),

    /**
     * Transport type.
     */
    TRANSPORT("Vehicle");

    /**
     * The string representation of the type.
     */
    private final String type;

    /**
     * A constructor to make each possible type.
     *
     * @param type the string associated with the model type.
     */
    private ModelType(String type) {
        this.type = type;
    }

    /**
     * Returns the enum version of a model type from a string.
     *
     * @param type the model as a string.
     * @return the model type as an enum.
     */
    protected static ModelType getModelType(String type) {
        ModelType[] values = ModelType.values();
        for (int k = 0; k < values.length; k++) {
            if (values[k].equals(type)) {
                return values[k];
            }
        }

        return null;
    }

    /**
     * Checks if this type is the same as the provided type.
     *
     * @param type a string of type.
     * @return <tt>true</tt> if the types are the same, <tt>false</tt> otherwise.
     */
    public boolean equals(String type) {
        return this.type.equals(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.type;
    }
}
