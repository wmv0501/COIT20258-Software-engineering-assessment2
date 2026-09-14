package drs.controller;

import drs.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.stream.Collectors;

/**
 * Controller for the "History Log" tab (Creative Feature 1).
 * Displays a filterable, scrollable log of all disaster reports ever submitted.
 * Users can filter by disaster type and/or status to narrow the list.
 */
public class HistoryController {

    // --- Filter controls ---
    @FXML private ComboBox<String>         typeFilterComboBox;
    @FXML private ComboBox<String>         statusFilterComboBox;
    @FXML private Label                    countLabel;

    // --- History table ---
    @FXML private TableView<DisasterReport>             historyTable;
    @FXML private TableColumn<DisasterReport, Integer>  idCol;
    @FXML private TableColumn<DisasterReport, String>   typeCol;
    @FXML private TableColumn<DisasterReport, String>   severityCol;
    @FXML private TableColumn<DisasterReport, String>   locationCol;
    @FXML private TableColumn<DisasterReport, Integer>  priorityCol;
    @FXML private TableColumn<DisasterReport, String>   statusCol;
    @FXML private TableColumn<DisasterReport, String>   reporterCol;
    @FXML private TableColumn<DisasterReport, String>   timestampCol;

    // --- Detail side panel — report fields ---
    @FXML private Label     detailPlaceholder;
    @FXML private GridPane  detailGrid;
    @FXML private Label     detailId;
    @FXML private Label     detailType;
    @FXML private Label     detailSeverity;
    @FXML private Label     detailStatus;
    @FXML private Label     detailPriority;
    @FXML private Label     detailLocation;
    @FXML private Label     detailReporter;
    @FXML private Label     detailPhone;
    @FXML private Label     detailTimestamp;
    @FXML private Separator detailDescSep;
    @FXML private Label     detailDescHeading;
    @FXML private Label     descriptionLabel;

    // --- Detail side panel — response fields ---
    @FXML private Separator detailRespSep;
    @FXML private Label     detailRespHeading;
    @FXML private Label     detailRespNoResponse;
    @FXML private Label     detailDeptHeading;
    @FXML private Label     detailDepartments;
    @FXML private Label     detailRespPersonHeading;
    @FXML private Label     detailResponders;
    @FXML private Label     detailNotesHeading;
    @FXML private Label     detailRespNotes;

    /** "All" option label used in filter ComboBoxes. */
    private static final String ALL_OPTION = "All";

    /** Observable list backing the history table. */
    private final ObservableList<DisasterReport> tableData = FXCollections.observableArrayList();

    /**
     * Initialises filter ComboBoxes and table columns.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        // Build type filter options: "All" + each DisasterType label
        ObservableList<String> typeOptions = FXCollections.observableArrayList(ALL_OPTION);
        for (DisasterType t : DisasterType.values()) {
            typeOptions.add(t.getLabel());
        }
        typeFilterComboBox.setItems(typeOptions);
        typeFilterComboBox.getSelectionModel().selectFirst();

        // Build status filter options: "All" + each DisasterStatus label
        ObservableList<String> statusOptions = FXCollections.observableArrayList(ALL_OPTION);
        for (DisasterStatus s : DisasterStatus.values()) {
            statusOptions.add(s.getLabel());
        }
        statusFilterComboBox.setItems(statusOptions);
        statusFilterComboBox.getSelectionModel().selectFirst();

        // Wire table columns to model getters
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severity"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        reporterCol.setCellValueFactory(new PropertyValueFactory<>("reporterName"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        historyTable.setItems(tableData);

        // Populate the side detail panel when a row is clicked
        historyTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showDetail(newVal));

        loadAllReports();
    }

    /**
     * Refreshes the table from the repository with no filters applied.
     * Called by MainController when this tab becomes active.
     */
    public void refresh() {
        loadAllReports();
    }

    /**
     * Handles the "Apply Filter" button.
     * Reads the selected filter options and queries the repository.
     */
    @FXML
    private void handleApplyFilter() {
        String typeSelection   = typeFilterComboBox.getValue();
        String statusSelection = statusFilterComboBox.getValue();

        // Convert UI selection back to enum (null means "All")
        DisasterType   typeFilter   = resolveTypeFilter(typeSelection);
        DisasterStatus statusFilter = resolveStatusFilter(statusSelection);

        tableData.setAll(DisasterRepository.filterReports(typeFilter, statusFilter));
        updateCountLabel();
    }

    /**
     * Handles the "Clear Filters" button.
     * Resets both ComboBoxes to "All" and reloads the full history.
     */
    @FXML
    private void handleClearFilters() {
        typeFilterComboBox.getSelectionModel().selectFirst();
        statusFilterComboBox.getSelectionModel().selectFirst();
        loadAllReports();
    }

    /** Loads all reports (no filter) into the table and updates the count label. */
    private void loadAllReports() {
        tableData.setAll(DisasterRepository.getAllReports());
        updateCountLabel();
    }

    /** Updates the record count label above the table. */
    private void updateCountLabel() {
        countLabel.setText("Showing " + tableData.size() + " record(s)");
    }

    /**
     * Populates the side detail panel with all fields from the selected report,
     * including response information (departments, responders, activities/notes)
     * if a DisasterResponse has been saved for this report.
     *
     * @param report the selected report, or null if selection is cleared
     */
    private void showDetail(DisasterReport report) {
        boolean hasReport = report != null;

        setVisible(detailPlaceholder, !hasReport);
        setVisible(detailGrid,        hasReport);
        setVisible(detailDescSep,     hasReport);
        setVisible(detailDescHeading, hasReport);
        setVisible(detailRespSep,     hasReport);
        setVisible(detailRespHeading, hasReport);

        if (!hasReport) {
            descriptionLabel.setText("");
            hideResponseFields();
            return;
        }

        // --- Report fields ---
        detailId.setText(String.valueOf(report.getId()));
        detailType.setText(report.getType().toString());
        detailSeverity.setText(report.getSeverity().toString());
        detailStatus.setText(report.getStatus().toString());
        detailPriority.setText(String.valueOf(report.getPriority()));
        detailLocation.setText(report.getLocation());
        detailReporter.setText(report.getReporterName());
        detailPhone.setText(report.getReporterPhone());
        detailTimestamp.setText(report.getTimestamp());
        descriptionLabel.setText(report.getDescription());

        // --- Response fields ---
        DisasterResponse response = DisasterRepository.getResponseForReport(report.getId());
        if (response == null) {
            setVisible(detailRespNoResponse, true);
            hideResponseFields();
            return;
        }

        setVisible(detailRespNoResponse, false);

        String depts = response.getAssignedDepartments().stream()
                .map(Department::getName)
                .collect(Collectors.joining("\n"));
        detailDepartments.setText(depts.isEmpty() ? "None" : depts);
        setVisible(detailDeptHeading,   true);
        setVisible(detailDepartments,   true);

        String resps = response.getAssignedResponders().stream()
                .map(r -> r.getName() + " (" + r.getRole() + ")")
                .collect(Collectors.joining("\n"));
        detailResponders.setText(resps.isEmpty() ? "None" : resps);
        setVisible(detailRespPersonHeading, true);
        setVisible(detailResponders,        true);

        String notes = response.getNotes();
        detailRespNotes.setText(notes.isEmpty() ? "None" : notes);
        setVisible(detailNotesHeading, true);
        setVisible(detailRespNotes,    true);
    }

    /** Hides all response-specific fields (departments, responders, notes). */
    private void hideResponseFields() {
        setVisible(detailDeptHeading,       false);
        setVisible(detailDepartments,       false);
        setVisible(detailRespPersonHeading, false);
        setVisible(detailResponders,        false);
        setVisible(detailNotesHeading,      false);
        setVisible(detailRespNotes,         false);
    }

    /** Convenience method to set both visible and managed on a node simultaneously. */
    private void setVisible(javafx.scene.Node node, boolean value) {
        node.setVisible(value);
        node.setManaged(value);
    }

    /**
     * Converts a type filter label to the corresponding DisasterType enum value.
     *
     * @param label the selected ComboBox label
     * @return the matching DisasterType, or null for "All"
     */
    private DisasterType resolveTypeFilter(String label) {
        if (label == null || ALL_OPTION.equals(label)) return null;
        for (DisasterType t : DisasterType.values()) {
            if (t.getLabel().equals(label)) return t;
        }
        return null;
    }

    /**
     * Converts a status filter label to the corresponding DisasterStatus enum value.
     *
     * @param label the selected ComboBox label
     * @return the matching DisasterStatus, or null for "All"
     */
    private DisasterStatus resolveStatusFilter(String label) {
        if (label == null || ALL_OPTION.equals(label)) return null;
        for (DisasterStatus s : DisasterStatus.values()) {
            if (s.getLabel().equals(label)) return s;
        }
        return null;
    }
}
