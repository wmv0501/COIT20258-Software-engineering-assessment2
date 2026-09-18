package drs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import drs.model.Department;
import drs.model.DisasterReport;
import drs.model.DisasterRepository;
import drs.model.DisasterResponse;
import drs.model.DisasterStatus;
import drs.model.Responder;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

/**
 * Controller for the "Coordinate Response" tab.
 * Allows a coordinator to select an active disaster, choose departments
 * and responders to assign, enter notes, and save a DisasterResponse record.
 * Assigned departments and responders are marked as Deployed in the repository.
 */
public class CoordinationController {

    /**
     * Observable list for the responses summary table.
     */
    private final ObservableList<DisasterResponse> responsesData = FXCollections.observableArrayList();
    // --- Disaster selection ---
    @FXML
    private ComboBox<DisasterReport> disasterComboBox;
    @FXML
    private Label disasterDetailLabel;
    // --- Department selection list ---
    @FXML
    private ListView<Department> departmentListView;
    // --- Responder selection list ---
    @FXML
    private ListView<Responder> responderListView;
    // --- Response activity checkboxes ---
    @FXML
    private CheckBox chkWarningEvacuation;
    @FXML
    private CheckBox chkSearchRescue;
    @FXML
    private CheckBox chkImmediateAssistance;
    @FXML
    private CheckBox chkAssessingDamage;
    @FXML
    private CheckBox chkContinuingAssistance;
    @FXML
    private CheckBox chkRestoration;
    // --- Extra notes ---
    @FXML
    private TextArea notesArea;
    // --- Save button and feedback label ---
    @FXML
    private Button saveResponseBtn;
    @FXML
    private Label statusLabel;
    // --- Current assignments table ---
    @FXML
    private TableView<DisasterResponse> responsesTable;
    @FXML
    private TableColumn<DisasterResponse, Integer> respIdCol;
    @FXML
    private TableColumn<DisasterResponse, Integer> respDisasterCol;
    @FXML
    private TableColumn<DisasterResponse, Integer> respDeptCountCol;
    @FXML
    private TableColumn<DisasterResponse, Integer> respPersonCountCol;
    @FXML
    private TableColumn<DisasterResponse, String> respCreatedCol;

    /**
     * Initialises the tab: populates the disaster ComboBox, configures
     * multi-selection on the lists, and sets up the responses table.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        // Allow multi-select in both lists (Ctrl/Cmd + click to select multiple)
        departmentListView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        responderListView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

        // Display readable labels instead of the raw toString() of each model object
        configureDisplayFormatting();

        // When department selection changes, filter responders to only those departments
        departmentListView.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<Department>) change -> updateResponderList() );

        // Update the detail label when a disaster is selected
        disasterComboBox.getSelectionModel().selectedItemProperty().addListener(
                ( obs, oldVal, newVal ) -> onDisasterSelected( newVal ) );

        // Configure responses summary table
        respIdCol.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        respDisasterCol.setCellValueFactory( new PropertyValueFactory<>( "disasterId" ) );
        respDeptCountCol.setCellValueFactory( new PropertyValueFactory<>( "departmentCount" ) );
        respPersonCountCol.setCellValueFactory( new PropertyValueFactory<>( "responderCount" ) );
        respCreatedCol.setCellValueFactory( new PropertyValueFactory<>( "createdAt" ) );

        responsesTable.setItems( responsesData );

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
     * Configures how model objects are rendered in the ComboBox and the two
     * ListViews. Without this, JavaFX falls back to each object's toString(),
     * which is written for logging and is not suitable for the user interface.
     */
    private void configureDisplayFormatting() {
        // Disaster ComboBox — show a concise incident summary
        disasterComboBox.setConverter( new StringConverter<DisasterReport>() {
            @Override
            public String toString( DisasterReport report ) {
                return report == null ? "" : formatDisaster( report );
            }

            @Override
            public DisasterReport fromString( String text ) {
                return null;    // the ComboBox is not editable
            }
        } );

        // Department list — show name and category
        departmentListView.setCellFactory( list -> new ListCell<Department>() {
            @Override
            protected void updateItem( Department department, boolean empty ) {
                super.updateItem( department, empty );
                setText( empty || department == null ? null : formatDepartment( department ) );
            }
        } );

        // Responder list — show name, role and parent department
        responderListView.setCellFactory( list -> new ListCell<Responder>() {
            @Override
            protected void updateItem( Responder responder, boolean empty ) {
                super.updateItem( responder, empty );
                setText( empty || responder == null ? null : formatResponder( responder ) );
            }
        } );
    }

    /**
     * Formats a disaster report for display in the selection ComboBox.
     *
     * @param report the report to format
     * @return a concise single-line summary
     */
    private String formatDisaster( DisasterReport report ) {
        return String.format( "#%d  %s (%s) — %s  [priority %d]",
                report.getId(), report.getType(), report.getSeverity(),
                report.getLocation(), report.getPriority() );
    }

    /**
     * Formats a department for display in the department ListView.
     *
     * @param department the department to format
     * @return a readable single-line label
     */
    private String formatDepartment( Department department ) {
        return String.format( "%s  (%s)", department.getName(), department.getType() );
    }

    /**
     * Formats a responder for display in the responder ListView.
     *
     * @param responder the responder to format
     * @return a readable single-line label
     */
    private String formatResponder( Responder responder ) {
        return String.format( "%s — %s, %s",
                responder.getName(), responder.getRole(), responder.getDepartmentName() );
    }

    /**
     * Handles the "Save Response" button.
     * Validates selection, creates a DisasterResponse, marks assigned
     * departments and responders as deployed, and saves everything.
     */
    @FXML
    private void handleSaveResponse() {
        DisasterReport selected = disasterComboBox.getValue();
        if ( selected == null ) {
            showError( "Please select an active disaster first." );
            return;
        }

        // Copy the live selection lists — they are cleared when the views reload
        List<Department> chosenDepts =
                new ArrayList<>( departmentListView.getSelectionModel().getSelectedItems() );
        List<Responder> chosenResps =
                new ArrayList<>( responderListView.getSelectionModel().getSelectedItems() );

        if ( chosenDepts.isEmpty() && chosenResps.isEmpty() ) {
            showError( "Select at least one department or responder." );
            return;
        }

        // Build and save the response
        DisasterResponse response = new DisasterResponse(
                0, selected, chosenDepts, chosenResps, buildNotes() );
        DisasterRepository.addResponse( response );

        // Mark assigned departments as deployed.
        // markDepartmentDeployed is used rather than a toggle so that assigning
        // the same department to a second incident cannot flip it back to Available.
        for ( Department d : chosenDepts ) {
            DisasterRepository.markDepartmentDeployed( d.getId() );
        }

        // Mark assigned responders as deployed
        for ( Responder r : chosenResps ) {
            DisasterRepository.markResponderDeployed( r.getId() );
        }

        // Update disaster status to RESPONDING if not already
        if ( selected.getStatus() == DisasterStatus.ASSESSING ) {
            DisasterRepository.updateReportStatus( selected.getId(), DisasterStatus.RESPONDING );
        }

        showSuccess( "Response #" + response.getId() + " saved for Disaster #" + selected.getId() );
        clearActivities();
        loadData();
    }

    /**
     * Updates the disaster detail label and refreshes available lists
     * when the user selects a disaster from the ComboBox.
     *
     * @param report the selected disaster report
     */
    private void onDisasterSelected( DisasterReport report ) {
        if ( report == null ) {
            disasterDetailLabel.setText( "No disaster selected." );
            return;
        }
        disasterDetailLabel.setText( String.format(
                "Type: %s | Severity: %s | Location: %s | Priority: %d",
                report.getType(), report.getSeverity(),
                report.getLocation(), report.getPriority() ) );

        // Refresh department list; responder list updates via the selection listener
        departmentListView.setItems(
                FXCollections.observableArrayList( DisasterRepository.getDepartmentsWithAvailableResponders() ) );
        updateResponderList();
    }

    /**
     * Loads all data from the repository into the UI controls.
     */
    private void loadData() {
        // Populate the disaster ComboBox with active (Assessing / Responding) disasters
        ObservableList<DisasterReport> activeReports =
                FXCollections.observableArrayList( DisasterRepository.getActiveReports() );
        disasterComboBox.setItems( activeReports );

        if ( !activeReports.isEmpty() && disasterComboBox.getValue() == null ) {
            disasterComboBox.getSelectionModel().selectFirst();
        }

        // Populate department list; responder list is derived from department selection
        departmentListView.setItems(
                FXCollections.observableArrayList( DisasterRepository.getDepartmentsWithAvailableResponders() ) );
        updateResponderList();

        // Refresh the responses summary table
        responsesData.setAll( DisasterRepository.getAllResponses() );
    }

    /**
     * Builds the notes string from selected activity checkboxes and the extra notes field.
     * Checked activities are listed first, followed by any free-text notes.
     *
     * @return combined notes string saved to the DisasterResponse
     */
    private String buildNotes() {
        StringBuilder sb = new StringBuilder();

        if ( chkWarningEvacuation.isSelected() )
            sb.append( "- Warning / Evacuation\n" );
        if ( chkSearchRescue.isSelected() )
            sb.append( "- Search and Rescue\n" );
        if ( chkImmediateAssistance.isSelected() )
            sb.append( "- Providing Immediate Assistance\n" );
        if ( chkAssessingDamage.isSelected() )
            sb.append( "- Assessing Damage\n" );
        if ( chkContinuingAssistance.isSelected() )
            sb.append( "- Continuing Assistance\n" );
        if ( chkRestoration.isSelected() )
            sb.append( "- Restoration / Infrastructure\n" );

        String extra = notesArea.getText().trim();
        if ( !extra.isEmpty() ) {
            if ( sb.length() > 0 )
                sb.append( "\n" );
            sb.append( extra );
        }

        return sb.toString().trim();
    }

    /**
     * Clears all activity checkboxes and the extra notes field.
     */
    private void clearActivities() {
        chkWarningEvacuation.setSelected( false );
        chkSearchRescue.setSelected( false );
        chkImmediateAssistance.setSelected( false );
        chkAssessingDamage.setSelected( false );
        chkContinuingAssistance.setSelected( false );
        chkRestoration.setSelected( false );
        notesArea.clear();
    }

    /**
     * Refreshes the responder list based on the currently selected departments.
     * If no departments are selected, all available responders are shown.
     * If one or more departments are selected, only responders belonging to
     * those departments are shown.
     */
    private void updateResponderList() {
        List<Department> selectedDepts = departmentListView.getSelectionModel().getSelectedItems();
        List<Responder> availableResponders = DisasterRepository.getAvailableResponders();

        List<Responder> filtered;
        if ( selectedDepts.isEmpty() ) {
            filtered = availableResponders;
        } else {
            filtered = availableResponders.stream()
                               .filter( r -> selectedDepts.contains( r.getDepartment() ) )
                               .collect( Collectors.toList() );
        }
        responderListView.setItems( FXCollections.observableArrayList( filtered ) );
    }

    /**
     * Displays a success message in green.
     */
    private void showSuccess( String message ) {
        statusLabel.setStyle( "-fx-text-fill: green;" );
        statusLabel.setText( message );
    }

    /**
     * Displays an error message in red.
     */
    private void showError( String message ) {
        statusLabel.setStyle( "-fx-text-fill: red;" );
        statusLabel.setText( message );
    }
}
