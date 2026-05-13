package com.example.finalsactivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DB_NAME = "apartment.db";

    public Database(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tenants (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "room TEXT," +
                "rent TEXT," +
                "due_date TEXT," +
                "payment_status TEXT)");

        db.execSQL("CREATE TABLE payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenant_id INTEGER," +
                "amount TEXT," +
                "method TEXT," +
                "status TEXT)");

        db.execSQL("CREATE TABLE maintenance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenant_id INTEGER," +
                "issue TEXT," +
                "category TEXT," +
                "priority TEXT," +
                "image TEXT," +
                "status TEXT," +
                "date_created TEXT)");

        db.execSQL("INSERT INTO maintenance(status) VALUES('Sample')");

        db.execSQL("CREATE TABLE announcements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "message TEXT)");

        db.execSQL("CREATE TABLE notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenant_id INTEGER," +
                "title TEXT," +
                "message TEXT," +
                "type TEXT," +  // rent, payment, maintenance, announcement
                "date TEXT)");

}

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS tenants");
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS maintenance");
        onCreate(db);
    }
}