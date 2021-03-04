package com.cpsu.sportgamegenerator.Data.Repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cpsu.sportgamegenerator.Data.Sport;
import com.cpsu.sportgamegenerator.Utils.DBHelper;
import com.cpsu.sportgamegenerator.Utils.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;

public class SportRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public SportRepo() {
        Sport sport = new Sport();
    }

    public static String createSportsTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + Sport.TABLE_SPORTS + " (" +
                Sport.COL_SPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " +
                Sport.COL_SPORT_NAME + " TEXT)";
        return query;
    }

    public void insert(String sportName) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Sport.COL_SPORT_NAME, sportName);
        // Inserting Row
        db.insert(Sport.TABLE_SPORTS, null, values);
        db.close();
    }

    public void update(Sport sport) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Sport.COL_SPORT_NAME, sport.getSportName());

        db.update(Sport.TABLE_SPORTS, values, Sport.COL_SPORT_ID + "= ? ", new String[]{String.valueOf(sport.getSportID())});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getSportList() {

        //Open connection to read only
        //db = DatabaseManager.getInstance().openDatabase();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " + Sport.COL_SPORT_ID + " as SportID, " +
                Sport.COL_SPORT_NAME + " as SportName " +
                "FROM " + Sport.TABLE_SPORTS + " ORDER BY SportName";

        ArrayList<HashMap<String, String>> sportList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            System.out.println("MoveToFirst");
            do {
                HashMap<String, String> sports = new HashMap<>();
                sports.put("ID", cursor.getString(cursor.getColumnIndex("SportID")));
                sports.put("Name", cursor.getString(cursor.getColumnIndex("SportName")));
                sportList.add(sports);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return sportList;
    }

    public Sport getSportByID(String id) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " + Sport.COL_SPORT_ID + " as SportID, " +
                Sport.COL_SPORT_NAME + " as SportName " +
                "FROM " + Sport.TABLE_SPORTS + " WHERE SportID = ?";

        Sport sport = new Sport();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{id});

        if (cursor.moveToFirst()) {
            do {
                sport.setSportID(cursor.getString(cursor.getColumnIndex("SportID")));
                sport.setSportName(cursor.getString(cursor.getColumnIndex("SportName")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return sport;
    }

    public boolean isSportExisting(String name) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Sport.TABLE_SPORTS + " WHERE " + Sport.COL_SPORT_NAME + " = ?";

        Sport sport = new Sport();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{name});

        if (cursor.moveToFirst()) {
            cursor.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        } else {
            cursor.close();
            DatabaseManager.getInstance().closeDatabase();
            return false;
        }
    }


}
