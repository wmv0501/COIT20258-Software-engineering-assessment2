package drs.controller;

import drs.model.DisasterReport;
import drs.model.DisasterRepository;
import drs.model.DisasterStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the "Assess and Prioritise" tab.
 * Displays all disaster reports sorted by priority.
 * Coordinators can update the status of a selected report and
 * override its automatically computed priority score.
 */
public class AssessDisasterController {

    /**
     * Observable list backing the reports table.
     */
    private final ObservableList<DisasterReport> tableData = FXCollections.observableArrayList();
    // --- Main table showing all reports ---
    @FXML
    private TableView<DisasterReport> reportsTable;
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
    private TableColumn<DisasterReport, String> reporterCol;
    @FXML
    private TableColumn<DisasterReport, String> timestampCol;
    // --- Detail panel for the selected report ---
    @FXML
    private Label detailIdLabel;
    @FXML
    private Label detailTypeLabel;
    @FXML
    private Label detailSeverityLabel;
    @FXML
    private Label detailLocationLabel;
    @FXML
    private Label detailReporterLabel;
    @FXML
    private Label detailDescLabel;
    @FXML
    private TextField priorityOverrideField;
    // --- Action buttons ---
    @FXML
    private Button startAssessmentBtn;
    @FXML
    private Button dispatchResponseBtn;
    @FXML
    private Button markResolvedBtn;
    @FXML
    private Button overridePriorityBtn;
    @FXML
    private Label statusLabel;
    /**
     * The report currently selected in the table.
     */
    private DisasterReport selectedReport;

    /**
     * Initialises the table columns and selection listener.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        // Wire columns to model getters
        idCol.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        typeCol.setCellValueFactory( new PropertyValueFactory<>( "type" ) );
        severityCol.setCellValueFactory( new PropertyValueFactory<>( "severity" ) );
        locationCol.setCellValueFactory( new PropertyValueFactory<>( "location" ) );
        priorityCol.setCellValueFactory( new PropertyValueFactory<>( "priority" ) );
        statusCol.setCellValueFactory( new PropertyValueFactory<>( "status" ) );
        reporterCol.setCellValueFactory( new PropertyValueFactory<>( "reporterName" ) );
        timestampCol.setCellValueFactory( new PropertyValueFactory<>( "timestamp" ) );

        reportsTable.setItems( tableData );

        // Update detail panel and enable buttons when a row is selected
        reportsTable.getSelectionModel().selectedItemProperty().addListener(
                ( obs, oldVal, newVal ) -> onReportSelected( newVal ) );

        disableActionButtons();
        loadReports();
    }

    /**
     * Refreshes the table from the repository.
     * Called by MainController when this tab becomes active.
     */
    public void refresh() {
        loadReports();
    }

    /**
     * Handles the "Start Assessment" button.
     * Changes the selected report's status to ASSESSING.
     */
    @FXML
    private void handleStartAssessment() {
        if ( selectedReport == null )
            return;
        DisasterRepository.updateReportStatus( selectedReport.getId(), DisasterStatus.ASSESSING );
        showSuccess( "Report #" + selectedReport.getId() + " is now being assessed." );
        loadReports();
    }

    /**
     * Handles the "Dispatch Response" button.
     * Changes the selected report's status to RESPONDING.
     */
    @FXML
    private void handleDispatchResponse() {
        if ( selectedReport == null )
            return;
        DisasterRepository.updateReportStatus( selectedReport.getId(), DisasterStatus.RESPONDING );
        showSuccess( "Response dispatched for Report #" + selectedReport.getId() + "." );
        loadReports();
    }

    /**
     * Handles the "Mark Resolved" button.
     * Changes the selected report's status to RESOLVED.
     */
    @FXML
    private void handleMarkResolved() {
        if ( selectedReport == null )
            return;
        DisasterRepository.updateReportStatus( selectedReport.getId(), DisasterStatus.RESOLVED );
        showSuccess( "Report #" + selectedReport.getId() + " marked as resolved." );
        loadReports();
    }

    /**
     * Handles the "Override Priority" button.
     * Validates the entered value and updates the priority in the repository.
     */
    @FXML
    private void handleOverridePriority() {
        if ( selectedReport == null )
            return;

        String input = priorityOverrideField.getText().trim();
        if ( input.isEmpty() ) {
            showError( "Enter a priority value first." );
            return;
        }

        try {
            int newPriority = Integer.parseInt( input );
            if ( newPriority < 1 || newPriority > 100 ) {
                showError( "Priority must be between 1 and 100." );
                return;
            }
            DisasterRepository.updateReportPriority( selectedReport.getId(), newPriority );
            showSuccess( "Priority updated to " + newPriority + " for Report #" + selectedReport.getId() );
            loadReports();
        } catch ( NumberFormatException e ) {
            showError( "Priority must be a whole number." );
        }
    }

    /**
     * Called when the user selects a row in the table.
     * Populates the detail panel and enables the appropriate action buttons.
     *
     * @param report the selected disaster report, or null if selection is cleared
     */
    private void onReportSelected( DisasterReport report ) {
        selectedReport = report;
        if ( report == null ) {
            disableActionButtons();
            clearDetails();
            return;
        }

        // Populate the detail labels
        detailIdLabel.setText( String.valueOf( report.getId() ) );
        detailTypeLabel.setText( report.getType().toString() );
        detailSeverityLabel.setText( report.getSeverity().toString() );
        detailLocationLabel.setText( report.getLocation() );
        detailReporterLabel.setText( report.getReporterName() + " — " + report.getReporterPhone() );
        detailDescLabel.setText( report.getDescription() );
        priorityOverrideField.setText( String.valueOf( report.getPriority() ) );

        // Enable/disable buttons based on current status
        DisasterStatus s = report.getStatus();
        startAssessmentBtn.setDisable( s != DisasterStatus.REPORTED );
        dispatchResponseBtn.setDisable( s != DisasterStatus.ASSESSING );
        markResolvedBtn.setDisable( s == DisasterStatus.RESOLVED );
        overridePriorityBtn.setDisable( false );
        statusLabel.setText( "" );
    }

    /**
     * Reloads report data from the repository into the table.
     */
    private void loadReports() {
        tableData.setAll( DisasterRepository.getAllReports() );

        // Re-select the same report if it still exists
        if ( selectedReport != null ) {
            for ( DisasterReport r : tableData ) {
                if ( r.getId() == selectedReport.getId() ) {
                    reportsTable.getSelectionModel().select( r );
                    break;
                }
            }
        }
    }

    /**
     * Disables all status-change action buttons.
     */
    private void disableActionButtons() {
        startAssessmentBtn.setDisable( true );
        dispatchResponseBtn.setDisable( true );
        markResolvedBtn.setDisable( true );
        overridePriorityBtn.setDisable( true );
    }

    /**
     * Clears all detail-panel labels.
     */
    private void clearDetails() {
        detailIdLabel.setText( "-" );
        detailTypeLabel.setText( "-" );
        detailSeverityLabel.setText( "-" );
        detailLocationLabel.setText( "-" );
        detailReporterLabel.setText( "-" );
        detailDescLabel.setText( "-" );
        priorityOverrideField.clear();
        statusLabel.setText( "" );
    }

    /**
     * Displays a success message in green on the status label.
     */
    private void showSuccess( String message ) {
        statusLabel.setStyle( "-fx-text-fill: green;" );
        statusLabel.setText( message );
    }

    /**
     * Displays an error message in red on the status label.
     */
    private void showError( String message ) {
        statusLabel.setStyle( "-fx-text-fill: red;" );
        statusLabel.setText( message );
    }
}
