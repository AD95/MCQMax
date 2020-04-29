package com.ad95.mcqmax.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ad95.mcqmax.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class databaseHelper extends SQLiteOpenHelper {

    private static databaseHelper mInstance = null;

    private databaseHelper(Context ctx, SQLiteDatabase.CursorFactory factory) {
        super(ctx.getApplicationContext(), Common.dbName, factory, 1);
    }

    public static databaseHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new databaseHelper(ctx.getApplicationContext(), null);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Common.mcqTable +
                " (" +
                Common.id + " text not null," +
                Common.correct_answer_id + " text," +
                Common.question_text + " text," +
                Common.category + " text," +
                "PRIMARY KEY(id)" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Common.choicesTable +
                " (" +
                Common.id + " text not null," +
                Common.chosen_percentage + " text," +
                Common.answer_text + " text," +
                Common.question_id + " text," +
                "PRIMARY KEY(id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void addEntry(ContentValues values) {
        String table = values.containsKey(Common.category) ? Common.mcqTable : Common.choicesTable;
        getWritableDatabase().insert(table, null, values);
    }

    public ArrayList<String> fetchCategories() {
        Cursor c;
        c = getReadableDatabase().rawQuery("SELECT DISTINCT " + Common.category + " FROM " + Common.mcqTable, null);
        ArrayList<String> list = new ArrayList<>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    list.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
        }
        return list;
    }

    public Map<String, ArrayList<String>> fetchMCQ(String category) {
        Cursor c;
        if (category.equals(Common.all))
            c = getReadableDatabase().rawQuery("SELECT * FROM "+ Common.mcqTable +" ORDER BY id", null);
        else
            c = getReadableDatabase().rawQuery("SELECT * FROM "+ Common.mcqTable +" WHERE " + Common.category + " = '" + category + "' ORDER BY " + Common.id, null);
        ArrayList<String> list;
        Map<String, ArrayList<String>> map1 = new HashMap<>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    list = new ArrayList<>();
                    for (int i = 1; i < 3; i++)
                        list.add(c.getString(i));
                    map1.put(c.getString(0), list);
                } while (c.moveToNext());
            }
            c.close();
        }
        return map1;
    }

    public Map<String, ArrayList<String>> fetchChoices(String id) {
        Cursor c;
        c = getReadableDatabase().rawQuery("SELECT * FROM "+ Common.choicesTable +" WHERE " + Common.question_id + " = '" + id + "' ORDER BY " + Common.id, null);
        ArrayList<String> list;
        Map<String, ArrayList<String>> map = new HashMap<>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    list = new ArrayList<>();
                    for (int i = 1; i < 3; i++)
                        list.add(c.getString(i));
                    map.put(c.getString(0), list);
                } while (c.moveToNext());
            }
            c.close();
        }
        return map;
    }

    public void dropTables() {
        getWritableDatabase().execSQL("DELETE FROM "+ Common.mcqTable);
        getWritableDatabase().execSQL("DELETE FROM "+ Common.choicesTable);
    }

}
