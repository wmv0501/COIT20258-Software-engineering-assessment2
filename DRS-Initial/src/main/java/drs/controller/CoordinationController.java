package drs.controller;

import drs.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for the "Coordinate Response" tab.
 * Allows a coordinator to select an active disaster, choose departments
 * and responders to assign, enter notes, and save a DisasterResponse record.
 * Assigned departments and responders are marked as Deployed in the repository.
 */
public class CoordinationController {

    // --- Disaster selection ---
    @FXML private ComboBox<DisasterReport>  disasterComboBox;
    @FXML private Label                     disasterDetailLabel;

    // --- Department selection list ---
    @FXML private ListView<Department>      departmentListView;

    // --- Responder selection list ---
    @FXML private ListView<Responder>       responderListView;

    // --- Notes ---
    @FXML private TextArea                  notesArea;

    // --- Save button and feedback label ---
    @FXML private Button                    saveResponseBtn;
    @FXML private Label                     statusLabel;

    // --- Current assignments table ---
    @FXML private TableView<DisasterResponse>             responsesTable;
    @FXML private TableColumn<DisasterResponse, Integer>  respIdCol;
    @FXML private TableColumn<DisasterResponse, Integer>  respDisasterCol;
    @FXML private TableColumn<DisasterResponse, Integer>  respDeptCountCol;
    @FXML private TableColumn<DisasterResponse, Integer>  respPersonCountCol;
    @FXML private TableColumn<DisasterResponse, String>   respCreatedCol;

    /** Observable list for the responses summary table. */
    private final ObservableList<DisasterResponse> responsesData = FXCollections.observableArrayList();

    /**
     * Initialises the tab: populates the disaster ComboBox, configures
     * multi-selection on the lists, and sets up the responses table.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        // Allow multi-select in both lists (Ctrl/Cmd + click to select multiple)
        departmentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        responderListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Update the detail label when a disaster is selected
        disasterComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onDisasterSelected(newVal));

        // Configure responses summary table
        respIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        respDisasterCol.setCellValueFactory(new PropertyValueFactory<>("disasterId"));
        respDeptCountCol.setCellValueFactory(new PropertyValueFactory<>("departmentCount"));
        respPersonCountCol.setCellValueFactory(new PropertyValueFactory<>("responderCount"));
        respCreatedCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        responsesTable.setItems(responsesData);

        loadData();
    }

    /**
     * Refreshes all data from the repository.
     * Called by MainController when this tab becomes active.
     */
    public void refresh() {
        loadData();
    }

    /**
     * Handles the "Save Response" button.
     * Validates selection, creates a DisasterResponse, marks assigned
     * departments and responders as deployed, and saves everything.
     */
    @FXML
    private void handleSaveResponse() {
        DisasterReport selected = disasterComboBox.getValue();
        if (selected == null) {
            showError("Please select an active disaster first.");
            return;
        }

        List<Department> chosenDepts = departmentListView.getSelectionModel().getSelectedItems();
        List<Responder>  chosenResps = responderListView.getSelectionModel().getSelectedItems();

        if (chosenDepts.isEmpty() && chosenResps.isEmpty()) {
            showError("Select at least one department or responder.");
            return;
        }

        // Build and save the response
        DisasterResponse response = new DisasterResponse(
                0, selected, chosenDepts, chosenResps, notesArea.getText().trim());
        DisasterRepository.addResponse(response);

        // Mark assigned departments as deployed
        for (Department d : chosenDepts) {
            DisasterRepository.toggleDepartmentAvailability(d.getId());
        }

        // Mark assigned responders as deployed
        for (Responder r : chosenResps) {
            DisasterRepository.toggleResponderAvailability(r.getId());
        }

        // Update disaster status to RESPONDING if not already
        if (selected.getStatus() == DisasterStatus.ASSESSING) {
            DisasterRepository.updateReportStatus(selected.getId(), DisasterStatus.RESPONDING);
        }

        showSuccess("Response #" + response.getId() + " saved for Disaster #" + selected.getId());
        notesArea.clear();
        loadData();
    }

    /**
     * Updates the disaster detail label and refreshes available lists
     * when the user selects a disaster from the ComboBox.
     *
     * @param report the selected disaster report
     */
    private void onDisasterSelected(DisasterReport report) {
        if (report == null) {
            disasterDetailLabel.setText("No disaster selected.");
            return;
        }
        disasterDetailLabel.setText(String.format(
            "Type: %s | Severity: %s | Location: %s | Priority: %d",
            report.getType(), report.getSeverity(),
            report.getLocation(), report.getPriority()));

        // Refresh available lists whenever disaster selection changes
        departmentListView.setItems(
                FXCollections.observableArrayList(DisasterRepository.getAvailableDepartments()));
        responderListView.setItems(
                FXCollections.observableArrayList(DisasterRepository.getAvailableResponders()));
    }

    /** Loads all data from the repository into the UI controls. */
    private void loadData() {
        // Populate the disaster ComboBox with active (Assessing / Responding) disasters
        ObservableList<DisasterReport> activeReports =
                FXCollections.observableArrayList(DisasterRepository.getActiveReports());
        disasterComboBox.setItems(activeReports);

        if (!activeReports.isEmpty() && disasterComboBox.getValue() == null) {
            disasterComboBox.getSelectionModel().selectFirst();
        }

        // Populate department and responder lists with available options
        departmentListView.setItems(
                FXCollections.observableArrayList(DisasterRepository.getAvailableDepartments()));
        responderListView.setItems(
                FXCollections.observableArrayList(DisasterRepository.getAvailableResponders()));

        // Refresh the responses summary table
        responsesData.setAll(DisasterRepository.getAllResponses());
    }

    /** Displays a success message in green. */
    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText(message);
    }

    /** Displays an error message in red. */
    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }
}
