package com.cpsu.sportgamegenerator.Data.Repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cpsu.sportgamegenerator.Data.Schedules;
import com.cpsu.sportgamegenerator.Utils.DBHelper;
import com.cpsu.sportgamegenerator.Utils.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;

public class SchedRepo {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public SchedRepo() {
        Schedules sched = new Schedules();
    }

    public static String createTblSched() {
        String query = "CREATE TABLE IF NOT EXISTS " + Schedules.TABLE_SCHEDULES + " (" +
                Schedules.COL_SCH_ID + " TEXT, " +
                Schedules.COL_SCH_DATE + " TEXT, " +
                Schedules.COL_SCH_SPORT + " TEXT, " +
                Schedules.COL_SCH_TYPE + " TEXT, " +
                Schedules.COL_SCH_T1 + " TEXT, " +
                Schedules.COL_SCH_T2 + " TEXT, " +
                Schedules.COL_SCH_RND + " TEXT, " +
                Schedules.COL_SCH_GAME + " TEXT) ";
        return query;
    }

    public static String createTblGameSched() {
        String query = "CREATE TABLE IF NOT EXISTS " + Schedules.TABLE_GAMESCHED + " (" +
                Schedules.COL_GAME_SCH_ID + " TEXT, " +
                Schedules.COL_GAME_NO + " TEXT, " +
                Schedules.COL_GAME_DATE + " TEXT) ";
        return query;
    }

    public void insert(ArrayList<HashMap<String, String>> schedule, String schedId, String date,  String sport, String type) {

        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i=0;i<schedule.size();i++) {
            values.put(Schedules.COL_SCH_ID, schedId);
            values.put(Schedules.COL_SCH_DATE, date);
            values.put(Schedules.COL_SCH_SPORT, sport);
            values.put(Schedules.COL_SCH_TYPE, type);
            values.put(Schedules.COL_SCH_T1, schedule.get(i).get("Team1"));
            values.put(Schedules.COL_SCH_T2, schedule.get(i).get("Team2"));
            values.put(Schedules.COL_SCH_RND, schedule.get(i).get("Round"));
            values.put(Schedules.COL_SCH_GAME, schedule.get(i).get("Game"));
            db.insert(Schedules.TABLE_SCHEDULES, null,values);
        }

        db.close();
    }

    public void insertGameSched(String schedId, ArrayList<HashMap<String, String>> gameSched) {

        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i=0;i<gameSched.size();i++) {
            values.put(Schedules.COL_GAME_SCH_ID, schedId);
            values.put(Schedules.COL_GAME_NO, gameSched.get(i).get("Game"));
            values.put(Schedules.COL_GAME_DATE, gameSched.get(i).get("Date"));

            db.insert(Schedules.TABLE_GAMESCHED, null,values);
        }

        db.close();
    }

    public ArrayList<HashMap<String, String>> getScheduleById(String schedId) {
        ArrayList<HashMap<String, String>> schedule = new ArrayList<>();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + Schedules.TABLE_SCHEDULES + " s " +
                "LEFT JOIN " + Schedules.TABLE_GAMESCHED + " g ON " +
                "s." + Schedules.COL_SCH_ID + "= g." + Schedules.COL_GAME_SCH_ID + " AND " +
                "s." + Schedules.COL_SCH_GAME + "=g."  + Schedules.COL_GAME_NO + " WHERE " +
                Schedules.COL_SCH_ID + " = ? ORDER BY CAST(" +
                Schedules.COL_SCH_GAME + " AS INT);";
        System.out.println(query);

        Cursor cursor = db.rawQuery(query, new String[]{schedId});

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> schedRow = new HashMap<>();
                schedRow.put("Team1", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_T1)));
                schedRow.put("Team2", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_T2)));
                schedRow.put("Round", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_RND)));
                schedRow.put("Game", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_GAME)));
                schedRow.put("Date", cursor.getString(cursor.getColumnIndex(Schedules.COL_GAME_DATE)));
                schedule.add(schedRow);
            } while (cursor.moveToNext());
        }
        return schedule;
    }

    public ArrayList<HashMap<String, String>> getSchedules() {
        ArrayList<HashMap<String, String>> schedList = new ArrayList<>();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + Schedules.TABLE_SCHEDULES + " GROUP BY " +
                Schedules.COL_SCH_ID + ", " +
                Schedules.COL_SCH_DATE + ", " +
                Schedules.COL_SCH_SPORT + ", " +
                Schedules.COL_SCH_TYPE + " ORDER BY " +
                Schedules.COL_SCH_DATE + " DESC; ";
        System.out.println(query);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> sched = new HashMap<>();
                sched.put("SchedId", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_ID)));
                sched.put("SchedSport", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_SPORT)));
                sched.put("SchedDateType", cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_DATE)) + " | " + cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_TYPE)));
                schedList.add(sched);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();

        return schedList;
    }

    public Schedules getSchedDetailById(String schedId) {
        Schedules schedule = new Schedules();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + Schedules.TABLE_SCHEDULES + " WHERE " +
                Schedules.COL_SCH_ID + "=? GROUP BY " +
                Schedules.COL_SCH_ID + ", " +
                Schedules.COL_SCH_DATE + ", " +
                Schedules.COL_SCH_SPORT + ", " +
                Schedules.COL_SCH_TYPE + "; ";

        Cursor cursor = db.rawQuery(query, new String[]{schedId});

        if (cursor.moveToFirst()) {
            do {
                schedule.setSchedId(cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_ID)));
                schedule.setDate(cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_DATE)));
                schedule.setSport(cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_SPORT)));
                schedule.setType(cursor.getString(cursor.getColumnIndex(Schedules.COL_SCH_TYPE)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();

        return schedule;
    }

    public void populateSchedTable(TableLayout schedTable, ArrayList<HashMap<String, String>> schedule, Context context) {
        schedTable.removeViews(1, schedTable.getChildCount()-1);
        for (int i=0; i<schedule.size(); i++) {
            schedTable.addView(addRow(schedule.get(i).get("Team1"), "vs", schedule.get(i).get("Team2"), schedule.get(i).get("Round"), schedule.get(i).get("Game"), schedule.get(i).get("Date"), context));
        }

    }

    public TableRow addRow(String team1, String vs, String team2, String round, String game, String date, Context context) {
        TableRow row = new TableRow(context);
        TextView tv1 = new TextView(context);
        TextView tv2 = new TextView(context);
        TextView tv3 = new TextView(context);
        TextView tv4 = new TextView(context);
        TextView tv5 = new TextView(context);
        TextView tv6 = new TextView(context);
        tv1.setText(team1);
        tv2.setText(vs);
        tv3.setText(team2);
        tv4.setText(round);
        tv5.setText(game);
        tv6.setText(date);
        tv1.setPadding(10, 10, 10, 10);
        tv2.setPadding(10, 10, 10, 10);
        tv3.setPadding(10, 10, 10, 10);
        tv4.setPadding(10, 10, 10, 10);
        tv5.setPadding(10, 10, 10, 10);
        tv6.setPadding(10, 10, 10, 10);
        row.addView(tv1);
        row.addView(tv2);
        row.addView(tv3);
        row.addView(tv4);
        row.addView(tv5);
        row.addView(tv6);
        return row;
    }
}
