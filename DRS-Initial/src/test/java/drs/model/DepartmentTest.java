package drs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Department model class.
 * Tests cover: constructors, getters, setters, availability logic, and toString.
 */
class DepartmentTest {

    private Department department;

    /**
     * Creates a fresh Department before each test using the full constructor.
     */
    @BeforeEach
    void setUp() {
        department = new Department(
                1,
                "Fire & Emergency",
                "Emergency Services",
                "Chief Alan Burns",
                "0400-111-001");
    }

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    void testFullConstructorSetsId() {
        assertEquals(1, department.getId());
    }

    @Test
    void testFullConstructorSetsName() {
        assertEquals("Fire & Emergency", department.getName());
    }

    @Test
    void testFullConstructorSetsType() {
        assertEquals("Emergency Services", department.getType());
    }

    @Test
    void testFullConstructorSetsContactPerson() {
        assertEquals("Chief Alan Burns", department.getContactPerson());
    }

    @Test
    void testFullConstructorSetsPhone() {
        assertEquals("0400-111-001", department.getPhone());
    }

    @Test
    void testFullConstructorSetsAvailableToTrue() {
        assertTrue(department.isAvailable());
    }

    @Test
    void testNoArgsConstructorDefaultsToAvailable() {
        Department empty = new Department();
        assertTrue(empty.isAvailable());
    }

    // -------------------------------------------------------------------------
    // Availability tests
    // -------------------------------------------------------------------------

    @Test
    void testAvailabilityStatusWhenAvailable() {
        assertEquals("Available", department.getAvailabilityStatus());
    }

    @Test
    void testAvailabilityStatusWhenDeployed() {
        department.setAvailable(false);
        assertEquals("Deployed", department.getAvailabilityStatus());
    }

    @Test
    void testSetAvailableToFalseMarksAsDeployed() {
        department.setAvailable(false);
        assertFalse(department.isAvailable());
    }

    @Test
    void testSetAvailableToTrueRestoresAvailability() {
        department.setAvailable(false);
        department.setAvailable(true);
        assertTrue(department.isAvailable());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void testSetIdUpdatesCorrectly() {
        department.setId(42);
        assertEquals(42, department.getId());
    }

    @Test
    void testSetNameUpdatesCorrectly() {
        department.setName("Hospital Services");
        assertEquals("Hospital Services", department.getName());
    }

    @Test
    void testSetTypeUpdatesCorrectly() {
        department.setType("Medical");
        assertEquals("Medical", department.getType());
    }

    @Test
    void testSetContactPersonUpdatesCorrectly() {
        department.setContactPerson("Dr. Helen Carter");
        assertEquals("Dr. Helen Carter", department.getContactPerson());
    }

    @Test
    void testSetPhoneUpdatesCorrectly() {
        department.setPhone("0400-999-888");
        assertEquals("0400-999-888", department.getPhone());
    }

    // -------------------------------------------------------------------------
    // toString test
    // -------------------------------------------------------------------------

    @Test
    void testToStringContainsKeyFields() {
        String result = department.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("Fire & Emergency"));
        assertTrue(result.contains("Emergency Services"));
        assertTrue(result.contains("available=true"));
    }
}
