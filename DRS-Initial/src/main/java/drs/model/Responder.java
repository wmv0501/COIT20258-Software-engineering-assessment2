package drs.model;

/**
 * Represents an individual responder (person) who can be assigned
 * to a disaster response. Each responder belongs to a department
 * and has an availability status.
 */
public class Responder {

    private int id;
    private String name;
    private String role;                // e.g., "Firefighter", "Paramedic"
    private String phone;
    private Department department;      // the department this responder belongs to
    private boolean available;          // true = available; false = currently deployed

    /**
     * No-args constructor. Sets availability to true by default.
     */
    public Responder() {
        this.available = true;
    }

    /**
     * Full constructor for creating a responder with all details.
     *
     * @param id the unique identifier
     * @param name the responder's full name
     * @param role the responder's role/job title
     * @param phone the responder's contact phone
     * @param department the department this responder belongs to
     */
    public Responder( int id, String name, String role,
                      String phone, Department department ) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.department = department;
        this.available = true;
    }

    // --- Accessors ---

    /**
     * Returns the unique identifier for this responder.
     */
    public int getId() {return id;}

    /**
     * Sets the unique identifier.
     */
    public void setId( int id ) {this.id = id;}

    /**
     * Returns the responder's full name.
     */
    public String getName() {return name;}

    /**
     * Sets the responder's full name.
     */
    public void setName( String name ) {this.name = name;}

    /**
     * Returns the responder's role/job title.
     */
    public String getRole() {return role;}

    /**
     * Sets the responder's role/job title.
     */
    public void setRole( String role ) {this.role = role;}

    /**
     * Returns the responder's phone number.
     */
    public String getPhone() {return phone;}

    /**
     * Sets the responder's phone number.
     */
    public void setPhone( String phone ) {this.phone = phone;}

    // --- Mutators ---

    /**
     * Returns the department this responder belongs to.
     */
    public Department getDepartment() {return department;}

    /**
     * Sets the department this responder belongs to.
     */
    public void setDepartment( Department department ) {
        this.department = department;
    }

    /**
     * Returns the department name for use in TableView columns.
     *
     * @return the department name, or "N/A" if no department is set
     */
    public String getDepartmentName() {
        return department != null ? department.getName() : "N/A";
    }

    /**
     * Returns true if this responder is currently available.
     */
    public boolean isAvailable() {return available;}

    /**
     * Sets the availability status.
     */
    public void setAvailable( boolean available ) {this.available = available;}

    /**
     * Returns availability as a display string for TableView columns.
     *
     * @return "Available" or "Deployed"
     */
    public String getAvailabilityStatus() {
        return available ? "Available" : "Deployed";
    }

    /**
     * Returns a human-readable summary of this responder.
     *
     * @return formatted string with key fields
     */
    @Override
    public String toString() {
        return String.format( "Responder[id=%d, name='%s', role='%s', dept='%s', available=%b]",
                id, name, role, getDepartmentName(), available );
    }
}
