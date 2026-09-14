package drs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Controller for the main window (main.fxml).
 * Its sole responsibility is to refresh the sub-controller of whichever
 * tab the user switches to, ensuring each screen always shows current data.
 *
 * Sub-controllers are injected by JavaFX using the fx:id + "Controller" naming
 * convention defined by the fx:include elements in main.fxml.
 */
public class MainController {

    @FXML private TabPane mainTabPane;

    // Injected sub-controllers — names match fx:id + "Controller"
    @FXML private ReportDisasterController  reportDisasterController;
    @FXML private AssessDisasterController  assessDisasterController;
    @FXML private CoordinationController    coordinationController;
    @FXML private HistoryController         historyController;
    @FXML private ResourceController        resourceController;

    /**
     * Sets up a tab-change listener that calls refresh() on the newly active tab's
     * controller so it always shows up-to-date data from the repository.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    if (newTab == null) return;
                    int index = mainTabPane.getTabs().indexOf(newTab);
                    refreshTab(index);
                });
    }

    /**
     * Delegates the refresh call to the appropriate sub-controller
     * based on the tab index.
     *
     * Tab order (matches main.fxml):
     *   0 — Report Disaster
     *   1 — Assess and Prioritise
     *   2 — Coordinate Response
     *   3 — History Log
     *   4 — Resources
     *
     * @param index the zero-based index of the tab that just became active
     */
    private void refreshTab(int index) {
        switch (index) {
            case 0 -> reportDisasterController.loadRecentReports();
            case 1 -> assessDisasterController.refresh();
            case 2 -> coordinationController.refresh();
            case 3 -> historyController.refresh();
            case 4 -> resourceController.refresh();
            default -> { /* no action for unknown tabs */ }
        }
    }
}
