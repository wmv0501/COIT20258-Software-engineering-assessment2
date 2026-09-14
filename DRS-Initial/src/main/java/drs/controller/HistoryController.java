package drs.controller;

import drs.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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

    // Label shown in the description column (detail on selection)
    @FXML private Label descriptionLabel;

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

        // Show the description of the selected report below the table
        historyTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        descriptionLabel.setText(newVal.getDescription());
                    } else {
                        descriptionLabel.setText("");
                    }
                });

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
