package com.example.ridesafedatacollection;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "myDatabase";
    private static final String TAG = "DatabaseHelper";

    // sensor table
    private static final String SENSOR_VALUES = "sensor_values";
    private static final String GFORCE = "gforce";
    private static final String GX = "gx";
    private static final String GY = "gy";
    private static final String GZ = "gz";
    private static final String SPEED = "speed";
    private static final String KEY_ID = "id";


    public DatabaseHelper(Context context) {
        super(context, DATABASENAME, null, 9);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createSensorTable = "CREATE TABLE " + SENSOR_VALUES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + GFORCE + " REAL,"
                + GX + " REAL,"
                + GY + " REAL,"
                + GZ + " REAL,"
                + SPEED + " REAL" + ");";


        db.execSQL(createSensorTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SENSOR_VALUES);

        onCreate(db);

    }

    public boolean addData(String item, String name, String col) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col, item);
        long result = db.insert(name, null, contentValues);
        return result != -1;
    }


    public Cursor getData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + name;
        return db.rawQuery(query, null);
    }

    public void deleteData(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + tableName);

    }


    public void addRow(ContentValues cv, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(tableName, null, cv);

    }


    public String getDBname() {

        return DATABASENAME;

    }


}


