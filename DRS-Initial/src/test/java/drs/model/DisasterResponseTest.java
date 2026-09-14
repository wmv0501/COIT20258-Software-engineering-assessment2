package drs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DisasterResponse model class.
 * Tests cover: constructors, getters, setters, addDepartment/addResponder,
 * count helpers, and toString.
 */
class DisasterResponseTest {

    private DisasterReport  report;
    private Department      dept1;
    private Department      dept2;
    private Responder       resp1;
    private Responder       resp2;
    private DisasterResponse response;

    /**
     * Creates supporting objects and a full DisasterResponse before each test.
     */
    @BeforeEach
    void setUp() {
        // Build a sample disaster report
        report = new DisasterReport(
                10,
                DisasterType.HURRICANE,
                Severity.CRITICAL,
                "Cairns, QLD",
                "Category 5 hurricane making landfall.",
                "John Reporter",
                "0400-000-001");

        // Build two departments
        dept1 = new Department(1, "Fire & Emergency", "Emergency", "Chief Burns", "0400-111-001");
        dept2 = new Department(2, "Hospital Services", "Medical",  "Dr. Carter",  "0400-111-002");

        // Build two responders
        resp1 = new Responder(1, "John Smith",  "Firefighter", "0401-001-001", dept1);
        resp2 = new Responder(2, "Anna Brown",  "Paramedic",   "0401-002-001", dept2);

        List<Department> depts = Arrays.asList(dept1, dept2);
        List<Responder>  resps = Arrays.asList(resp1, resp2);

        response = new DisasterResponse(5, report, depts, resps, "Evacuate zone A first.");
    }

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    void testFullConstructorSetsId() {
        assertEquals(5, response.getId());
    }

    @Test
    void testFullConstructorSetsDisasterReport() {
        assertSame(report, response.getDisasterReport());
    }

    @Test
    void testFullConstructorSetsNotes() {
        assertEquals("Evacuate zone A first.", response.getNotes());
    }

    @Test
    void testFullConstructorCopiesDepartmentsList() {
        assertEquals(2, response.getAssignedDepartments().size());
        assertTrue(response.getAssignedDepartments().contains(dept1));
        assertTrue(response.getAssignedDepartments().contains(dept2));
    }

    @Test
    void testFullConstructorCopiesRespondersList() {
        assertEquals(2, response.getAssignedResponders().size());
        assertTrue(response.getAssignedResponders().contains(resp1));
        assertTrue(response.getAssignedResponders().contains(resp2));
    }

    @Test
    void testFullConstructorSetsTimestamp() {
        assertNotNull(response.getCreatedAt());
        assertFalse(response.getCreatedAt().isEmpty());
    }

    @Test
    void testNoArgsConstructorInitialisesEmptyLists() {
        DisasterResponse empty = new DisasterResponse();
        assertNotNull(empty.getAssignedDepartments());
        assertNotNull(empty.getAssignedResponders());
        assertEquals(0, empty.getDepartmentCount());
        assertEquals(0, empty.getResponderCount());
    }

    // -------------------------------------------------------------------------
    // Count helper tests
    // -------------------------------------------------------------------------

    @Test
    void testGetDepartmentCount() {
        assertEquals(2, response.getDepartmentCount());
    }

    @Test
    void testGetResponderCount() {
        assertEquals(2, response.getResponderCount());
    }

    @Test
    void testGetDisasterId() {
        assertEquals(10, response.getDisasterId());
    }

    @Test
    void testGetDisasterIdWhenNullReportReturnsMinusOne() {
        DisasterResponse emptyResp = new DisasterResponse();
        assertEquals(-1, emptyResp.getDisasterId());
    }

    // -------------------------------------------------------------------------
    // addDepartment / addResponder tests
    // -------------------------------------------------------------------------

    @Test
    void testAddDepartmentIncreasesCount() {
        DisasterResponse r = new DisasterResponse();
        r.addDepartment(dept1);
        assertEquals(1, r.getDepartmentCount());
    }

    @Test
    void testAddDepartmentDoesNotAddDuplicate() {
        DisasterResponse r = new DisasterResponse();
        r.addDepartment(dept1);
        r.addDepartment(dept1); // add same object again
        assertEquals(1, r.getDepartmentCount());
    }

    @Test
    void testAddResponderIncreasesCount() {
        DisasterResponse r = new DisasterResponse();
        r.addResponder(resp1);
        assertEquals(1, r.getResponderCount());
    }

    @Test
    void testAddResponderDoesNotAddDuplicate() {
        DisasterResponse r = new DisasterResponse();
        r.addResponder(resp1);
        r.addResponder(resp1);
        assertEquals(1, r.getResponderCount());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void testSetIdUpdatesCorrectly() {
        response.setId(99);
        assertEquals(99, response.getId());
    }

    @Test
    void testSetNotesUpdatesCorrectly() {
        response.setNotes("New instructions.");
        assertEquals("New instructions.", response.getNotes());
    }

    // -------------------------------------------------------------------------
    // toString test
    // -------------------------------------------------------------------------

    @Test
    void testToStringContainsKeyFields() {
        String result = response.toString();
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("disasterId=10"));
        assertTrue(result.contains("departments=2"));
        assertTrue(result.contains("responders=2"));
    }
}
