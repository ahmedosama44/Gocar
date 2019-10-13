package com.example.gocar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_Vehicles = "vehicles";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AGE = "age";
    private static final String KEY_PHONENUMBER = "phonenumber";
    private static final String KEY_NATIONALITY = "nationality";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    //
    private static final String KEY_VID = "id";
    private static final String KEY_MODELNAME = "modelname";
    private static final String KEY_PRODUCTIONYEAR = "productionyear";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_IMAGEPATH = "imagepath";
    private static final String KEY_FUELLEVEL = "fuellevel";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_AGE + " TEXT," + KEY_PHONENUMBER + " TEXT,"
                + KEY_NATIONALITY + " TEXT ," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_VEHICLE_TABLE = "CREATE TABLE " + TABLE_Vehicles + "("
                + KEY_VID + " Text," + KEY_MODELNAME + " TEXT,"
                + KEY_PRODUCTIONYEAR + " TEXT," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT,"
                + KEY_IMAGEPATH + " TEXT ," + KEY_FUELLEVEL + " TEXT"+ ")";
        db.execSQL(CREATE_VEHICLE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String age, String phonenumber, String nationality, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_AGE, age); // Name
        values.put(KEY_PHONENUMBER, phonenumber); // Email
        values.put(KEY_NATIONALITY, nationality);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at); // Created At
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    /* Adding a vehicle*/
    public void addvehicle(String id, String modelname, String productionyear, String latitude, String longitude, String imagepath, String fuellevel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VID, id); // Name
        values.put(KEY_MODELNAME, modelname); // Email
        values.put(KEY_PRODUCTIONYEAR, productionyear); // Name
        values.put(KEY_LATITUDE, latitude); // Email
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_IMAGEPATH, imagepath);
        values.put(KEY_FUELLEVEL, fuellevel); // Created At
        // Inserting Row
        long di = db.insert(TABLE_Vehicles, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + di);
    }


    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
    public void retrievevehicles(ArrayList<String> Id,ArrayList<String> Modelname, ArrayList<String> Productionyear, ArrayList<String> Latitude, ArrayList<String> Longitude, ArrayList<String> Imagepath, ArrayList<String> Fuellevel) {
        String selectQuery = "SELECT  * FROM " + TABLE_Vehicles;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int size=count();
        for(int i=1;i<=size;i++) {
            Id.add(cursor.getString(0));
            Modelname.add(cursor.getString(1));
            Productionyear.add(cursor.getString(2));
            Latitude.add(cursor.getString(3));
            Longitude.add(cursor.getString(4));
            Imagepath.add(cursor.getString(5));
            Fuellevel.add(cursor.getString(6));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
    }
    public int count() {
        int c=0;
        String sql = "SELECT COUNT(*) FROM " + TABLE_Vehicles;
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        if( cursor.getCount()> 0){
            cursor.moveToFirst();
            c= cursor.getInt(0);
        }
        cursor.close();
        return c;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}