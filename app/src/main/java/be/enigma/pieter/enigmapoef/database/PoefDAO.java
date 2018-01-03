package be.enigma.pieter.enigmapoef.database;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import be.enigma.pieter.enigmapoef.models.Poef;

/**
 * Created by Pieter on 27/12/2017.
 */

public class PoefDAO extends BaseDAO {

    public PoefDAO() {
        super();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public Poef geefPoefVanId(int id) {
        Poef poef = new Poef();

        String query = "SELECT * FROM Poef WHERE id = ?";
        try (Connection con = BaseDAO.getConnectie()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

                preparedStatement.setInt(1, id);
                Log.wtf("test", "meegegeven id = " + id);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        poef.setGebruiker(rs.getString("gebruiker"));
                        poef.setHoeveelheid(rs.getString("hoeveelheid"));
                        poef.setReden(rs.getString("reden"));
                        poef.setTijd(rs.getString("tijd"));
                    }
                } catch (Exception ex) {
                    Log.wtf("error", ex);
                    System.out.println(ex);
                }
            } catch (Exception ex) {
                System.out.println(ex);
                Log.wtf("error", ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.wtf("error", e);
        }

        return poef;


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void insert(Poef poef) {

        try (Connection con = BaseDAO.getConnectie()) {
            if (poef != null) {

                String query = "INSERT INTO Poef (id,gebruiker,hoeveelheid,reden,tijd,checked) VALUES (NULL,?,?,?,?,0);";
                con.setAutoCommit(false);
                try (PreparedStatement preparedStatement = con.prepareStatement(query,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, poef.getGebruiker());
                    preparedStatement.setString(2, poef.getHoeveelheid());
                    preparedStatement.setString(3, poef.getReden());
                    preparedStatement.setString(4, poef.getTijd());

                    preparedStatement.executeUpdate();
                    con.commit();
                } catch (Exception ex) {
                    System.out.println(ex);
                }

            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static ArrayList<Integer> getAllIdUnchecked() {

        Integer id;
        ResultSet rs = null;
        ArrayList<Integer> list = new ArrayList<>();
        String query = "SELECT id FROM Poef where checked = 0";
        try (Connection con = BaseDAO.getConnectie()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.executeQuery();
                con.commit();
                while (rs.next()) {
                    id = rs.getInt("id");
                    list.add(id);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getId(Poef poef) {
        int id = 0;

        String query = "SELECT id FROM Poef WHERE gebruiker = ? and hoeveelheid = ? and reden = ?";
        try (Connection con = BaseDAO.getConnectie()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

                preparedStatement.setString(1, poef.getGebruiker());
                preparedStatement.setString(2, poef.getHoeveelheid());
                preparedStatement.setString(3, poef.getReden());
                //preparedStatement.setString(4, poef.getTijd());

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        id = rs.getInt("id");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setChecked(Poef poef) {

        String query = "UPDATE Poef SET checked = 1 WHERE gebruiker = ? and hoeveelheid = ? and reden = ? and tijd = ?";
        try (Connection con = BaseDAO.getConnectie()) {
            if (poef != null) {

                con.setAutoCommit(false);
                try (PreparedStatement preparedStatement = con.prepareStatement(query,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, poef.getGebruiker());
                    preparedStatement.setString(2, poef.getHoeveelheid());
                    preparedStatement.setString(3, poef.getReden());
                    preparedStatement.setString(4, poef.getTijd());

                    preparedStatement.executeUpdate();
                    con.commit();
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static ArrayList<Poef> getAllPoefFromUser(String gebruiker) {

        ArrayList<Poef> list = new ArrayList<>();
        String query = "SELECT * FROM Poef where gebruiker = ?";
        try (Connection con = BaseDAO.getConnectie()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, gebruiker);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {

                        Poef poef = new Poef();

                        poef.setGebruiker(rs.getString("gebruiker"));
                        poef.setHoeveelheid(rs.getString("hoeveelheid"));
                        poef.setReden(rs.getString("reden"));
                        poef.setTijd(rs.getString("tijd"));
                        list.add(poef);
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;

    }














}









