package drs.model;

/**
 * Enum representing the lifecycle status of a disaster report.
 * A report progresses from REPORTED → ASSESSING → RESPONDING → RESOLVED.
 */
public enum DisasterStatus {

    REPORTED( "Reported" ),
    ASSESSING( "Assessing" ),
    RESPONDING( "Responding" ),
    RESOLVED( "Resolved" );

    // Human-readable label shown in the UI
    private final String label;

    /**
     * Constructs a DisasterStatus with the given display label.
     *
     * @param label the UI-friendly name
     */
    DisasterStatus( String label ) {
        this.label = label;
    }

    /**
     * Returns the display label for this status.
     *
     * @return the label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the display label, used by ComboBox and TableView rendering.
     *
     * @return the label string
     */
    @Override
    public String toString() {
        return label;
    }
}
