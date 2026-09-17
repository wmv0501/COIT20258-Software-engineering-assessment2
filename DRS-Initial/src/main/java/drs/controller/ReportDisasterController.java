package drs.controller;

import drs.model.DisasterReport;
import drs.model.DisasterRepository;
import drs.model.DisasterType;
import drs.model.Severity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the "Report Disaster" tab.
 * Handles user input from the disaster report form, validates fields,
 * creates a DisasterReport and saves it to the repository,
 * and refreshes the recent-reports table.
 */
public class ReportDisasterController {

    /**
     * Observable list backing the recent-reports table.
     */
    private final ObservableList<DisasterReport> tableData = FXCollections.observableArrayList();
    // --- Form fields ---
    @FXML
    private ComboBox<DisasterType> typeComboBox;
    @FXML
    private ComboBox<Severity> severityComboBox;
    @FXML
    private TextField locationField;
    @FXML
    private TextField reporterNameField;
    @FXML
    private TextField reporterPhoneField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label statusLabel;
    // --- Recent-reports table ---
    @FXML
    private TableView<DisasterReport> recentReportsTable;
    @FXML
    private TableColumn<DisasterReport, Integer> idCol;
    @FXML
    private TableColumn<DisasterReport, String> typeCol;
    @FXML
    private TableColumn<DisasterReport, String> severityCol;
    @FXML
    private TableColumn<DisasterReport, String> locationCol;
    @FXML
    private TableColumn<DisasterReport, Integer> priorityCol;
    @FXML
    private TableColumn<DisasterReport, String> statusCol;
    @FXML
    private TableColumn<DisasterReport, String> timestampCol;

    /**
     * Initialises the form by populating ComboBoxes and configuring the table.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        // Populate drop-downs with all enum values
        typeComboBox.setItems( FXCollections.observableArrayList( DisasterType.values() ) );
        severityComboBox.setItems( FXCollections.observableArrayList( Severity.values() ) );

        // Set default selections
        typeComboBox.getSelectionModel().selectFirst();
        severityComboBox.getSelectionModel().selectFirst();

        // Wire table columns to model properties via reflection-based PropertyValueFactory
        idCol.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        typeCol.setCellValueFactory( new PropertyValueFactory<>( "type" ) );
        severityCol.setCellValueFactory( new PropertyValueFactory<>( "severity" ) );
        locationCol.setCellValueFactory( new PropertyValueFactory<>( "location" ) );
        priorityCol.setCellValueFactory( new PropertyValueFactory<>( "priority" ) );
        statusCol.setCellValueFactory( new PropertyValueFactory<>( "status" ) );
        timestampCol.setCellValueFactory( new PropertyValueFactory<>( "timestamp" ) );

        recentReportsTable.setItems( tableData );

        // Load any reports already in the repository (e.g., on tab re-visit)
        loadRecentReports();
    }

    /**
     * Handles the "Submit Report" button action.
     * Validates required fields, creates a DisasterReport, saves it,
     * and updates the table and status label.
     */
    @FXML
    private void handleSubmit() {
        // Validate that all required fields are filled
        if ( !validateForm() ) {
            return;
        }

        // Build the report from form inputs
        DisasterType type = typeComboBox.getValue();
        Severity severity = severityComboBox.getValue();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        String reporterName = reporterNameField.getText().trim();
        String reporterPhone = reporterPhoneField.getText().trim();

        DisasterReport report = new DisasterReport(
                0, type, severity, location, description, reporterName, reporterPhone );

        // Persist the report (repository assigns the ID)
        DisasterRepository.addReport( report );

        // Provide feedback and refresh the table
        statusLabel.setStyle( "-fx-text-fill: green;" );
        statusLabel.setText(
                "Report #" + report.getId() + " submitted successfully. Priority: " + report.getPriority() );
        loadRecentReports();
        clearForm();
    }

    /**
     * Handles the "Clear" button action.
     * Resets all form fields to their default state.
     */
    @FXML
    private void handleClear() {
        clearForm();
        statusLabel.setText( "" );
    }

    /**
     * Validates that all required form fields contain non-empty values.
     * Displays an error message via the status label if validation fails.
     *
     * @return true if all required fields are filled; false otherwise
     */
    private boolean validateForm() {
        if ( typeComboBox.getValue() == null ) {
            showError( "Please select a disaster type." );
            return false;
        }
        if ( severityComboBox.getValue() == null ) {
            showError( "Please select a severity level." );
            return false;
        }
        if ( locationField.getText().trim().isEmpty() ) {
            showError( "Location is required." );
            return false;
        }
        if ( reporterNameField.getText().trim().isEmpty() ) {
            showError( "Reporter name is required." );
            return false;
        }
        if ( reporterPhoneField.getText().trim().isEmpty() ) {
            showError( "Reporter phone is required." );
            return false;
        }
        if ( descriptionArea.getText().trim().isEmpty() ) {
            showError( "Description is required." );
            return false;
        }
        return true;
    }

    /**
     * Reloads the recent-reports table from the repository.
     * Called after a new submission or on tab selection.
     */
    public void loadRecentReports() {
        tableData.setAll( DisasterRepository.getAllReports() );
    }

    /**
     * Resets all form fields and ComboBox selections to defaults.
     */
    private void clearForm() {
        typeComboBox.getSelectionModel().selectFirst();
        severityComboBox.getSelectionModel().selectFirst();
        locationField.clear();
        reporterNameField.clear();
        reporterPhoneField.clear();
        descriptionArea.clear();
    }

    /**
     * Displays an error message in red on the status label.
     */
    private void showError( String message ) {
        statusLabel.setStyle( "-fx-text-fill: red;" );
        statusLabel.setText( message );
    }
}
