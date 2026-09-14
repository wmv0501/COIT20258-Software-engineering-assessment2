package drs.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single disaster report submitted by a user.
 * Contains all details about the disaster, the reporter, and the current
 * assessment status. Priority is computed automatically from type and severity.
 */
public class DisasterReport {

    // Formatter for consistent timestamp display throughout the application
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Priority weight contributed by disaster type
    private static final int WEIGHT_HURRICANE  = 10;
    private static final int WEIGHT_EARTHQUAKE = 9;
    private static final int WEIGHT_FIRE       = 8;
    private static final int WEIGHT_FLOOD      = 7;
    private static final int WEIGHT_OTHER      = 5;

    private int id;
    private DisasterType type;
    private Severity severity;
    private String location;
    private String description;
    private String reporterName;
    private String reporterPhone;
    private DisasterStatus status;
    private int priority;           // computed from type + severity
    private String timestamp;       // formatted submission time

    /**
     * No-args constructor. Sets sensible defaults.
     * Required for frameworks that instantiate via reflection.
     */
    public DisasterReport() {
        this.status    = DisasterStatus.REPORTED;
        this.timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }

    /**
     * Full constructor that computes priority automatically.
     *
     * @param id            the unique identifier
     * @param type          the type of disaster
     * @param severity      the severity level
     * @param location      the location description
     * @param description   the detailed description
     * @param reporterName  the name of the person reporting
     * @param reporterPhone the phone number of the reporter
     */
    public DisasterReport(int id, DisasterType type, Severity severity,
                          String location, String description,
                          String reporterName, String reporterPhone) {
        this.id           = id;
        this.type         = type;
        this.severity     = severity;
        this.location     = location;
        this.description  = description;
        this.reporterName  = reporterName;
        this.reporterPhone = reporterPhone;
        this.status       = DisasterStatus.REPORTED;
        this.timestamp    = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        this.priority     = computePriority(type, severity);
    }

    /**
     * Computes the priority score from a disaster type and severity.
     * Higher score = higher urgency. Range: 15 (Other + Low) to 50 (Hurricane + Critical).
     *
     * @param type     the disaster type
     * @param severity the severity level
     * @return the computed priority score
     */
    public static int computePriority(DisasterType type, Severity severity) {
        int typeWeight = switch (type) {
            case HURRICANE  -> WEIGHT_HURRICANE;
            case EARTHQUAKE -> WEIGHT_EARTHQUAKE;
            case FIRE       -> WEIGHT_FIRE;
            case FLOOD      -> WEIGHT_FLOOD;
            case OTHER      -> WEIGHT_OTHER;
        };
        return typeWeight + severity.getPriorityWeight();
    }

    // --- Accessors ---

    /** Returns the unique identifier for this report. */
    public int getId() { return id; }

    /** Returns the disaster type. */
    public DisasterType getType() { return type; }

    /** Returns the severity level. */
    public Severity getSeverity() { return severity; }

    /** Returns the location of the disaster. */
    public String getLocation() { return location; }

    /** Returns the detailed description of the disaster. */
    public String getDescription() { return description; }

    /** Returns the name of the reporter. */
    public String getReporterName() { return reporterName; }

    /** Returns the phone number of the reporter. */
    public String getReporterPhone() { return reporterPhone; }

    /** Returns the current status of this report. */
    public DisasterStatus getStatus() { return status; }

    /** Returns the computed priority score. */
    public int getPriority() { return priority; }

    /** Returns the formatted submission timestamp. */
    public String getTimestamp() { return timestamp; }

    // --- Mutators ---

    /** Sets the unique identifier. */
    public void setId(int id) { this.id = id; }

    /** Sets the disaster type and recomputes priority. */
    public void setType(DisasterType type) {
        this.type = type;
        this.priority = computePriority(this.type, this.severity);
    }

    /** Sets the severity and recomputes priority. */
    public void setSeverity(Severity severity) {
        this.severity = severity;
        this.priority = computePriority(this.type, this.severity);
    }

    /** Sets the location of the disaster. */
    public void setLocation(String location) { this.location = location; }

    /** Sets the detailed description. */
    public void setDescription(String description) { this.description = description; }

    /** Sets the reporter's name. */
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    /** Sets the reporter's phone number. */
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    /** Sets the current status of this report. */
    public void setStatus(DisasterStatus status) { this.status = status; }

    /**
     * Overrides the automatically computed priority.
     * Allows coordinators to manually adjust urgency.
     *
     * @param priority the new priority value
     */
    public void setPriority(int priority) { this.priority = priority; }

    /** Sets the submission timestamp (used when restoring persisted data). */
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    /**
     * Returns a human-readable summary of this disaster report.
     *
     * @return formatted string with all key fields
     */
    @Override
    public String toString() {
        return String.format(
            "DisasterReport[id=%d, type=%s, severity=%s, location='%s', status=%s, priority=%d, reporter='%s']",
            id, type, severity, location, status, priority, reporterName);
    }
}
