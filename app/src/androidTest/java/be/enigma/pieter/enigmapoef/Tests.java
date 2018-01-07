package be.enigma.pieter.enigmapoef;

import org.junit.Before;
import org.junit.Test;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.models.Poef;

import static org.junit.Assert.*;

/**
 * Created by Pieter on 7/01/2018.
 */
public class Tests {

    private Poef poef1;
    private Poef poef2;
    private Poef poef3;

    @Before
    public void setUp() throws Exception {
        poef1 = new Poef("","",null,"");
        poef2 = new Poef("testnaam","2","test","");
        poef3 = new Poef("testnaam", "-1", "test", "");
    }

    @Test
    public void poefToevoegen() throws Exception {
        PoefToevoegen poefToevoegen = new PoefToevoegen();
        poefToevoegen.addData(poef1);
        assertEquals(poef1.getGebruiker(), "");
        assertFalse(poef2.getGebruiker().isEmpty());
        assertFalse(Float.parseFloat(poef3.getHoeveelheid()) > 0);
    }

    @Test(expected = NullPointerException.class)
    public void nullStringTest() {
        assertTrue(poef1.getReden().equals(""));
    }

}