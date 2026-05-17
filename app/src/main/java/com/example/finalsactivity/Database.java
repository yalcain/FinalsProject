package com.example.finalsactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Database extends SQLiteOpenHelper {

    // Database Info
    public static final String DB_NAME = "apartment.db";
    public static final int DB_VERSION = 8; // ⚠️ INCREASED VERSION to apply new table structure

    // -------------------------- TABLE NAMES --------------------------
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TENANTS = "tenants";
    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String TABLE_PAYMENTS = "payments";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_MAINTENANCE = "maintenance";
    public static final String TABLE_PLACES = "places";

    // -------------------------- COLUMNS --------------------------
    // Users Table
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_FULLNAME = "fullname";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_AGE = "age";
    public static final String COL_USER_GENDER = "gender";
    public static final String COL_USER_CONTACT = "contact";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_VERIFIED = "verified";

    // Tenants Table
    public static final String COL_TENANT_ID = "id";
    public static final String COL_TENANT_USERNAME = "username";
    public static final String COL_TENANT_ROOM = "room";
    public static final String COL_TENANT_RENT = "rent";
    public static final String COL_TENANT_DUE_DATE = "due_date";
    public static final String COL_TENANT_CONTACT = "contact";

    // Rooms Table
    public static final String COL_ROOM_ID = "id";
    public static final String COL_ROOM_PROPERTY = "property";
    public static final String COL_ROOM_NAME = "room_name";
    public static final String COL_ROOM_TYPE = "type";
    public static final String COL_ROOM_CAPACITY = "capacity";
    public static final String COL_ROOM_OCCUPIED = "occupied";

    // Bookings Table
    public static final String COL_BOOKING_ID = "id";
    public static final String COL_BOOKING_USERNAME = "username";
    public static final String COL_BOOKING_PROPERTY = "property";
    public static final String COL_BOOKING_LOCATION = "location";
    public static final String COL_BOOKING_CHECKIN = "checkin";
    public static final String COL_BOOKING_CHECKOUT = "checkout";
    public static final String COL_BOOKING_TOTAL = "total";

    // Payments Table
    public static final String COL_PAYMENT_ID = "id";
    public static final String COL_PAYMENT_USERNAME = "username";
    public static final String COL_PAYMENT_AMOUNT = "amount";
    public static final String COL_PAYMENT_METHOD = "method";
    public static final String COL_PAYMENT_REFERENCE = "reference";
    public static final String COL_PAYMENT_IMAGE = "image";
    public static final String COL_PAYMENT_STATUS = "status";
    public static final String COL_PAYMENT_DATE = "date";

    // Notifications Table
    public static final String COL_NOTIF_ID = "id";
    public static final String COL_NOTIF_TENANT_ID = "tenant_id";
    public static final String COL_NOTIF_TITLE = "title";
    public static final String COL_NOTIF_MESSAGE = "message";
    public static final String COL_NOTIF_TYPE = "type";
    public static final String COL_NOTIF_DATE = "date";
    public static final String COL_NOTIF_IS_READ = "is_read"; // 0 = unread, 1 = read

    // Maintenance Table

    public static final String COL_MAINTENANCE_ID = "id";
    public static final String COL_MAINTENANCE_TENANT_ID = "tenant_id";
    public static final String COL_MAINTENANCE_ISSUE = "issue";
    public static final String COL_MAINTENANCE_DESC = "description";
    public static final String COL_MAINTENANCE_STATUS = "status";
    public static final String COL_MAINTENANCE_DATE = "date";

    // Places Table Columns
    public static final String COL_PLACE_ID = "id";
    public static final String COL_PLACE_NAME = "name";
    public static final String COL_PLACE_ADDRESS = "address";
    public static final String COL_PLACE_PRICE = "price";
    public static final String COL_PLACE_TYPE = "type";
    public static final String COL_PLACE_RATING = "rating";
    public static final String COL_PLACE_IMAGE = "image";


    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_ROOM_ID = "room_id";
    public static final String COL_USER_STATUS = "status";
    public static final String COL_USER_ROLE = "role";





    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USER_NAME + " TEXT," +          // NEW
                COL_USER_USERNAME + " TEXT UNIQUE," +
                COL_USER_PASSWORD + " TEXT," +
                COL_USER_CONTACT + " TEXT," +
                COL_USER_ROOM_ID + " INTEGER," +    // NEW
                COL_USER_STATUS + " TEXT DEFAULT 'Active'," + // NEW
                COL_USER_ROLE + " TEXT DEFAULT 'tenant'" + // NEW
                ")");

        // Tenants Table
        db.execSQL("CREATE TABLE " + TABLE_TENANTS + " (" +
                COL_TENANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TENANT_USERNAME + " TEXT," +
                COL_TENANT_ROOM + " TEXT," +
                COL_TENANT_RENT + " TEXT," +
                COL_TENANT_DUE_DATE + " TEXT," +
                COL_TENANT_CONTACT + " TEXT)");

        // Rooms Table
        db.execSQL("CREATE TABLE " + TABLE_ROOMS + " (" +
                COL_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ROOM_PROPERTY + " TEXT," +
                COL_ROOM_NAME + " TEXT," +
                COL_ROOM_TYPE + " TEXT," +
                COL_ROOM_CAPACITY + " INTEGER," +
                COL_ROOM_OCCUPIED + " INTEGER)");

        // Bookings Table
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_BOOKING_USERNAME + " TEXT," +
                COL_BOOKING_PROPERTY + " TEXT," +
                COL_BOOKING_LOCATION + " TEXT," +
                COL_BOOKING_CHECKIN + " TEXT," +
                COL_BOOKING_CHECKOUT + " TEXT," +
                COL_BOOKING_TOTAL + " INTEGER)");

        // Payments Table
        db.execSQL("CREATE TABLE " + TABLE_PAYMENTS + " (" +
                COL_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PAYMENT_USERNAME + " TEXT," +
                COL_PAYMENT_AMOUNT + " TEXT," +
                COL_PAYMENT_METHOD + " TEXT," +
                COL_PAYMENT_REFERENCE + " TEXT," +
                COL_PAYMENT_IMAGE + " TEXT," +
                COL_PAYMENT_STATUS + " TEXT," +
                COL_PAYMENT_DATE + " TEXT)");

        // ✅ NOTIFICATIONS TABLE (CORRECT & COMPLETE)
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " (" +
                COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTIF_TENANT_ID + " INTEGER, " +
                COL_NOTIF_TITLE + " TEXT, " +
                COL_NOTIF_MESSAGE + " TEXT, " +
                COL_NOTIF_TYPE + " TEXT, " +
                COL_NOTIF_DATE + " TEXT, " +
                COL_NOTIF_IS_READ + " INTEGER DEFAULT 0" +
                ")");

        // Maintenance Table
        // CREATE MAINTENANCE TABLE
        db.execSQL("CREATE TABLE " + TABLE_MAINTENANCE + " (" +
                COL_MAINTENANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_MAINTENANCE_TENANT_ID + " INTEGER," +
                COL_MAINTENANCE_ISSUE + " TEXT," +
                COL_MAINTENANCE_DESC + " TEXT," +
                COL_MAINTENANCE_STATUS + " TEXT DEFAULT 'Pending'," +
                COL_MAINTENANCE_DATE + " TEXT," +
                "FOREIGN KEY(" + COL_MAINTENANCE_TENANT_ID + ") REFERENCES " + TABLE_USERS + "(id))");

        // Places Table
        db.execSQL("CREATE TABLE " + TABLE_PLACES + " (" +
                COL_PLACE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PLACE_NAME + " TEXT," +
                COL_PLACE_ADDRESS + " TEXT," +
                COL_PLACE_PRICE + " TEXT," +
                COL_PLACE_TYPE + " TEXT," +
                COL_PLACE_RATING + " TEXT," +
                COL_PLACE_IMAGE + " INTEGER DEFAULT " + R.drawable.bedspace + ")");

        insertInitialRooms(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOMS, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                if (count == 0) {
                    insertInitialRooms(db);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertInitialRooms(SQLiteDatabase db) {
        String insertQuery = "INSERT INTO " + TABLE_ROOMS +
                " (" + COL_ROOM_PROPERTY + ", " + COL_ROOM_NAME + ", " + COL_ROOM_TYPE + ", " + COL_ROOM_CAPACITY + ", " + COL_ROOM_OCCUPIED + ") VALUES " +
                "('SD Dorm 2', 'Room 1 - 2F', 'Bedspace', 4, 0)," +
                "('SD Dorm 2', 'Room 2 - 2F', 'Bedspace', 4, 0)," +
                "('SD Dorm 2', 'Room 3 - 2F', 'Bedspace', 4, 0)," +
                "('SD Dorm 2', 'Room 4 - 2F', 'Bedspace', 4, 0)," +
                "('SD Dorm 2', 'Room 1 - 3F', 'Transient (Electric Fan)', 1, 0)," +
                "('SD Dorm 2', 'Room 2 - 3F', 'Transient (Electric Fan)', 1, 0)," +
                "('SD Dorm 2', 'Room 3 - 3F', 'Transient (Electric Fan)', 1, 0)," +
                "('SD Dorm 2', 'Room 4 - 3F', 'Transient (Electric Fan)', 1, 0)," +
                "('Palar Dormitory', 'Room 1 - 1F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 2 - 1F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 1 - 2F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 2 - 2F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 3 - 2F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 1 - 3F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 2 - 3F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 3 - 3F', 'Bedspace', 4, 0)," +
                "('Palar Dormitory', 'Room 1 - 4F', 'Transient', 1, 0)," +
                "('Palar Dormitory', 'Room 2 - 4F', 'Transient', 1, 0)," +
                "('Milflores', 'Room 1 - 1F', 'Bedspace', 2, 0)," +
                "('Milflores', 'Room 2 - 1F', 'Bedspace', 2, 0)," +
                "('Milflores', 'Room 1 - 2F', 'Bedspace', 2, 0)," +
                "('Milflores', 'Room 2 - 2F', 'Bedspace', 2, 0)," +
                "('Milflores', 'Room 3 - 2F', 'Bedspace', 2, 0)," +
                "('Milflores', 'Room 4 - 2F', 'Bedspace', 2, 0)," +
                "('Milflores', 'Room 5 - 2F', 'Bedspace', 2, 0)," +
                "('Loft 22', 'Room 1 - 3F', 'Transient (Aircon)', 1, 0)," +
                "('Loft 22', 'Room 2 - 3F', 'Transient (Aircon)', 1, 0)," +
                "('Loft 22', 'Room 1 - 4F', 'Transient (Aircon)', 1, 0)," +
                "('Loft 22', 'Room 2 - 4F', 'Transient (Aircon)', 1, 0)," +
                "('Loft 22', 'Room 1 - 5F', 'Transient (Aircon)', 1, 0)";

        db.execSQL(insertQuery);
    }

    // ✅ FUNCTION TO ADD NEW NOTIFICATION (ready to use)
    public void addNotification(int tenantId, String title, String message, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIF_TENANT_ID, tenantId);
        values.put(COL_NOTIF_TITLE, title);
        values.put(COL_NOTIF_MESSAGE, message);
        values.put(COL_NOTIF_TYPE, type);
        values.put(COL_NOTIF_DATE, new SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault()).format(new Date()));
        values.put(COL_NOTIF_IS_READ, 0); // new message = unread
        db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables to apply new structure
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TENANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINTENANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }

    public void resetDatabase(SQLiteDatabase db) {
        onUpgrade(db, DB_VERSION, DB_VERSION + 1);
    }
}