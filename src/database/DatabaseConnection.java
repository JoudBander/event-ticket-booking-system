package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "1234";
    private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found!");
            System.err.println("Make sure ojdbc.jar is in your classpath");
            throw new SQLException("Oracle JDBC Driver not found: " + e.getMessage());

        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful!");
                System.out.println("  Connected to: " + DB_URL);
                System.out.println("  User: " + DB_USER);
                return true;
            }
        } catch (SQLException e) {
            System.err.println(" Database connection failed!");
            System.err.println("  Error: " + e.getMessage());
            return false;
        }
        return false;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}

    
