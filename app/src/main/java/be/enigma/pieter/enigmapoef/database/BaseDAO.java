package be.enigma.pieter.enigmapoef.database;

import java.sql.Connection;

/**
 * Created by Pieter on 27/12/2017.
 */

public class BaseDAO {
    private Connection connectie;

    public Connection getConnectie() {
        return connectie;
    }

    public void setConnectie(Connection connectie) {
        this.connectie = connectie;
    }

    public BaseDAO() {
        setConnectie(DatabaseSingleton.getDatabaseSingleton().getConnection(true));
    }

}
