package com.example.eider.bracadatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eider on 05/04/2017.
 */

public class AdminDB extends SQLiteOpenHelper {

    private static final String DBNAME = "envint.db";
    private static final int VERSIONDB = 1;
    private SQLiteDatabase DB;


    private final static String TABLE_NAME = "tbl_envint";
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



    public AdminDB(Context context) {
        super(context, DBNAME, null, VERSIONDB);
    }



    String CREATE_TABLE = "CREATE TABLE"+ TABLE_NAME+"("+
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
            MAC1+" TEXT NOT NULL, "+
            RSSI1+" TEXT NOT NULL, "+
            MAC2+" TEXT NOT NULL, "+
            RSSI2+" TEXT NOT NULL, "+
            MAC3+" TEXT NOT NULL, "+
            RSSI3+" TEXT NOT NULL, "+
            MAC4+" TEXT NOT NULL, "+
            RSSI4+" TEXT NOT NULL, "+
            MAC5+" TEXT NOT NULL, "+
            RSSI5+" TEXT NOT NULL);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP DATABASE IF EXIST TBLCONTACTOS ");
        //db.execSQL(sql);

    }

    public void insert(SQLiteDatabase db, String maddress1, String rss1,
                       String maddress2, String rss2,
                       String maddress3, String rss3,
                       String maddress4, String rss4,
                       String maddress5, String rss5){

        String SQL = "insert into "+TABLE_NAME+"("
                +MAC1+"," +RSSI1+","
                +MAC2+"," +RSSI2+","
                +MAC3+"," +RSSI3+","
                +MAC4+"," +RSSI4+","
                +MAC5+"," +RSSI5+")"
                +" values('"+maddress1+"','"+rss1+
                "','"+maddress2+"','"+rss2+
                "','"+maddress3+"','"+rss3+
                "','"+maddress4+"','"+rss4+
                "','"+maddress5+"','"+rss5+"');";
        db.execSQL(SQL);
        db.close();
    }



}
