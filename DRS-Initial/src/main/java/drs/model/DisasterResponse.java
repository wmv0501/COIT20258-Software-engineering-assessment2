package drs.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the coordinated response to a specific disaster report.
 * Links a DisasterReport to the departments and responders assigned to it,
 * along with coordinator notes and the time the response was created.
 */
public class DisasterResponse {

    // Formatter for consistent timestamp display
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" );

    private int id;
    private DisasterReport disasterReport;          // the disaster being responded to
    private List<Department> assignedDepartments;   // departments assigned to this response
    private List<Responder> assignedResponders;     // individual responders assigned
    private String notes;                           // coordinator notes
    private String createdAt;                       // when this response was created

    /**
     * No-args constructor. Initialises lists and sets the creation timestamp.
     */
    public DisasterResponse() {
        this.assignedDepartments = new ArrayList<>();
        this.assignedResponders = new ArrayList<>();
        this.createdAt = LocalDateTime.now().format( TIMESTAMP_FORMAT );
    }

    /**
     * Full constructor for creating a complete disaster response record.
     *
     * @param id the unique identifier
     * @param disasterReport the disaster report this response addresses
     * @param assignedDepartments the list of departments assigned
     * @param assignedResponders the list of individual responders assigned
     * @param notes coordinator notes
     */
    public DisasterResponse( int id, DisasterReport disasterReport,
                             List<Department> assignedDepartments,
                             List<Responder> assignedResponders,
                             String notes ) {
        this.id = id;
        this.disasterReport = disasterReport;
        this.assignedDepartments = new ArrayList<>( assignedDepartments );
        this.assignedResponders = new ArrayList<>( assignedResponders );
        this.notes = notes;
        this.createdAt = LocalDateTime.now().format( TIMESTAMP_FORMAT );
    }

    // --- Accessors ---

    /**
     * Returns the unique identifier for this response.
     */
    public int getId() {return id;}

    /**
     * Sets the unique identifier.
     */
    public void setId( int id ) {this.id = id;}

    /**
     * Returns the disaster report this response is for.
     */
    public DisasterReport getDisasterReport() {return disasterReport;}

    /**
     * Sets the disaster report this response addresses.
     */
    public void setDisasterReport( DisasterReport disasterReport ) {
        this.disasterReport = disasterReport;
    }

    /**
     * Returns the list of departments assigned to this response.
     */
    public List<Department> getAssignedDepartments() {return assignedDepartments;}

    /**
     * Replaces the assigned departments list.
     */
    public void setAssignedDepartments( List<Department> assignedDepartments ) {
        this.assignedDepartments = new ArrayList<>( assignedDepartments );
    }

    /**
     * Returns the list of individual responders assigned.
     */
    public List<Responder> getAssignedResponders() {return assignedResponders;}

    /**
     * Replaces the assigned responders list.
     */
    public void setAssignedResponders( List<Responder> assignedResponders ) {
        this.assignedResponders = new ArrayList<>( assignedResponders );
    }

    /**
     * Returns the coordinator notes for this response.
     */
    public String getNotes() {return notes;}

    // --- Mutators ---

    /**
     * Sets the coordinator notes.
     */
    public void setNotes( String notes ) {this.notes = notes;}

    /**
     * Returns the formatted creation timestamp.
     */
    public String getCreatedAt() {return createdAt;}

    /**
     * Sets the creation timestamp.
     */
    public void setCreatedAt( String createdAt ) {this.createdAt = createdAt;}

    /**
     * Returns the number of departments assigned, for display in tables.
     *
     * @return department count
     */
    public int getDepartmentCount() {return assignedDepartments.size();}

    /**
     * Returns the number of responders assigned, for display in tables.
     *
     * @return responder count
     */
    public int getResponderCount() {return assignedResponders.size();}

    /**
     * Returns the ID of the associated disaster report, for display in tables.
     *
     * @return the disaster report ID, or -1 if no report is set
     */
    public int getDisasterId() {
        return disasterReport != null ? disasterReport.getId() : -1;
    }

    /**
     * Adds a department to this response if not already present.
     *
     * @param department the department to add
     */
    public void addDepartment( Department department ) {
        if ( !assignedDepartments.contains( department ) ) {
            assignedDepartments.add( department );
        }
    }

    /**
     * Adds a responder to this response if not already present.
     *
     * @param responder the responder to add
     */
    public void addResponder( Responder responder ) {
        if ( !assignedResponders.contains( responder ) ) {
            assignedResponders.add( responder );
        }
    }

    /**
     * Returns a human-readable summary of this disaster response.
     *
     * @return formatted string with key fields
     */
    @Override
    public String toString() {
        return String.format(
                "DisasterResponse[id=%d, disasterId=%d, departments=%d, responders=%d, createdAt='%s']",
                id, getDisasterId(), assignedDepartments.size(),
                assignedResponders.size(), createdAt );
    }
}
