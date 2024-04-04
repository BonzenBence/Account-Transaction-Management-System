package bankaccountsfx.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // JDBC URL, username and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3307/banking_system?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "Rockpsp123!";

    // JDBC variables for opening and managing connection
    private static Connection connection;

    public static void connect() {
        try {
            // Dynamically load the JDBC driver class
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (connection != null) {
                System.out.println("Connected to the database!");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to the database failed!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect(); // Ensure connect() method re-establishes the connection
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from the database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Connection testConn = DatabaseConnector.getConnection();
            if (testConn != null) {
                System.out.println("Connection successful!");
                testConn.close();
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
