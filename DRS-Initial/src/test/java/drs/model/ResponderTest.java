package drs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Responder model class.
 * Tests cover: constructors, getters, setters, availability logic,
 * department name helper, and toString.
 */
class ResponderTest {

    private Department department;
    private Responder  responder;

    /**
     * Creates a fresh Department and Responder before each test.
     */
    @BeforeEach
    void setUp() {
        department = new Department(
                1, "Fire & Emergency", "Emergency Services",
                "Chief Alan Burns", "0400-111-001");

        responder = new Responder(
                1, "John Smith", "Firefighter", "0401-001-001", department);
    }

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    void testFullConstructorSetsId() {
        assertEquals(1, responder.getId());
    }

    @Test
    void testFullConstructorSetsName() {
        assertEquals("John Smith", responder.getName());
    }

    @Test
    void testFullConstructorSetsRole() {
        assertEquals("Firefighter", responder.getRole());
    }

    @Test
    void testFullConstructorSetsPhone() {
        assertEquals("0401-001-001", responder.getPhone());
    }

    @Test
    void testFullConstructorSetsDepartment() {
        assertSame(department, responder.getDepartment());
    }

    @Test
    void testFullConstructorSetsAvailableToTrue() {
        assertTrue(responder.isAvailable());
    }

    @Test
    void testNoArgsConstructorDefaultsToAvailable() {
        Responder empty = new Responder();
        assertTrue(empty.isAvailable());
    }

    // -------------------------------------------------------------------------
    // Availability tests
    // -------------------------------------------------------------------------

    @Test
    void testAvailabilityStatusWhenAvailable() {
        assertEquals("Available", responder.getAvailabilityStatus());
    }

    @Test
    void testAvailabilityStatusWhenDeployed() {
        responder.setAvailable(false);
        assertEquals("Deployed", responder.getAvailabilityStatus());
    }

    @Test
    void testSetAvailableToFalseMarksAsDeployed() {
        responder.setAvailable(false);
        assertFalse(responder.isAvailable());
    }

    @Test
    void testSetAvailableToTrueRestoresAvailability() {
        responder.setAvailable(false);
        responder.setAvailable(true);
        assertTrue(responder.isAvailable());
    }

    // -------------------------------------------------------------------------
    // Department name helper tests
    // -------------------------------------------------------------------------

    @Test
    void testGetDepartmentNameReturnsDepartmentName() {
        assertEquals("Fire & Emergency", responder.getDepartmentName());
    }

    @Test
    void testGetDepartmentNameWhenNullDepartmentReturnsNA() {
        Responder noDept = new Responder(2, "Jane Doe", "Paramedic", "0401-002-001", null);
        assertEquals("N/A", noDept.getDepartmentName());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void testSetIdUpdatesCorrectly() {
        responder.setId(42);
        assertEquals(42, responder.getId());
    }

    @Test
    void testSetNameUpdatesCorrectly() {
        responder.setName("Maria Garcia");
        assertEquals("Maria Garcia", responder.getName());
    }

    @Test
    void testSetRoleUpdatesCorrectly() {
        responder.setRole("Fire Captain");
        assertEquals("Fire Captain", responder.getRole());
    }

    @Test
    void testSetPhoneUpdatesCorrectly() {
        responder.setPhone("0401-999-888");
        assertEquals("0401-999-888", responder.getPhone());
    }

    @Test
    void testSetDepartmentUpdatesCorrectly() {
        Department newDept = new Department(
                2, "Hospital Services", "Medical", "Dr. Helen Carter", "0400-111-002");
        responder.setDepartment(newDept);
        assertSame(newDept, responder.getDepartment());
        assertEquals("Hospital Services", responder.getDepartmentName());
    }

    // -------------------------------------------------------------------------
    // toString test
    // -------------------------------------------------------------------------

    @Test
    void testToStringContainsKeyFields() {
        String result = responder.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("Firefighter"));
        assertTrue(result.contains("Fire & Emergency"));
        assertTrue(result.contains("available=true"));
    }
}
