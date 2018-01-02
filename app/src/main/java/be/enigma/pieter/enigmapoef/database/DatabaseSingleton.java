package be.enigma.pieter.enigmapoef.database;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Pieter on 27/12/2017.
 */

public class DatabaseSingleton {

    private static DatabaseSingleton ref;

    private DatabaseSingleton() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static DatabaseSingleton getDatabaseSingleton() {
        if (ref == null)
            ref = new DatabaseSingleton();
        return ref;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();

    }

    public Connection getConnection(boolean autoCommit) {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://dt5.ehb.be/"+DatabaseProperties.USERNAME,
                    DatabaseProperties.USERNAME, DatabaseProperties.PASSWORD);

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }
}
