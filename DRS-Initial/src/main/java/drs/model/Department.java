package drs.model;

/**
 * Represents a government or emergency department that can be assigned
 * to a disaster response. Each department has a contact person and
 * an availability status indicating whether it is currently free or deployed.
 */
public class Department {

    private int id;
    private String name;
    private String type;            // e.g., "Emergency Services", "Medical"
    private String contactPerson;
    private String phone;
    private boolean available;      // true = available; false = currently deployed

    /**
     * No-args constructor. Sets availability to true by default.
     */
    public Department() {
        this.available = true;
    }

    /**
     * Full constructor for creating a department with all details.
     *
     * @param id the unique identifier
     * @param name the department name
     * @param type the department category
     * @param contactPerson the primary contact name
     * @param phone the contact phone number
     */
    public Department( int id, String name, String type,
                       String contactPerson, String phone ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.available = true;
    }

    // --- Accessors ---

    /**
     * Returns the unique identifier for this department.
     */
    public int getId() {return id;}

    /**
     * Sets the unique identifier.
     */
    public void setId( int id ) {this.id = id;}

    /**
     * Returns the department name.
     */
    public String getName() {return name;}

    /**
     * Sets the department name.
     */
    public void setName( String name ) {this.name = name;}

    /**
     * Returns the department type/category.
     */
    public String getType() {return type;}

    /**
     * Sets the department type/category.
     */
    public void setType( String type ) {this.type = type;}

    /**
     * Returns the name of the primary contact person.
     */
    public String getContactPerson() {return contactPerson;}

    // --- Mutators ---

    /**
     * Sets the primary contact person.
     */
    public void setContactPerson( String contactPerson ) {
        this.contactPerson = contactPerson;
    }

    /**
     * Returns the department phone number.
     */
    public String getPhone() {return phone;}

    /**
     * Sets the contact phone number.
     */
    public void setPhone( String phone ) {this.phone = phone;}

    /**
     * Returns true if the department is currently available for deployment.
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
     * Returns a human-readable summary of this department.
     *
     * @return formatted string with key fields
     */
    @Override
    public String toString() {
        return String.format( "Department[id=%d, name='%s', type='%s', available=%b]",
                id, name, type, available );
    }
}
