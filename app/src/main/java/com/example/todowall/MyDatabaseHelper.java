package com.example.todowall;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "ToDoWall.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "todo";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "todo_title";
    private static final String COLUMN_STATUS = "todo_status";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " + COLUMN_STATUS + " TEXT);";
        db.execSQL(query);

        String query2 = "CREATE TABLE IF NOT EXISTS " + "settings" +
                "(" + "name" + " TEXT, " +
                "value" + " BLOB);";
        db.execSQL(query2);

        String query3 = "SELECT * FROM settings WHERE name=" + "'walluri'";

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query3, null);
        }

        if(cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("name", "walluri");
            cv.put("value", "");
            db.insert("settings", null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + "settings");
        onCreate(db);
    }

    void addTodo(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_STATUS, "unchecked");
        long result = db.insert(TABLE_NAME, null, cv);

        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show();
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String title, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_STATUS, status);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show();
        }
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "deleted", Toast.LENGTH_LONG).show();
        }
    }

    void changeWall(byte[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;

//        String query2 = "CREATE TABLE IF NOT EXISTS " + "settings" +
//                "(" + "name" + " TEXT, " +
//                "value" + " BLOB);";
//        db.execSQL(query2);
//
//        String query = "SELECT * FROM settings WHERE name=" + "'walluri'";
//
//        Cursor cursor = null;
//        if(db != null) {
//            cursor = db.rawQuery(query, null);
//        }
//
//        if(cursor.getCount() == 0) {
//            ContentValues cv = new ContentValues();
//            cv.put("name", "walluri");
//            cv.put("value", data);
//            result = db.insert("settings", null, cv);
//        }
//        else {
            ContentValues cv = new ContentValues();
            cv.put("value", data);

            result = db.update("settings", cv, "name=?", new String[]{"walluri"});
//        }
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show();
        }
    }

    Cursor getWall() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;

        String query2 = "CREATE TABLE IF NOT EXISTS " + "settings" +
                "(" + "name" + " TEXT, " +
                "value" + " BLOB);";
        db.execSQL(query2);

        String query = "SELECT * FROM settings WHERE name=" + "'walluri'";

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Bitmap getImage(){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = 0;

//        String query2 = "CREATE TABLE IF NOT EXISTS " + "settings" +
//                "(" + "name" + " TEXT, " +
//                "value" + " BLOB);";
//        db.execSQL(query2);
//
//        String query = "SELECT * FROM settings WHERE name=" + "'walluri'";
//
//        Cursor cursor = null;
//        if(db != null) {
//            cursor = db.rawQuery(query, null);
//        }
//
//        if(cursor.getCount() != 0) {


            String qu = "select value from settings where name=" + "'walluri'";
            Cursor cur = db.rawQuery(qu, null);

            if (cur.moveToFirst()) {
                byte[] imgByte = cur.getBlob(0);
                cur.close();
                return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            }
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
//        }

        return null;
    }
}
