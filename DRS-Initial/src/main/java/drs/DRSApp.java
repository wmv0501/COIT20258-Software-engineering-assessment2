package drs;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Disaster Response System (DRS) application.
 * Loads the main FXML layout and displays the primary stage.
 *
 * Run from IntelliJ via the Run configuration, or from the command line with:
 * mvn javafx:run
 */
public class DRSApp extends Application {

    /**
     * Window title displayed in the title bar.
     */
    private static final String WINDOW_TITLE = "Disaster Response System — DRS-Initial";

    /**
     * Preferred window width in pixels.
     */
    private static final double WINDOW_WIDTH = 1000.0;

    /**
     * Preferred window height in pixels.
     */
    private static final double WINDOW_HEIGHT = 720.0;

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main( String[] args ) {
        launch( args );
    }

    /**
     * JavaFX application start method.
     * Loads main.fxml, creates the scene, and shows the primary stage.
     *
     * @param primaryStage the primary window provided by the JavaFX runtime
     * @throws IOException if main.fxml cannot be loaded
     */
    @Override
    public void start( Stage primaryStage ) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource( "/drs/main.fxml" ) );

        Scene scene = new Scene( loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT );

        primaryStage.setTitle( WINDOW_TITLE );
        primaryStage.setScene( scene );
        primaryStage.setMinWidth( 800 );
        primaryStage.setMinHeight( 600 );
        primaryStage.show();
    }
}
