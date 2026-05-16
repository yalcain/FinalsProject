package com.example.finalsactivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DB_NAME = "apartment.db";

    public Database(Context context) {
        super(context, DB_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "fullname TEXT," +
                        "username TEXT UNIQUE," +
                        "age INTEGER," +
                        "gender TEXT," +
                        "contact TEXT," +
                        "email TEXT UNIQUE," +
                        "password TEXT)"
        );

        db.execSQL(
                "CREATE TABLE tenants (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "room TEXT," +
                        "rent TEXT," +
                        "due_date TEXT," +
                        "contact TEXT)"
        );

        db.execSQL(
                "CREATE TABLE rooms (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "property TEXT," +
                        "room_name TEXT," +
                        "type TEXT," +
                        "capacity INTEGER," +
                        "occupied INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE bookings (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "property TEXT," +
                        "location TEXT," +
                        "checkin TEXT," +
                        "checkout TEXT," +
                        "total INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE payments (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "amount TEXT," +
                        "method TEXT," +
                        "reference TEXT," +
                        "image TEXT," +
                        "status TEXT," +
                        "date TEXT)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS notifications (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "message TEXT," +
                        "type TEXT," +
                        "date TEXT)"
        );

        // MAINTENANCE TABLE
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS maintenance (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tenant_id INTEGER," +
                        "issue TEXT," +
                        "category TEXT," +
                        "priority TEXT," +
                        "image TEXT," +
                        "status TEXT," +
                        "date_created TEXT)"
        );
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM rooms",
                null
        );

        if (c.moveToFirst() && c.getInt(0) == 0) {

            db.execSQL(
                    "INSERT INTO rooms(property, room_name, type, capacity, occupied) VALUES " +
                            "('SD Dorm 2','Room 1','Bedspace',4,0)," +
                            "('SD Dorm 2','Room 2','Bedspace',4,0)," +
                            "('Loft 22','Room 1','Transient',2,0)," +
                            "('Milflores','Room 1','Bedspace',3,0)"
            );
        }

        c.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS tenants");
        db.execSQL("DROP TABLE IF EXISTS rooms");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS notifications");

        onCreate(db);
    }
}