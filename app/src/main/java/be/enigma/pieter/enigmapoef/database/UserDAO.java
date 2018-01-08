package be.enigma.pieter.enigmapoef.database;

import android.annotation.TargetApi;
import android.os.Build;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.enigma.pieter.enigmapoef.models.Poef;

/**
 * Created by Pieter on 8/01/2018.
 */

public class UserDAO extends BaseDAO {

    public UserDAO() {super();}

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int checkIfBestuur(String user) {
        int isBestuur = 0;

        String query = "SELECT bestuur FROM Users WHERE emailadres = ?";
        try (Connection con = BaseDAO.getConnectie()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

                preparedStatement.setString(1, user);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        isBestuur = rs.getInt("bestuur");
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
        return isBestuur;
    }
}
