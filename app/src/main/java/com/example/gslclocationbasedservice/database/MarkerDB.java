package com.example.gslclocationbasedservice.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gslclocationbasedservice.model.Position;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MarkerDB{
    DBHelper dbHelper;

    public MarkerDB(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public int insertMarker(LatLng point){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.FIELD_MARKER_LATITUDE, point.latitude);
        cv.put(DBHelper.FIELD_MARKER_LONGITUDE, point.longitude);
        int id = (int)db.insert(DBHelper.TABLE_MARKER, null, cv);
        db.close();
        return id;
    }

    public List<Position> getAllMarkers(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_MARKER, null);
        List<Position> positionsList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.FIELD_MARKER_ID));
                double latitude =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.FIELD_MARKER_LATITUDE));
                double longitude =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.FIELD_MARKER_LONGITUDE));
                positionsList.add(new Position(id, new LatLng(latitude, longitude)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return positionsList;
    }

    public void deleteMarker(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_MARKER, "id=?", new String[]{id});
        db.close();
    }
}
