package drs.model;

/**
 * Enum representing the severity levels of a disaster report.
 * Severity contributes to the automatically computed priority score.
 */
public enum Severity {

    LOW("Low", 10),
    MEDIUM("Medium", 20),
    HIGH("High", 30),
    CRITICAL("Critical", 40);

    // Human-readable label shown in the UI
    private final String label;

    // Points contributed to the priority computation
    private final int priorityWeight;

    /**
     * Constructs a Severity level with a label and priority weight.
     *
     * @param label          the UI-friendly name
     * @param priorityWeight points added to the priority score
     */
    Severity(String label, int priorityWeight) {
        this.label = label;
        this.priorityWeight = priorityWeight;
    }

    /**
     * Returns the display label for this severity level.
     *
     * @return the label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the weight this severity adds to the priority score.
     *
     * @return the priority weight
     */
    public int getPriorityWeight() {
        return priorityWeight;
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
