package be.enigma.pieter.enigmapoef;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Objects;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.models.Poef;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Pieter on 7/01/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SQLiteTest {

    private DatabaseHelper database;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DatabaseHelper.TABLE_NAME);
        database = new DatabaseHelper(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }


    @Test
    public void getPoefByUserTest() {
        Cursor test = database.getPoefByUser("test");
        assertTrue(test != null);
    }

    @Test(timeout = 1000)
    public void addDataTest() {
        database.addData("test","1","test","");

        Cursor data = database.getPoefByUser("test");

        ArrayList<Poef>poeflijst = new ArrayList<>();

        while (data.moveToNext()) {
            Poef poef = new Poef();

            poef.setGebruiker(data.getString(1)); //listData.add(data.getString(1)); gebruiker
            poef.setHoeveelheid(data.getString(2)); //listData.add(data.getString(2)); aantal
            poef.setReden(data.getString(3)); //listData.add(data.getString(3)); reden
            poef.setTijd(data.getString(4)); //listData.add(data.getString(4)); tijd

            String tijd = poef.getTijd();
            poef.setTijd("");
            poeflijst.add(poef);
        }

        for (Poef poef: poeflijst)
        {

            assertTrue(Objects.equals(poef.getGebruiker(), "test"));


        }






    }
    /*@Test
    public void shouldAddExpenseType() throws Exception {
        database.addExpenseType(new ExpenseType("Food"));

        List<String> expenseTypes = database.getExpenseTypes();
        assertThat(expenseTypes.size(), is(1));
        assertTrue(expenseTypes.get(0).equals("Food"));
    }*/
}
