import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Stack;
import java.util.List;

/**
 * Controller class for the main coloring interface.
 * This class manages the 16x16 pixel grid. implements the "color-by-number" logic,
 * handles undo functionality via a Command pattern, and syncs progress with the database.
 *
 * @author Color-Coded Coders
 * @version 1.0
 */
public class ColoringPageController {
   // FXML Injected UI Components
   @FXML private Button doneBtn, galleryBtn, undoBtn;
   @FXML private GridPane coloringGrid;
   @FXML private HBox palette;
   @FXML private VBox doneSticker;
   @FXML private Text pageTitle;
   @FXML private Text userNameLabel;
   
   // State Variables
   private int selectedNumber = 1;
   private Color selectedColor = Color.TRANSPARENT;
   private VBox selectedBucket = null;
   private int coloredCount = 0;
   private final int totalPixels = 16 * 16;
   private String savedName;
   private String currentPageTitle;
   
   /**
    * History stack used to store undoable actions.
    * Each Runnable contains the logic to revert a pixel's color state.
    */
   private Stack<Runnable> undoHistory = new Stack<>();
   
   /**
    * Initializes the controller class.
    * Clears existing grid and palette children and ensures the completion button is disabled.
    */
   @FXML
   public void initialize() {
      coloringGrid.getChildren().clear();
      palette.getChildren().clear();
      doneBtn.setDisable(true);
   }
   
   /**
    * Sets the active coloring page and updates the UI header and data.
    *
    * @param page The key name of the page (e.g., "House", "Dolphin").
    */
   public void setActivePage(String page) {
      this.currentPageTitle = page;
      switch (page) {
         case "House":      this.pageTitle.setText("The Cozy Cottage"); break;
         case "Flower":     this.pageTitle.setText("Spring Blossom"); break;
         case "Dolphin":    this.pageTitle.setText("Ocean Friend"); break;
         case "Mountain":   this.pageTitle.setText("Misty Morning Summit"); break;
         default:           this.pageTitle.setText(page); break;
      }
      loadGrid(getPageData(page));
      updatePalette(page);
   }
   
   /**
    * Populates the GridPane with interactive pixel cells.
    * Restores previously saved progress from the database upon loading.
    *
    * @param data The integer array representing the image pixel map.
    */
   public void loadGrid(int[] data) {
      coloringGrid.getChildren().clear();
      coloredCount = 0;
      undoHistory.clear();
      
      List<Integer> savedPixels = NameDatabase.getSavedPixels(this.savedName, currentPageTitle);
      
      for (int r = 0; r < 16; r++) {
         for (int c = 0; c < 16; c++) {
            int index = (r * 16) + c;
            int target = data[index];
            
            Rectangle pixel = new Rectangle(30, 30, Color.TRANSPARENT);
            pixel.setStrokeType(StrokeType.INSIDE);
            
            Text n = new Text(String.valueOf(target));
            n.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-weight: Bold; -fx-font-size: 11;");
            
            StackPane cell = new StackPane(pixel, n);
            cell.setPrefSize(30, 30);
            
            // Restore progress from DB
            if (savedPixels.contains(index)) {
               Color storedColor = getColorForNumber(currentPageTitle, target);
               pixel.setFill(storedColor);
               pixel.setStroke(storedColor);
               n.setVisible(false);
               coloredCount++;
            }
            
            cell.setOnMouseClicked(e -> handlePixelClick(pixel, n, target, index));
            coloringGrid.add(cell, c, r);
         }
      }
   }
   
   /**
    * Internal logic for coloring a pixel.
    * Validates if the selected color matches the pixel number and saves progress.
    */
   private void handlePixelClick(Rectangle pixel, Text n, int target, int index) {
      if (selectedNumber == target && !pixel.getFill().equals(selectedColor)) {
         Color oldColor = (Color) pixel.getFill();
         boolean wasTextVisible = n.isVisible();
         
         // Update UI state
         pixel.setFill(selectedColor);
         pixel.setStroke(selectedColor);
         n.setVisible(false);
         coloredCount++;
         
         if (coloredCount == totalPixels) enableDoneButton();
         
         // Store undo action
         undoHistory.push(() -> {
            pixel.setFill(oldColor);
            pixel.setStroke(Color.web("#000000", 0.08));
            n.setVisible(wasTextVisible);
            coloredCount--;
            doneBtn.setDisable(true);
         });
         
         // Update Database
         NameDatabase.savePixelProgress(this.savedName, this.currentPageTitle, index, (coloredCount == totalPixels));
      }
   }
   
   /**
    * Sets the user's name for database tracking and UI labeling.
    *
    * @param name The name of the current user.
    */
   public void setUserName(String name) {
      this.savedName = name;
      if (this.userNameLabel != null) {
         this.userNameLabel.setText(name);
      }
   }
   
   /**
    * Reverts the last coloring action performed by the user.
    */
   @FXML
   void handleUndo(ActionEvent event) {
      if (!undoHistory.isEmpty()) {
         undoHistory.pop().run();
      }
   }
   
   /**
    * Triggers the completion animation when the user finishes the drawing.
    */
   @FXML
   void handleDone(ActionEvent event) {
      doneSticker.setVisible(true);
      ScaleTransition st = new ScaleTransition(Duration.millis(500), doneSticker);
      st.setFromX(0); st.setFromY(0);
      st.setToX(1);   st.setToY(1);
      st.play();
   }
   
   /**
    * Transitions the user back to the gallery/selection page with a fade effect.
    */
   @FXML
   void handleGallery(ActionEvent event) throws IOException {
      Parent currentRoot = galleryBtn.getScene().getRoot();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      
      fadeOut.setOnFinished(e -> {
         try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectionPageFXML.fxml"));
             Parent root = loader.load();
             
             NavigationController controller = loader.getController();
             if (controller != null) {
                 controller.setUserName(this.savedName);
             }
             
             Stage stage = (Stage) galleryBtn.getScene().getWindow();
             stage.setScene(new Scene(root));
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      });
      fadeOut.play();
   }
   
   /**
    * Helper to enable and style the Done button upon completion.
    */
   private void enableDoneButton() {
      doneBtn.setDisable(false);
      doneBtn.setStyle(doneBtn.getStyle() + "-fx-opacity: 1.0; -fx-background-color: #C2DAB8;");
   }
   
   /**
    * Maps the image key to the actual static data array.
    */
   private int[] getPageData(String page) {
      return switch (page) {
         case "House" -> ColoringPageImage.House;
         case "Dolphin" -> ColoringPageImage.Dolphin;
         case "Flower" -> ColoringPageImage.Flower;
         case "Mountain" -> ColoringPageImage.Mountain;
         default -> ColoringPageImage.House;
      };
   }
   
   /**
    * Generates the color palette based on the current page's theme.
    */
   private void updatePalette(String page) {
      palette.getChildren().clear();
      if (page.equals("House")) {
         palette.getChildren().addAll(
            createPaintBucket(1, Color.web("#CEE5ED")), createPaintBucket(2, Color.web("#FAF9F6")),
            createPaintBucket(3, Color.web("#FFC4DA")), createPaintBucket(4, Color.web("#FDFCE8")),
            createPaintBucket(5, Color.web("#C2DAB8"))
         );
      } else if (page.equals("Dolphin")) {
         palette.getChildren().addAll(
            createPaintBucket(1, Color.web("#B0E0E6")), createPaintBucket(2, Color.web("#4682B4")),
            createPaintBucket(3, Color.web("#0000CD")), createPaintBucket(4, Color.web("#FFFFFF"))
         );
         
      } else if (page.equals("Flower")) {
         palette.getChildren().addAll(
            createPaintBucket(1, Color.web("#CEE5ED")), createPaintBucket(2, Color.web("#FFE2E1")),
            createPaintBucket(3, Color.web("#FFC0CB")), createPaintBucket(4, Color.web("#FFFFE0"))
         );
      } else if (page.equals("Mountain")) {
         palette.getChildren().addAll(
            createPaintBucket(1, Color.web("#B0E0E6")), createPaintBucket(2, Color.web("#FFFFFF")),
            createPaintBucket(3, Color.web("#B0C4DE")), createPaintBucket(4, Color.web("#778899"))
         );
      }
   }
   
   /**
    * Creates an interactive "Paint Bucket" UI element for the palette.
    */
   private VBox createPaintBucket(int num, Color color) {
      VBox box = new VBox(5);
      box.setAlignment(javafx.geometry.Pos.CENTER);
      Circle swatch = new Circle(22, color);
      swatch.setStroke(Color.web("#6B4E51"));
      Text label = new Text(String.valueOf(num));
      label.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-weight: Bold;");
      
      box.getChildren().addAll(swatch, label);
      box.setOnMouseClicked(e -> {
         if (selectedBucket != null) {
            selectedBucket.setStyle("");
            selectedBucket.setScaleX(1.0);
            selectedBucket.setScaleY(1.0);
         }
         this.selectedNumber = num;
         this.selectedColor = color;
         this.selectedBucket = box;
         box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 15;");
         box.setScaleX(1.1);
         box.setScaleY(1.1);
      });
      return box;
   }
   
   /**
     * Returns the specific Color object associated with a pixel number for a given page.
     * Required for restoring saved progress from the database.
     */
    private Color getColorForNumber(String page, int num) {
        return switch (page) {
            case "House" -> switch (num) {
                case 1 -> Color.web("#CEE5ED");
                case 2 -> Color.web("#FAF9F6");
                case 3 -> Color.web("#FFC4DA");
                case 4 -> Color.web("#FDFCE8");
                case 5 -> Color.web("#C2DAB8");
                default -> Color.TRANSPARENT;
            };
            case "Dolphin" -> switch (num) {
                case 1 -> Color.web("#B0E0E6");
                case 2 -> Color.web("#4682B4");
                case 3 -> Color.web("#0000CD");
                case 4 -> Color.web("#FFFFFF");
                default -> Color.TRANSPARENT;
            };
            case "Flower" -> switch (num) {
               case 1 -> Color.web("#CEE5ED");
               case 2 -> Color.web("#FFE4E1");
               case 3 -> Color.web("#FFC0CB");
               case 4 -> Color.web("#FFFFE0");
               default -> Color.TRANSPARENT;
            };
            case "Mountain" -> switch (num) {
               case 1 -> Color.web("#B0E0E6");
               case 2 -> Color.web("#FFFFFF");
               case 3 -> Color.web("#B0C4DE");
               case 4 -> Color.web("#778899");
               default -> Color.TRANSPARENT;
            };
            default -> Color.TRANSPARENT;
        };
     }
}