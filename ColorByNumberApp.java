import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the My Coloring Book: color by number! application.
 * This class orchestrates the application lifecycle, including database
 * initialization and the primary Stage configuration.
 *
 * @author Color-Coded Coders
 * @version 1.0
 */
public class ColorByNumberApp extends Application {
   
   /**
    * Sets up the application database and loads the initial Cover Page.
    *
    * This method overrides the standard JavaFX start method to ensure
    * the SQLite database is ready before any UI controllers attempt to
    * access user data.
    *
    * @param primaryStage The primary stage for this application.
    * @throws Exception If the FXML resource "CoverPageFXML.fxml" cannot be loaded.
    */
    @Override
    public void start (Stage primaryStage) throws Exception {
        // Initialize the database to ensure tables exist before UI interaction
        NameDatabase.initialize();
        
        // Load the initial layout
        Parent root = FXMLLoader.load(getClass().getResource("CoverPageFXML.fxml"));
        
        // Configure and display the Primary Stage
        primaryStage.setTitle("My Coloring Book: color by numbers!");
        primaryStage.setScene(new Scene(root, 600, 750));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * Standard main method to launch the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}