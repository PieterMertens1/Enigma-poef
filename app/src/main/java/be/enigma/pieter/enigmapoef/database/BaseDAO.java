package be.enigma.pieter.enigmapoef.database;
import java.sql.*;

/**
 * Created by Pieter on 27/12/2017.
 */

public class BaseDAO {
    private static Connection connectie;

    public static Connection getConnectie() {
        return connectie;
    }

    public void setConnectie(Connection connectie) {
        this.connectie = connectie;
    }

    public BaseDAO() {
        setConnectie(DatabaseSingleton.getDatabaseSingleton().getConnection(true));
    }

}
