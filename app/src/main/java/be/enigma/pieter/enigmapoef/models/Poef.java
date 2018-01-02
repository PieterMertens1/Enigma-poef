package be.enigma.pieter.enigmapoef.models;

import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by Pieter on 26/11/2017.
 */

public class Poef {

    private String gebruiker;
    private String hoeveelheid;
    private String reden;
    private String tijd;

    @Override
    public String toString() {
        return "Poef{" +
                "gebruiker='" + gebruiker + '\'' +
                ", hoeveelheid='" + hoeveelheid + '\'' +
                ", reden='" + reden + '\'' +
                ", tijd = '" + tijd + '\n' +
                '}';
    }

    public Poef() {
    }

    public Poef(String gebruiker, String hoeveelheid, String reden, String tijd) {
        this.gebruiker = gebruiker;
        this.hoeveelheid = hoeveelheid;
        this.reden = reden;


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.tijd =  timestamp.toString();


        //this.tijd = tijd;
    }




    public String getGebruiker() {
        return gebruiker;
    }

    public void setGebruiker(String gebruiker) {
        this.gebruiker = gebruiker;
    }

    public String getHoeveelheid() {
        return hoeveelheid;
    }

    public void setHoeveelheid(String hoeveelheid) {
        this.hoeveelheid = hoeveelheid;
    }

    public String getReden() {
        return reden;
    }

    public void setReden(String reden) {
        this.reden = reden;
    }


    public String getTijd() {
        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //return timestamp.toString();
        return tijd;
    }

    public void setTijd(String tijd) {
        this.tijd = tijd;
    }
}
