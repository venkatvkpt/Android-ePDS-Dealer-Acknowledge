package in.gov.bihar.sfc.biharepds.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.gov.bihar.sfc.biharepds.model.DealerInformation;

/**
 * Created by VR on 12/29/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DEALER_INFORMATION.db";

    // Fps table name
    private static final String TABLE_NAME = "FPS_DEALER_INFO";

    // Fps Table Columns names
    private static final String COLUMN_FPS_CODE = "FPS_CODE";
    private static final String COLUMN_FPS_DEALER_NAME = "FPS_DEALER_NAME";
    private static final String COLUMN_FPS_LICENSE_AUTH_NUMBER = "FPS_LICENSE_AUTH_NUMBER";
    private static final String COLUMN_LOCALITY_MARKET_NAME = "LOCALITY_MARKET_NAME";
    private static final String COLUMN_DISTRICT_ID = "DISTRICT_ID";
    private static final String COLUMN_BLOCK_ID = "BLOCK_ID";
    private static final String COLUMN_PANCHAYAT_ID = "PANCHAYAT_ID";
    private static final String COLUMN_IS_ACTIVE= "IS_ACTIVE";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FPS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_FPS_CODE + " TEXT PRIMARY KEY, " + COLUMN_FPS_DEALER_NAME + " TEXT, "
                + COLUMN_FPS_LICENSE_AUTH_NUMBER + " TEXT, "
                + COLUMN_LOCALITY_MARKET_NAME + " TEXT, "
                + COLUMN_DISTRICT_ID + " TEXT, "
                + COLUMN_BLOCK_ID + " TEXT, "
                + COLUMN_PANCHAYAT_ID + " TEXT, "
                + COLUMN_IS_ACTIVE + " TEXT ); ";
        db.execSQL(CREATE_FPS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

   public void insertSigninDealer(String content) {
        SQLiteDatabase db = this.getWritableDatabase();

       String qry="INSERT INTO "+TABLE_NAME+" ("+COLUMN_FPS_CODE+", "+COLUMN_FPS_DEALER_NAME+", "+COLUMN_FPS_LICENSE_AUTH_NUMBER+", " +
               COLUMN_LOCALITY_MARKET_NAME+", "+COLUMN_DISTRICT_ID+", "+COLUMN_BLOCK_ID+", "+COLUMN_PANCHAYAT_ID+", "+COLUMN_IS_ACTIVE+") " +
               "VALUES "+content+";";
        db.execSQL(qry);
        db.close();
    }

    // Check  Dealer Count
    public boolean isAnyDealer() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt != 0;
    }

    // Deleting Dealer
    public void deleteSigninDealer() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

    // Getting Dealer Information
    public  DealerInformation getDealerinfo() {
        String selectQuery = "SELECT  "+COLUMN_FPS_DEALER_NAME+","+COLUMN_FPS_LICENSE_AUTH_NUMBER+","
                +COLUMN_LOCALITY_MARKET_NAME+" FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            DealerInformation info = new DealerInformation();
            info.setFps_dealer_name(cursor.getString(0));
            info.setFps_license_auth_number(cursor.getString(1));
            info.setLocality_market_name(cursor.getString(2));
            db.close();
            cursor.close();
            return info;
        }

        return null;
    }

    // Getting Dealer FPS EPDS CODE
    public  String getDealerCode() {
        String selectQuery = "SELECT  "+COLUMN_FPS_CODE+" FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String fpsCode =cursor.getString(0);
            db.close();
            cursor.close();
            return fpsCode;
        }

        return null;
    }

    // Getting Dealer Details like FPS Code, District Id and Block Id
    public  DealerInformation getDealerDetails() {
        String selectQuery = "SELECT  "+COLUMN_FPS_CODE+","+COLUMN_DISTRICT_ID+","+COLUMN_BLOCK_ID+","
                +COLUMN_PANCHAYAT_ID+" FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            DealerInformation info = new DealerInformation();
            info.setFps_code(cursor.getString(0));
            info.setDistrict_id(cursor.getString(1));
            info.setBlock_id(cursor.getString(2));
            info.setPanchayat_id(cursor.getString(3));
            db.close();
            cursor.close();
            return info;
        }

        return null;
    }
}