package be.enigma.pieter.enigmapoef.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pieter on 28/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Poef";
    private static final String COL0 = "ID";
    private static final String COL1 = "gebruiker";
    private static final String COL2 = "hoeveelheid";
    private static final String COL3 = "reden";
    private static final String COL4 = "tijd";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //String createTable = "CREATE TABLE " + TABLE_NAME + "(ID integer PRIMARY KEY AUTOINCREMENT, " + COL1 + " Text)";
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID integer PRIMARY KEY AUTOINCREMENT, " + COL1 + " Text, " + COL2 + " Text, " + COL3 + " Text, " + COL4 + " Text)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropIfExists = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL( dropIfExists);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(String gebruiker, String hoeveelheid, String reden, String tijd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, gebruiker);
        contentValues.put(COL2, hoeveelheid);
        contentValues.put(COL3, reden);
        contentValues.put(COL4, tijd);

        System.out.print("addData: Adding " + gebruiker + "with: " + hoeveelheid + "aan poef" + " to "  + TABLE_NAME );

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor getPoefByUser(String user) {
        if (user != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + user +"'";
            Cursor data = db.rawQuery(query, null);

            return data;
        }
        else {
            return null;
        }
    }



    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "Drop TABLE " + TABLE_NAME;
        //db.execSQL(query);



        db.delete(TABLE_NAME, null, null);

    }















}
