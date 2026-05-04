import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the SQLite database.
 * Handles the persistence of user registration, individual pixel states,
 * and drawing completion status to ensure user progress is saved locally.
 *
 * @author Color-Coded Coders
 * @version 1.0
 */
public class NameDatabase {
   /** Connection URL for the SQLite database file. */
   private static final String URL = "jdbc:sqlite:coloringbook.db";
   
   /**
    * Initializes the database schema.
    * Creates the {@code users} and {@code pixel_progress} tables if they
    * do not already exist in the local .db file.
    */
   public static void initialize() {
      String userTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "name TEXT NOT NULL);";
                        
      String progressTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "user_name TEXT, "
                        + "page_name TEXT, "
                        + "pixel_index INTEGER, "
                        + "is_completed BOOLEAN, "
                        + "PRIMARY KEY (user_name, page_name, pixel_index));";
      
      try (Connection conn = connect();
           Statement stmt = conn.createStatement()) {
         stmt.execute(userTable);
         stmt.execute(progressTable);
      } catch (SQLException e) {
         System.err.println("Database Initialization Error: " + e.getMessage());
      }
           
   }
   
   /**
    * Establishes a connection to the SQLite database.
    *
    * @return A Connection object for executing SQL queries.
    * @throws SQLException if the connection to the database file fails.
    */
   private static Connection connect() throws SQLException {
      return DriverManager.getConnection(URL);
   }
   
   /**
    * Persists a new user name to the database.
    *
    * @param name The name entered by the user in the registration field.
    */
   public static void saveUserName(String name) {
      String sql = "INSERT INTO users(name) VALUES(?)";
      try (Connection conn = connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, name);
         pstmt.executeUpdate();    
      } catch (SQLException e) {
         System.err.println("Error saving user: " + e.getMessage());
      }
   }
   
   /**
    * Retrieves the name of the most recently registered user.
    * Used to personalize the UI and track progress ownership.
    *
    * @return The latest user name string, or "Guest" if no records are found.
    */
   public static String getLatestName() {
      String sql = "SELECT name FROM users ORDER BY id DESC LIMIT 1";
      try (Connection conn = connect();
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(sql)) {
         if (rs.next()) {
            return rs.getString("name");
         }
      } catch (SQLException e) {
         System.err.println("Error fetching latest name: " + e.getMessage());
      }
      return "Guest";
   }
   
   /**
    * Saves the state of a successfully colored pixel to the database.
    * Uses an INSERT OR REPLACE strategy to update existing progress.
    *
    * @param user The current user's name.
    * @param page The title of the coloring page (e.g., "Dolphin").
    * @param index The index of the pixel within the GridPane.
    * @param completed Whether the entire drawing has been finished.
    */
   public static void savePixelProgress(String user, String page, int index, boolean completed) {
      String sql = "INSERT OR REPLACE INTO pixel_progress(user_name, page_name, pixel_index, is_completed) VALUES(?,?,?,?)";
      try (Connection conn = connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, user);
         pstmt.setString(2, page);
         pstmt.setInt(3, index);
         pstmt.setBoolean(4, completed);
         pstmt.executeUpdate();    
      } catch (SQLException e) {
         System.err.println("Error saving pixel progress: " + e.getMessage());
      }
   }
   
   /**
    * Retrieves a List of previously colored pixel indices for a specific user and page.
    *
    * @param user The current user's name.
    * @param page The title of the coloring page.
    * @return A List of integers representing the indices of filled pixels.
    */
   public static List<Integer> getSavedPixels(String user, String page) {
      List<Integer> pixels = new ArrayList<>();
      String sql = "SELECT pixel_index FROM pixel_progress WHERE user_name = ? AND page_name = ?";
      
      try (Connection conn = connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, user);
         pstmt.setString(2, page);
         ResultSet rs = pstmt.executeQuery();
         
         while (rs.next()) {
            pixels.add(rs.getInt("pixel_index"));
         }
      } catch (SQLException e) {
         System.err.println("Error loading saved pixels: " + e.getMessage());
      }
      return pixels;
   }
}