package com.suyati.mapstrackingcurrentlocationfinal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by suyati on 2/15/17.
 */

public class MapsDatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "SoCXO.db";
    //    public static final int DATABASE_VERSION = 6; //October 21
    public static final int DATABASE_VERSION = 6; //New field bookmark added

    private static MapsDatabaseHelper mInstancedb = null;
    private static SQLiteDatabase db;
    private Context context;

    public MapsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static MapsDatabaseHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)*/
        if (mInstancedb == null) {
            mInstancedb = new MapsDatabaseHelper(ctx.getApplicationContext());
        }
        return mInstancedb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LATLNG_WITH_TIME_TABLE = "CREATE TABLE " + MapsContractClass.LatLngWithTime.TABLE_NAME + " (" +
                MapsContractClass.LatLngWithTime._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MapsContractClass.LatLngWithTime.LATITUDE + " REAL NOT NULL, " +
                MapsContractClass.LatLngWithTime.LONGITUDE + " REAL NOT NULL, " +
                MapsContractClass.LatLngWithTime.ADDRESS + " TEXT NULL, " +
                MapsContractClass.LatLngWithTime.CITY + " TEXT NULL, " +
                MapsContractClass.LatLngWithTime.STATE + " TEXT NULL, " +
                MapsContractClass.LatLngWithTime.COUNTRY + " TEXT NULL, " +
                MapsContractClass.LatLngWithTime.POSTAL_CODE + " TEXT NULL, " +
                MapsContractClass.LatLngWithTime.KNOWN_NAME + " TEXT NULL, " +
                MapsContractClass.LatLngWithTime.IS_AN_HOUR + " NUMERIC NULL, " +
                MapsContractClass.LatLngWithTime.IS_A_STOP + " NUMERIC NULL, " +
                MapsContractClass.LatLngWithTime.DATE + " NUMERIC NULL, " +
                MapsContractClass.LatLngWithTime.TIME + " NUMERIC NULL " +
                " );";

        db.execSQL(SQL_CREATE_LATLNG_WITH_TIME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
