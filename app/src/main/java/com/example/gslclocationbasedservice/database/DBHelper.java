package com.example.gslclocationbasedservice.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MapsDB";
    private static final int DB_VERSION = 2;

    public static final String TABLE_MARKER = "markers";
    public static final String FIELD_MARKER_ID = "id";
    public static final String FIELD_MARKER_LATITUDE = "latitude";
    public static final String FIELD_MARKER_LONGITUDE = "longitude";

    private static final String CREATE_TABLE_MARKER =
            "CREATE TABLE " + TABLE_MARKER + "(" +
                    FIELD_MARKER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FIELD_MARKER_LATITUDE + " DOUBLE," +
                    FIELD_MARKER_LONGITUDE + " DOUBLE)";

    private static final String DROP_TABLE_MARKER = "DROP TABLE IF EXISTS " + TABLE_MARKER;


    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MARKER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE_MARKER);
        onCreate(db);
    }
}
