package be.enigma.pieter.enigmapoef.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

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



        //db.delete(TABLE_NAME, null, null);

    }

/*
    *//**
     * Compose JSON out of SQLite records
     * @return
     *//*
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM users where udpateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userId", cursor.getString(0));
                map.put("userName", cursor.getString(1));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    *//**
     * Get Sync status of SQLite
     * @return
     *//*
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync neededn";
        }
        return msg;
    }


    *//**
     * Get SQLite records that are yet to be Synced
     * @return
     *//*
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM users where udpateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }


    *//**
     * Update Sync status against each User ID
     * @param id
     * @param status
     *//*
    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update users set udpateStatus = '"+ status +"' where userId="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }*/












}
