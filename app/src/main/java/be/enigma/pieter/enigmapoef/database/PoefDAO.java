package be.enigma.pieter.enigmapoef.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import be.enigma.pieter.enigmapoef.models.Poef;

/**
 * Created by Pieter on 27/12/2017.
 */

public class PoefDAO extends BaseDAO {

    public PoefDAO() {
        super();
    }


    public ArrayList<Poef> geefAllePoef() {

        ArrayList<Poef> lijst = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = "Select * from Poef";

        try {
            ps = getConnectie().prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {

                lijst.add(new Poef(rs.getString("gebruiker"), rs.getString("hoeveelheid"), rs.getString("reden")));

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return lijst;


    }

    public int insert(Poef poef) {
        int result = 0;
        PreparedStatement ps = null;
        String sql = "Insert into Poef Values(?,?,?)";


        try {
            ps = getConnectie().prepareStatement(sql);
            ps.setString(1, poef.getGebruiker());
            ps.setString(2, poef.getHoeveelheid());
            ps.setString(3, poef.getReden());

            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally
        {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        return result;
    }



}
