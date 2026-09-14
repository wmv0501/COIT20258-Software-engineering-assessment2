package drs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DisasterReport model class.
 * Tests cover: constructors, getters, setters, toString, and priority computation.
 */
class DisasterReportTest {

    private DisasterReport report;

    /**
     * Creates a fresh DisasterReport before each test using the full constructor.
     */
    @BeforeEach
    void setUp() {
        report = new DisasterReport(
                1,
                DisasterType.FIRE,
                Severity.HIGH,
                "12 Main Street, Brisbane",
                "Residential building fire with multiple floors affected.",
                "Jane Doe",
                "0400-123-456");
    }

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    void testFullConstructorSetsId() {
        assertEquals(1, report.getId());
    }

    @Test
    void testFullConstructorSetsType() {
        assertEquals(DisasterType.FIRE, report.getType());
    }

    @Test
    void testFullConstructorSetsSeverity() {
        assertEquals(Severity.HIGH, report.getSeverity());
    }

    @Test
    void testFullConstructorSetsLocation() {
        assertEquals("12 Main Street, Brisbane", report.getLocation());
    }

    @Test
    void testFullConstructorSetsDescription() {
        assertEquals("Residential building fire with multiple floors affected.",
                report.getDescription());
    }

    @Test
    void testFullConstructorSetsReporterName() {
        assertEquals("Jane Doe", report.getReporterName());
    }

    @Test
    void testFullConstructorSetsReporterPhone() {
        assertEquals("0400-123-456", report.getReporterPhone());
    }

    @Test
    void testFullConstructorSetsDefaultStatusToReported() {
        assertEquals(DisasterStatus.REPORTED, report.getStatus());
    }

    @Test
    void testFullConstructorComputesPriorityAutomatically() {
        // FIRE weight=8, HIGH weight=30 → expected priority=38
        assertEquals(38, report.getPriority());
    }

    @Test
    void testNoArgsConstructorCreatesReportedStatus() {
        DisasterReport empty = new DisasterReport();
        assertEquals(DisasterStatus.REPORTED, empty.getStatus());
    }

    @Test
    void testNoArgsConstructorSetsTimestamp() {
        DisasterReport empty = new DisasterReport();
        assertNotNull(empty.getTimestamp());
        assertFalse(empty.getTimestamp().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Priority computation tests
    // -------------------------------------------------------------------------

    @Test
    void testComputePriorityHurricaneCritical() {
        // HURRICANE=10, CRITICAL=40 → expected=50
        int priority = DisasterReport.computePriority(DisasterType.HURRICANE, Severity.CRITICAL);
        assertEquals(50, priority);
    }

    @Test
    void testComputePriorityOtherLow() {
        // OTHER=5, LOW=10 → expected=15
        int priority = DisasterReport.computePriority(DisasterType.OTHER, Severity.LOW);
        assertEquals(15, priority);
    }

    @Test
    void testComputePriorityEarthquakeMedium() {
        // EARTHQUAKE=9, MEDIUM=20 → expected=29
        int priority = DisasterReport.computePriority(DisasterType.EARTHQUAKE, Severity.MEDIUM);
        assertEquals(29, priority);
    }

    @Test
    void testComputePriorityFloodHigh() {
        // FLOOD=7, HIGH=30 → expected=37
        int priority = DisasterReport.computePriority(DisasterType.FLOOD, Severity.HIGH);
        assertEquals(37, priority);
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void testSetStatusUpdatesCorrectly() {
        report.setStatus(DisasterStatus.ASSESSING);
        assertEquals(DisasterStatus.ASSESSING, report.getStatus());
    }

    @Test
    void testSetTypeRecomputesPriority() {
        // Change from FIRE (8) to HURRICANE (10), severity stays HIGH (30) → expected=40
        report.setType(DisasterType.HURRICANE);
        assertEquals(DisasterType.HURRICANE, report.getType());
        assertEquals(40, report.getPriority());
    }

    @Test
    void testSetSeverityRecomputesPriority() {
        // FIRE (8) + CRITICAL (40) → expected=48
        report.setSeverity(Severity.CRITICAL);
        assertEquals(Severity.CRITICAL, report.getSeverity());
        assertEquals(48, report.getPriority());
    }

    @Test
    void testSetPriorityOverridesComputedValue() {
        report.setPriority(99);
        assertEquals(99, report.getPriority());
    }

    @Test
    void testSetLocationUpdatesCorrectly() {
        report.setLocation("Townsville");
        assertEquals("Townsville", report.getLocation());
    }

    // -------------------------------------------------------------------------
    // toString test
    // -------------------------------------------------------------------------

    @Test
    void testToStringContainsKeyFields() {
        String result = report.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("Fire"));
        assertTrue(result.contains("High"));
        assertTrue(result.contains("Jane Doe"));
    }
}
