package drs.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central in-memory data store for the DRS application.
 * Provides static access to all disaster reports, departments, responders,
 * and response records. Pre-populates reference data on first class load.
 *
 * All controllers access data exclusively through this class (Repository pattern).
 */
public class DisasterRepository {

    // --- In-memory data collections ---
    private static final List<DisasterReport> reports = new ArrayList<>();
    private static final List<Department> departments = new ArrayList<>();
    private static final List<Responder> responders = new ArrayList<>();
    private static final List<DisasterResponse> responses = new ArrayList<>();

    // Auto-increment counters for primary keys
    private static int reportIdCounter = 1;
    private static int responseIdCounter = 1;

    // Pre-populate reference data when the class is first loaded
    static {
        seedDepartments();
        seedResponders();
    }

    // Private constructor — this is a utility class with only static methods
    private DisasterRepository() {}

    // =========================================================================
    // Disaster Report operations
    // =========================================================================

    /**
     * Adds a new disaster report, assigning the next available ID.
     *
     * @param report the report to add (id will be overwritten)
     */
    public static void addReport( DisasterReport report ) {
        report.setId( reportIdCounter++ );
        reports.add( report );
    }

    /**
     * Returns all disaster reports, sorted by priority descending (highest first).
     *
     * @return sorted list of all reports
     */
    public static List<DisasterReport> getAllReports() {
        return reports.stream()
                       .sorted( Comparator.comparingInt( DisasterReport::getPriority ).reversed() )
                       .collect( Collectors.toList() );
    }

    /**
     * Returns reports filtered by status, sorted by priority descending.
     *
     * @param status the status to filter by
     * @return filtered and sorted list
     */
    public static List<DisasterReport> getReportsByStatus( DisasterStatus status ) {
        return reports.stream()
                       .filter( r -> r.getStatus() == status )
                       .sorted( Comparator.comparingInt( DisasterReport::getPriority ).reversed() )
                       .collect( Collectors.toList() );
    }

    /**
     * Returns reports filtered by both type and status.
     * Pass null for either parameter to skip that filter.
     *
     * @param type the disaster type filter, or null for any
     * @param status the status filter, or null for any
     * @return filtered and sorted list
     */
    public static List<DisasterReport> filterReports( DisasterType type, DisasterStatus status ) {
        return reports.stream()
                       .filter( r -> type == null || r.getType() == type )
                       .filter( r -> status == null || r.getStatus() == status )
                       .sorted( Comparator.comparingInt( DisasterReport::getPriority ).reversed() )
                       .collect( Collectors.toList() );
    }

    /**
     * Returns reports whose status is ASSESSING or RESPONDING (active incidents).
     *
     * @return list of active disaster reports
     */
    public static List<DisasterReport> getActiveReports() {
        return reports.stream()
                       .filter( r -> r.getStatus() == DisasterStatus.ASSESSING
                                             || r.getStatus() == DisasterStatus.RESPONDING )
                       .sorted( Comparator.comparingInt( DisasterReport::getPriority ).reversed() )
                       .collect( Collectors.toList() );
    }

    /**
     * Updates the status of a disaster report.
     *
     * @param reportId the ID of the report to update
     * @param newStatus the new status to set
     */
    public static void updateReportStatus( int reportId, DisasterStatus newStatus ) {
        reports.stream()
                .filter( r -> r.getId() == reportId )
                .findFirst()
                .ifPresent( r -> r.setStatus( newStatus ) );
    }

    /**
     * Updates the priority of a disaster report.
     *
     * @param reportId the ID of the report to update
     * @param newPriority the new priority value
     */
    public static void updateReportPriority( int reportId, int newPriority ) {
        reports.stream()
                .filter( r -> r.getId() == reportId )
                .findFirst()
                .ifPresent( r -> r.setPriority( newPriority ) );
    }

    // =========================================================================
    // Department operations
    // =========================================================================

    /**
     * Returns all departments.
     *
     * @return list of all departments
     */
    public static List<Department> getAllDepartments() {
        return new ArrayList<>( departments );
    }

    /**
     * Returns only departments that are currently available.
     *
     * @return list of available departments
     */
    public static List<Department> getAvailableDepartments() {
        return departments.stream()
                       .filter( Department::isAvailable )
                       .collect( Collectors.toList() );
    }

    /**
     * Returns departments that can still be assigned to a new response:
     * the department must itself be available (not already deployed) and
     * must have at least one available responder to send.
     *
     * Used by the coordination screen to populate the selectable department list.
     *
     * @return list of assignable departments
     */
    public static List<Department> getDepartmentsWithAvailableResponders() {
        return departments.stream()
                       .filter( Department::isAvailable )
                       .filter( d -> responders.stream()
                                             .anyMatch( r -> r.getDepartment() == d && r.isAvailable() ) )
                       .collect( Collectors.toList() );
    }

    /**
     * Marks a department as deployed (unavailable).
     * Used when a department is assigned to a disaster response. Unlike
     * toggleDepartmentAvailability, this always sets the department to
     * Deployed, so assigning the same department to a second incident
     * cannot accidentally flip it back to Available.
     *
     * @param departmentId the ID of the department to deploy
     */
    public static void markDepartmentDeployed( int departmentId ) {
        departments.stream()
                .filter( d -> d.getId() == departmentId )
                .findFirst()
                .ifPresent( d -> d.setAvailable( false ) );
    }

    /**
     * Toggles the availability of a department between Available and Deployed.
     * Used by the Resource Management screen, where a manual toggle is the
     * intended behaviour.
     *
     * @param departmentId the ID of the department to toggle
     */
    public static void toggleDepartmentAvailability( int departmentId ) {
        departments.stream()
                .filter( d -> d.getId() == departmentId )
                .findFirst()
                .ifPresent( d -> d.setAvailable( !d.isAvailable() ) );
    }

    // =========================================================================
    // Responder operations
    // =========================================================================

    /**
     * Returns all responders.
     *
     * @return list of all responders
     */
    public static List<Responder> getAllResponders() {
        return new ArrayList<>( responders );
    }

    /**
     * Returns only responders that are currently available.
     *
     * @return list of available responders
     */
    public static List<Responder> getAvailableResponders() {
        return responders.stream()
                       .filter( Responder::isAvailable )
                       .collect( Collectors.toList() );
    }

    /**
     * Marks a responder as deployed (unavailable).
     * Used when a responder is assigned to a disaster response, so that
     * repeated assignment can never flip the responder back to Available.
     *
     * @param responderId the ID of the responder to deploy
     */
    public static void markResponderDeployed( int responderId ) {
        responders.stream()
                .filter( r -> r.getId() == responderId )
                .findFirst()
                .ifPresent( r -> r.setAvailable( false ) );
    }

    /**
     * Toggles the availability of a responder between Available and Deployed.
     * Used by the Resource Management screen, where a manual toggle is the
     * intended behaviour.
     *
     * @param responderId the ID of the responder to toggle
     */
    public static void toggleResponderAvailability( int responderId ) {
        responders.stream()
                .filter( r -> r.getId() == responderId )
                .findFirst()
                .ifPresent( r -> r.setAvailable( !r.isAvailable() ) );
    }

    // =========================================================================
    // Response operations
    // =========================================================================

    /**
     * Saves a new disaster response record, assigning the next available ID.
     *
     * @param response the response to save
     */
    public static void addResponse( DisasterResponse response ) {
        response.setId( responseIdCounter++ );
        responses.add( response );
    }

    /**
     * Returns the response record for a specific disaster report, if any exists.
     *
     * @param reportId the ID of the disaster report
     * @return the DisasterResponse, or null if none has been created yet
     */
    public static DisasterResponse getResponseForReport( int reportId ) {
        return responses.stream()
                       .filter( r -> r.getDisasterId() == reportId )
                       .findFirst()
                       .orElse( null );
    }

    /**
     * Returns every response record created for a specific disaster report,
     * in the order they were saved. A single disaster may be responded to
     * more than once as additional departments are brought in.
     *
     * @param reportId the ID of the disaster report
     * @return list of matching responses (empty if none exist)
     */
    public static List<DisasterResponse> getResponsesForReport( int reportId ) {
        return responses.stream()
                       .filter( r -> r.getDisasterId() == reportId )
                       .collect( Collectors.toList() );
    }

    /**
     * Returns all response records.
     *
     * @return list of all disaster responses
     */
    public static List<DisasterResponse> getAllResponses() {
        return new ArrayList<>( responses );
    }

    // =========================================================================
    // Seed data — pre-populated reference data
    // =========================================================================

    /**
     * Seeds the eight standard departments used by the DRS.
     * Called once via the static initialiser.
     */
    private static void seedDepartments() {
        departments.add(
                new Department( 1, "Fire & Emergency", "Emergency Services", "Chief Alan Burns", "0400-111-001" ) );
        departments.add( new Department( 2, "Hospital Services", "Medical", "Dr. Helen Carter", "0400-111-002" ) );
        departments.add(
                new Department( 3, "Electricity Authority", "Utilities", "Engineer Sam Reid", "0400-111-003" ) );
        departments.add(
                new Department( 4, "Transportation Dept", "Infrastructure", "Director Fiona Clarke", "0400-111-004" ) );
        departments.add(
                new Department( 5, "Waste Management", "Sanitation", "Supervisor Gary Walsh", "0400-111-005" ) );
        departments.add(
                new Department( 6, "Water Supply Authority", "Utilities", "Engineer Nina Patel", "0400-111-006" ) );
        departments.add(
                new Department( 7, "School Administration", "Education", "Principal Roy Dawson", "0400-111-007" ) );
        departments.add( new Department( 8, "Law Enforcement", "Security", "Sergeant Kim Torres", "0400-111-008" ) );
    }

    /**
     * Seeds responders (personnel) for each department.
     * Called once via the static initialiser.
     */
    private static void seedResponders() {
        // Fire & Emergency (dept 1)
        Department fire = departments.get( 0 );
        responders.add( new Responder( 1, "John Smith", "Firefighter", "0401-001-001", fire ) );
        responders.add( new Responder( 2, "Maria Garcia", "Fire Captain", "0401-001-002", fire ) );

        // Hospital Services (dept 2)
        Department hospital = departments.get( 1 );
        responders.add( new Responder( 3, "Dr. James Lee", "Doctor", "0401-002-001", hospital ) );
        responders.add( new Responder( 4, "Anna Brown", "Paramedic", "0401-002-002", hospital ) );

        // Electricity Authority (dept 3)
        Department electricity = departments.get( 2 );
        responders.add( new Responder( 5, "Mike Johnson", "Electrician", "0401-003-001", electricity ) );
        responders.add( new Responder( 6, "Sarah Williams", "Electrical Engineer", "0401-003-002", electricity ) );

        // Transportation Dept (dept 4)
        Department transport = departments.get( 3 );
        responders.add( new Responder( 7, "Tom Davis", "Traffic Controller", "0401-004-001", transport ) );
        responders.add( new Responder( 8, "Lisa Martinez", "Road Engineer", "0401-004-002", transport ) );

        // Waste Management (dept 5)
        Department waste = departments.get( 4 );
        responders.add( new Responder( 9, "Paul Wilson", "Waste Technician", "0401-005-001", waste ) );
        responders.add( new Responder( 10, "Rachel Anderson", "Sanitation Supervisor", "0401-005-002", waste ) );

        // Water Supply Authority (dept 6)
        Department water = departments.get( 5 );
        responders.add( new Responder( 11, "David Taylor", "Plumber", "0401-006-001", water ) );
        responders.add( new Responder( 12, "Emma Thomas", "Water Engineer", "0401-006-002", water ) );

        // School Administration (dept 7)
        Department schools = departments.get( 6 );
        responders.add( new Responder( 13, "Robert Jackson", "Principal", "0401-007-001", schools ) );
        responders.add( new Responder( 14, "Susan Harris", "Welfare Officer", "0401-007-002", schools ) );

        // Law Enforcement (dept 8)
        Department law = departments.get( 7 );
        responders.add( new Responder( 15, "Officer Chris White", "Police Officer", "0401-008-001", law ) );
        responders.add( new Responder( 16, "Sergeant Pat Lewis", "Sergeant", "0401-008-002", law ) );
    }
}
