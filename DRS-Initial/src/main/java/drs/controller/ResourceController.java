package drs.controller;

import drs.model.Department;
import drs.model.DisasterRepository;
import drs.model.Responder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the "Resources" tab (Creative Feature 2).
 * Displays department and responder availability, and lets coordinators
 * toggle resources between Available and Deployed status.
 * Summary statistics are shown at the top of the screen.
 */
public class ResourceController {

    /**
     * Observable lists backing the two tables.
     */
    private final ObservableList<Department> deptData = FXCollections.observableArrayList();
    private final ObservableList<Responder> respData = FXCollections.observableArrayList();
    // --- Summary stats labels ---
    @FXML
    private Label deptAvailableLabel;
    @FXML
    private Label deptDeployedLabel;
    @FXML
    private Label respAvailableLabel;
    @FXML
    private Label respDeployedLabel;
    // --- Departments table ---
    @FXML
    private TableView<Department> deptTable;
    @FXML
    private TableColumn<Department, Integer> deptIdCol;
    @FXML
    private TableColumn<Department, String> deptNameCol;
    @FXML
    private TableColumn<Department, String> deptTypeCol;
    @FXML
    private TableColumn<Department, String> deptContactCol;
    @FXML
    private TableColumn<Department, String> deptPhoneCol;
    @FXML
    private TableColumn<Department, String> deptStatusCol;
    @FXML
    private Button toggleDeptBtn;
    @FXML
    private Label deptStatusLabel;
    // --- Responders table ---
    @FXML
    private TableView<Responder> respTable;
    @FXML
    private TableColumn<Responder, Integer> respIdCol;
    @FXML
    private TableColumn<Responder, String> respNameCol;
    @FXML
    private TableColumn<Responder, String> respRoleCol;
    @FXML
    private TableColumn<Responder, String> respDeptCol;
    @FXML
    private TableColumn<Responder, String> respPhoneCol;
    @FXML
    private TableColumn<Responder, String> respStatusCol;
    @FXML
    private Button toggleRespBtn;
    @FXML
    private Label respStatusLabel;

    /**
     * Initialises both tables and configures their columns.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        // Configure department table columns
        deptTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        deptIdCol.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        deptNameCol.setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        deptTypeCol.setCellValueFactory( new PropertyValueFactory<>( "type" ) );
        deptContactCol.setCellValueFactory( new PropertyValueFactory<>( "contactPerson" ) );
        deptPhoneCol.setCellValueFactory( new PropertyValueFactory<>( "phone" ) );
        deptStatusCol.setCellValueFactory( new PropertyValueFactory<>( "availabilityStatus" ) );

        deptTable.setItems( deptData );

        // Configure responder table columns
        respTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        respIdCol.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        respNameCol.setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        respRoleCol.setCellValueFactory( new PropertyValueFactory<>( "role" ) );
        respDeptCol.setCellValueFactory( new PropertyValueFactory<>( "departmentName" ) );
        respPhoneCol.setCellValueFactory( new PropertyValueFactory<>( "phone" ) );
        respStatusCol.setCellValueFactory( new PropertyValueFactory<>( "availabilityStatus" ) );

        respTable.setItems( respData );

        // Enable toggle buttons only when a row is selected
        toggleDeptBtn.setDisable( true );
        toggleRespBtn.setDisable( true );

        deptTable.getSelectionModel().selectedItemProperty().addListener(
                ( obs, oldVal, newVal ) -> toggleDeptBtn.setDisable( newVal == null ) );
        respTable.getSelectionModel().selectedItemProperty().addListener(
                ( obs, oldVal, newVal ) -> toggleRespBtn.setDisable( newVal == null ) );

        loadData();
    }

    /**
     * Refreshes both tables and statistics from the repository.
     * Called by MainController when this tab becomes active.
     */
    public void refresh() {
        loadData();
    }

    /**
     * Handles the "Toggle Availability" button for departments.
     * Flips the availability status of the selected department.
     */
    @FXML
    private void handleToggleDepartment() {
        Department selected = deptTable.getSelectionModel().getSelectedItem();
        if ( selected == null )
            return;

        DisasterRepository.toggleDepartmentAvailability( selected.getId() );
        String newStatus = selected.isAvailable() ? "Available" : "Deployed";
        deptStatusLabel.setStyle( "-fx-text-fill: green;" );
        deptStatusLabel.setText( selected.getName() + " → " + newStatus );
        loadData();
    }

    /**
     * Handles the "Toggle Availability" button for responders.
     * Flips the availability status of the selected responder.
     */
    @FXML
    private void handleToggleResponder() {
        Responder selected = respTable.getSelectionModel().getSelectedItem();
        if ( selected == null )
            return;

        DisasterRepository.toggleResponderAvailability( selected.getId() );
        String newStatus = selected.isAvailable() ? "Available" : "Deployed";
        respStatusLabel.setStyle( "-fx-text-fill: green;" );
        respStatusLabel.setText( selected.getName() + " → " + newStatus );
        loadData();
    }

    /**
     * Reloads all departments and responders from the repository and updates stats.
     */
    private void loadData() {
        deptData.setAll( DisasterRepository.getAllDepartments() );
        respData.setAll( DisasterRepository.getAllResponders() );
        updateStats();
    }

    /**
     * Computes and displays the availability statistics for both resource types.
     */
    private void updateStats() {
        long deptAvail = deptData.stream().filter( Department::isAvailable ).count();
        long deptDeployed = deptData.size() - deptAvail;
        long respAvail = respData.stream().filter( Responder::isAvailable ).count();
        long respDeployed = respData.size() - respAvail;

        deptAvailableLabel.setText( "Available: " + deptAvail );
        deptDeployedLabel.setText( "Deployed: " + deptDeployed );
        respAvailableLabel.setText( "Available: " + respAvail );
        respDeployedLabel.setText( "Deployed: " + respDeployed );
    }
}
