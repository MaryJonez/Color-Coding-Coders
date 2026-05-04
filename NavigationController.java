import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.animation.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

/**
 * Controller class for managing navigation and user interactions within the
 * Coloring Book application. Handles scene transitions, user registration,
 * and gallery animations.
 *
 * @author Color-Coded Coders
 * @version 1.0
 */
public class NavigationController {
   // --- FXML UI Components
   @FXML private VBox customAlert, nameTag;
   @FXML private Text alertMessage, nameText, userNameLabel;
   @FXML private TextField nameField;
   @FXML private Button signButton, openButton;
   
   @FXML private ImageView imageOne, imageTwo, imageThree, imageFour;
   @FXML private Pane decorPane, decor;
   @FXML private StackPane cardOne, cardTwo, cardThree, cardFour;
   
   @FXML private Button selectOneButton, selectTwoButton, selectThreeButton, selectFourButton;
   @FXML private Text numText1, numText2, numText3, numText4;
   @FXML private Text label1, label2, label3, label4;
   
   // --- State Variables ---
   private String name = "Guest";
   private String savedName;
   
   // Theme Colors
   private final String PINK_BRIGHT = "#FFB7B2";
   private final String BLUE_FILL = "#BCD8EC";
   private final String LIME_ACCENT = "#D4E157";
   
   /**
    * Initializes the controller. Sets up the initial UI state, loads gallery
    * images from the Model, and triggers animations.
    */
   @FXML
   public void initialize() {
      // Add decorative background elements if panes exist
      if (decorPane != null) addConfetti(decorPane, 15);
      if (decor != null) addConfetti(decor, 25);
      
      // Setup Gallery if images are present
      if (imageOne != null) {
         loadGalleryImages();
         startGalleryAnimations();
      }
      
      resetOverlays();
      
      // Check for existing user in database
      String existingUser = NameDatabase.getLatestName();
      if (existingUser != null && !existingUser.equals("Guest")) {
         this.name = existingUser;
         autoFillUser(existingUser);
      }
   }
   
   /**
    * Generates randomized decorative circles (confetti) on the background.
    *
    * @param pane The pane to add confetti to.
    * @param count The number of confetti pieces to generate.
    */
   private void addConfetti(Pane pane, int count) {
      String[] colors = {PINK_BRIGHT, BLUE_FILL, LIME_ACCENT, "#FFFFFF"};
      java.util.Random rand = new java.util.Random();
      
      for (int i = 0; i < count; i++) {
         Circle dot = new Circle(rand.nextInt(8) + 4, Color.web(colors[rand.nextInt(colors.length)], 0.4));
         dot.setCenterX(rand.nextInt(600));
         dot.setCenterY(rand.nextInt(750));
         pane.getChildren().add(dot);
      }
   }
   
   /**
    * Automatically transitions the UI if a name is already found in the DB.
    *
    * @param name The user's name retrieved from the database.
    */
   private void autoFillUser(String name) {
      this.name = name;
      if (nameText != null) nameText.setText(name);
      
      // Hide entry field and sign button
      if (nameField != null) {
          nameField.setVisible(false);
          nameField.setManaged(false);
      }
      if (signButton != null) {
          signButton.setVisible(false);
          signButton.setManaged(false);
      }  
      
      // Show name tag and enable entry
      if (nameTag != null) {
          nameTag.setVisible(true);
          nameTag.setManaged(true);
      }
      if (openButton != null) openButton.setDisable(false);
   }
   
   /**
    * Resets the visibility and managed state of UI overlays like alerts and name tags.
    */
   private void resetOverlays() {
      if (nameTag != null) {
          nameTag.setVisible(false);
          nameTag.setManaged(false);
      }
      if (customAlert != null) {
          customAlert.setVisible(false);
          customAlert.setManaged(false);
      }
   }
   
   /**
    * Sets the user's name for display and session tracking.
    *
    * @param name The user's name.
    */
   public void setUserName(String name) {
      this.savedName = name;
      if (this.userNameLabel != null) {
          this.userNameLabel.setText(name);
      }
   }
   
   /**
    * Loads category images into the gallery view using paths defined in the Model.
    */
   private void loadGalleryImages() {
      try {
          imageOne.setImage(new Image(getClass().getResourceAsStream(ColoringPageImage.IMAGE_PATHS[0])));
          imageTwo.setImage(new Image(getClass().getResourceAsStream(ColoringPageImage.IMAGE_PATHS[1])));
          imageThree.setImage(new Image(getClass().getResourceAsStream(ColoringPageImage.IMAGE_PATHS[2])));
          imageFour.setImage(new Image(getClass().getResourceAsStream(ColoringPageImage.IMAGE_PATHS[3])));
      } catch (Exception e) {
         System.err.println("Error loading gallery images: " + e.getMessage());
      }
   }
   
   /**
    * Triggers floating animations and hover effects for the gallery cards.
    */
   private void startGalleryAnimations() {
      addFloatingAnimation(cardOne, 0.0);
      addFloatingAnimation(cardTwo, 0.5);
      addFloatingAnimation(cardThree, 0.2);
      addFloatingAnimation(cardFour, 0.7);
      
      addHoverEffects(cardOne, numText1, label1, ColoringPageImage.THEME_COLORS[0]);
      addHoverEffects(cardTwo, numText2, label2, ColoringPageImage.THEME_COLORS[1]);
      addHoverEffects(cardThree, numText3, label3, ColoringPageImage.THEME_COLORS[2]);
      addHoverEffects(cardFour, numText4, label4, ColoringPageImage.THEME_COLORS[3]);
   }
   
   /**
    * Handles general navigation between pages with a fade transition.
    *
    * @param event The ActionEvent triggerd by the navigation button.
    * @throws IOException If the FXML file cannot be loaded.
    */
   @FXML
   void handleNavigation(ActionEvent event) throws IOException {
      Button clicked = (Button) event.getSource();
      String fxmlFile = clicked.getText().equals("Open the Book!") ?
         "InstructionsPageFXML.fxml" : "SelectionPageFXML.fxml";
      
      Parent currentRoot = clicked.getScene().getRoot();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(800), currentRoot);
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      
      fadeOut.setOnFinished(e -> {
         try {
             Parent nextRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
             Stage stage = (Stage) clicked.getScene().getWindow();
             
             nextRoot.setOpacity(0.0);
             stage.getScene().setRoot(nextRoot);
             
             FadeTransition fadeIn = new FadeTransition(Duration.millis(800), nextRoot);
             fadeIn.setToValue(1.0);
             fadeIn.play();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      });
      fadeOut.play();
   }
   
   /**
    * Validates and saves the user's name to the Database Model and updates the UI.
    *
    * @param event The ActionEvent triggered by the sign button.
    */
   @FXML
   void handleSignButton(ActionEvent event) {
      String nameInput = nameField.getText().trim();
      
      if (nameInput.isEmpty() || !nameInput.matches("[a-zA-Z\\s]+")) {
          alertMessage.setText(nameInput.isEmpty() ? "Wait!\nWe need your name!" : "Wait!\nLetters only please!");
          customAlert.setVisible(true);
          customAlert.setManaged(true);
          return;
      }
      
      this.name = nameInput;
      NameDatabase.saveUserName(nameInput);
      nameText.setText(nameInput);
      
      nameField.setVisible(false);
      nameField.setManaged(false);
      signButton.setVisible(false);
      signButton.setManaged(false);
      nameTag.setVisible(true);
      nameTag.setManaged(true);
      
      playStampAnimation();
   }
   
   /**
    * Plays a "stamp" effect animation when the user signs the book.
    */
   private void playStampAnimation() {
      nameTag.setScaleX(1.8);
      nameTag.setScaleY(1.8);
      nameTag.setOpacity(0);
      
      ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), nameTag);
      scaleDown.setToX(1.0);
      scaleDown.setToY(1.0);
      
      FadeTransition fadeIn = new FadeTransition(Duration.millis(200), nameTag);
      fadeIn.setToValue(1.0);
      
      ParallelTransition stamp = new ParallelTransition(scaleDown, fadeIn);
      stamp.setOnFinished(e -> openButton.setDisable(false));
      stamp.play();
   }
   
   /**
    * Closes the custom validation alert.
    */
   @FXML
   void handleCloseAlert(ActionEvent event) {
      customAlert.setVisible(false);
      customAlert.setManaged(false);
   }
   
   /**
    * Passes the selected category to the ColoringPageController and switches scenes.
    *
    * @param event The ActionEvent from the specific card's button.
    */
   @FXML
   void handlePageSelection(ActionEvent event) throws IOException {
      Button clicked = (Button) event.getSource();
      String id = clicked.getId();
      
      String selected = switch (id) {
         case "selectTwoButton" -> "Mountain";
         case "selectThreeButton" -> "Dolphin";
         case "selectFourButton" -> "House";
         default -> "Flower";
      };
      
      Parent currentRoot = clicked.getScene().getRoot();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      
      fadeOut.setOnFinished(e -> {
         try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("ColoringPageFXML.fxml"));
             Parent gameRoot = loader.load();
             
             ColoringPageController controller = loader.getController();
             if (controller != null) {
                 controller.setUserName(this.name);
                 controller.setActivePage(selected);
             }
             
             gameRoot.setOpacity(0);
             Stage stage = (Stage) clicked.getScene().getWindow();
             stage.getScene().setRoot(gameRoot);
             
             FadeTransition fadeIn = new FadeTransition(Duration.millis(500), gameRoot);
             fadeIn.setToValue(1.0);
             fadeIn.play();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      });
      fadeOut.play();
   }
   
   /**
    * Adds a vertical floating animaion to a node.
    *
    * @param stk   The StackPane to animate.
    * @param delay The delay before the animation starts.
    */
   private void addFloatingAnimation(StackPane stk, double delay) {
      TranslateTransition tt = new TranslateTransition(Duration.seconds(2), stk);
      tt.setByY(-10);
      tt.setCycleCount(Animation.INDEFINITE);
      tt.setAutoReverse(true);
      tt.setInterpolator(Interpolator.EASE_BOTH);
      tt.setDelay(Duration.seconds(delay));
      tt.play();
   }
   
   /**
    * Syncs hover highlighting across the card frame and text labels.
    * 
    * @param card  The card node to monitor.
    * @param num   The number label to highlight.
    * @param label The category label to highlight.
    * @param color The hex color code for the highlight.
    */
   private void addHoverEffects(StackPane card, Text num, Text label, String color) {
      card.setOnMouseEntered(e -> {
         if (card.getChildren().get(0) instanceof javafx.scene.shape.Rectangle frame) {
             frame.setStroke(Color.web(color));
             frame.setStrokeWidth(6);
         }
         if (num != null) num.setFill(Color.web(color, 0.9));
         if (label != null) label.setFill(Color.web(color));
         card.setCursor(Cursor.HAND);
      });
      
      card.setOnMouseExited(e -> {
         if (card.getChildren().get(0) instanceof javafx.scene.shape.Rectangle frame) {
             frame.setStroke(Color.web("#6B4E51"));
             frame.setStrokeWidth(3);
         }
         if (num != null) num.setFill(Color.web("#6B4E51", 0.9));
         if (label != null) label.setFill(Color.web("#6B4E51"));
      });
   }
}