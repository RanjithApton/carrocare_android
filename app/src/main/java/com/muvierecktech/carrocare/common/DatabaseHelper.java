package com.muvierecktech.carrocare.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.muvierecktech.carrocare.model.DBModel;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "carrocare.db";

    public static final String TABLE_NAME = "cart_table";

    public static final String S_NO = "sno";
    public static final String CUS_ID = "cusid";
    public static final String TOKEN = "token";
    public static final String ACTION = "type";
    public static final String IMG = "imge";
    public static final String MODEL = "model";
    public static final String NUMBER = "number";
    public static final String CAR_PRICE = "carprice";
    public static final String CAR_ID = "carid";
    public static final String P_MONTHS = "paidmonth";
    public static final String FINE = "fine";
    public static final String TOTAL = "total";
    public static final String SCH_DATE = "date";
    public static final String SCH_TIME = "time";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_NAME + " (sno INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cusid TEXT, " +
                "token TEXT, " +
                " type TEXT," +
                " imge TEXT," +
                " model TEXT," +
                " number TEXT," +
                "carprice TEXT, " +
                "carid TEXT, " +
                "paidmonth TEXT, " +
                "fine TEXT, " +
                "total TEXT , " +
                "date TEXT, " +
                "time TEXT ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addCart(String cus_id, String token,String action, String image,String model,String number,String car_price, String car_id, String paid_month, String fine_amount, String total_amount, String date, String time ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUS_ID,cus_id);
        contentValues.put(TOKEN,token);
        contentValues.put(ACTION,action);
        contentValues.put(IMG,image);
        contentValues.put(MODEL,model);
        contentValues.put(NUMBER,number);
        contentValues.put(CAR_PRICE,car_price);
        contentValues.put(CAR_ID,car_id);
        contentValues.put(P_MONTHS,paid_month);
        contentValues.put(FINE,fine_amount);
        contentValues.put(TOTAL,total_amount);
        contentValues.put(SCH_DATE,date);
        contentValues.put(SCH_TIME,time);
        return db.insert(TABLE_NAME,null,contentValues);
    }

    public ArrayList<DBModel> getItems() {
        ArrayList<DBModel> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME;

        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DBModel dbModel = new DBModel();
                dbModel.setSno(Integer.valueOf(cursor.getString(0)));
                dbModel.setCusid(cursor.getString(1));
                dbModel.setToken(cursor.getString(2));
                dbModel.setType(cursor.getString(3));
                dbModel.setImge(cursor.getString(4));
                dbModel.setModel(cursor.getString(5));
                dbModel.setNumber(cursor.getString(6));
                dbModel.setCarprice(cursor.getString(7));
                dbModel.setCarid(cursor.getString(8));
                dbModel.setPaidmonth(cursor.getString(9));
                dbModel.setFine(cursor.getString(10));
                dbModel.setTotal(cursor.getString(11));
                arrayList.add(dbModel);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    public int deleteItem(int sno) {
        SQLiteDatabase db = this.getWritableDatabase();
        //deleting row
        return db.delete(TABLE_NAME , "sno=?",new String[]{String.valueOf(sno)});
    }

    public int getTotalItemOfCart() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void DeleteAllCartData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME);
        database.close();
    }

    public String CheckOrderExists(String action, String carid) {

        String count = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{action, carid});
        if (cursor.moveToFirst()) {
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{action, carid});
//            count = cursor.getString(cursor.getColumnIndex(P_MONTHS));
//            if (count.equals("0")) {
//                db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
//
//            }
        }
        cursor.close();
        db.close();

        return count;
    }

    public double getTotalCartAmt() {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rawQuery = db.rawQuery(" SELECT SUM (" + TOTAL + ") FROM " + TABLE_NAME, null);
        rawQuery.moveToFirst();
        total = Integer.parseInt(String.valueOf(rawQuery.getInt(0)));
        db.close();
        return total;
    }


}
