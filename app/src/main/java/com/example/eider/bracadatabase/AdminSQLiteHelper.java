package com.example.eider.bracadatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eider on 28/06/2017.
 */


public class AdminSQLiteHelper extends SQLiteOpenHelper {

    private final static String TABLE_NAME = "beacon";
    private final static String MAC1 = "address1";
    private final static String RSSI1 = "rssi1";
    private final static String MAC2 = "address2";
    private final static String RSSI2 = "rssi2";
    private final static String MAC3 = "address3";
    private final static String RSSI3 = "rssi3";
    private final static String MAC4 = "address4";
    private final static String RSSI4 = "rssi4";
    private final static String MAC5 = "address5";
    private final static String RSSI5 = "rssi5";

    String CREATE_TABLE = "CREATE TABLE BeaconInfo("+
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "MAC1 TEXT NOT NULL, RSSI1 TEXT NOT NULL,"+
            "MAC2 TEXT NOT NULL, RSSI2 TEXT NOT NULL,"+
            "MAC3 TEXT NOT NULL, RSSI3 TEXT NOT NULL,"+
            "MAC4 TEXT NOT NULL, RSSI4 TEXT NOT NULL,"+
            "MAC5 TEXT NOT NULL, RSSI5 TEXT NOT NULL)";

    public AdminSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           db.execSQL("DROP TABLE IF EXITS BeaconInfo");
        onCreate(db);
    }
}
