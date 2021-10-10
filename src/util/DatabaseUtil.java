package util;

import java.sql.*;

public class DatabaseUtil {

    public static void createNewDatabase(String url) {
        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void executeFillDB(String createTable, String url) {
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(createTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Connects to database and executes a Statement
     */
    public static void execute(String statement, String url) {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(statement);

            System.out.println("Statement was successful \uD83E\uDD73");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
