package drs.model;

/**
 * Enum representing the types of disasters the DRS can handle.
 * Each constant maps to a user-friendly display label.
 */
public enum DisasterType {

    HURRICANE("Hurricane"),
    EARTHQUAKE("Earthquake"),
    FIRE("Fire"),
    FLOOD("Flood"),
    OTHER("Other");

    // Human-readable label shown in the UI
    private final String label;

    /**
     * Constructs a DisasterType with the given display label.
     *
     * @param label the UI-friendly name
     */
    DisasterType(String label) {
        this.label = label;
    }

    /**
     * Returns the display label for this disaster type.
     *
     * @return the label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the display label, used by ComboBox rendering.
     *
     * @return the label string
     */
    @Override
    public String toString() {
        return label;
    }
}
