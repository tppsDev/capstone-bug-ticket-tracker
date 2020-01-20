/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Tim Smith
 */
public class DatabaseMgr {
    private static final String DB = "62THZ4TZGb";
    private static final String URL = "jdbc:mysql://remotemysql.com/" + DB;
    private static final String DB_USER = "62THZ4TZGb";
    private static final String DB_PASSWORD = "mCV5yHLsxu";
    
    
    // Constructor
    private DatabaseMgr() {
        throw new RuntimeException("DatabaseMgr cannot be instantiated");
    }
    
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
