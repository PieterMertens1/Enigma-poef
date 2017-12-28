package be.enigma.pieter.enigmapoef.models;

import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by Pieter on 26/11/2017.
 */

public class Poef {

    private String gebruiker;
    private String hoeveelheid;
    private String reden;
    private Map<String, String> tijd;


    public Poef() {
    }

    public Poef(String gebruiker, String hoeveelheid, String reden) {
        // ik weet ni waarom ma dees had ik ni
        this.gebruiker = gebruiker;
        this.hoeveelheid = hoeveelheid;
        this.reden = reden;
        //this.tijd = ServerValue.TIMESTAMP;
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

    public Map<String, String> getTijd() {
        return ServerValue.TIMESTAMP;
    }

    public void setTijd(Map<String, String> tijd) {
        this.tijd = tijd;
    }
}
