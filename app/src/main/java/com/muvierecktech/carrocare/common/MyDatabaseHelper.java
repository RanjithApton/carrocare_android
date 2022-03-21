package com.muvierecktech.carrocare.common;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.muvierecktech.carrocare.model.DBModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "carrocare_new.db";

    public static final String TABLE_NAME = "cart_table";

    public static final String ACTION = "type";
    public static final String IMG = "imge";
    public static final String MODEL = "model";
    public static final String NUMBER = "number";
    public static final String CAR_PRICE = "carprice";
    public static final String CAR_ID = "carid";
    public static final String P_MONTHS = "paidmonth";
    public static final String FINE = "fine";
    public static final String GST_AMOUNT = "gst_amount";
    public static final String TOTAL = "total";
    public static final String SUB_TOTAL = "sub_total";
    public static final String SCH_DATE = "date";
    public static final String SCH_TIME = "time";

    public static DecimalFormat decimalformatData = new DecimalFormat("0.00");


    private String OrderTableInfo = TABLE_NAME + "(" +
            ACTION + " TEXT ," +
            IMG + " TEXT ," +
            MODEL + " TEXT ," +
            NUMBER + " TEXT ," +
            CAR_PRICE + " TEXT ," +
            CAR_ID + " TEXT ," +
            P_MONTHS + " TEXT ," +
            FINE + " TEXT ," +
            GST_AMOUNT + " TEXT ," +
            TOTAL + " TEXT ," +
            SUB_TOTAL + " TEXT ," +
            SCH_DATE + " TEXT ," +
            SCH_TIME + " TEXT )";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + OrderTableInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        replaceDataToNewTable(db, TABLE_NAME, OrderTableInfo);
        onCreate(db);
    }

    private void replaceDataToNewTable(SQLiteDatabase db, String tableName, String tableString) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableString);

        List<String> columns = getColumns(db, tableName);
        db.execSQL("ALTER TABLE " + tableName + " RENAME TO temp_" + tableName);
        db.execSQL("CREATE TABLE " + tableString);

        columns.retainAll(getColumns(db, tableName));
        String cols = join(columns, ",");
        db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s from temp_%s",
                tableName, cols, cols, tableName));
        db.execSQL("DROP TABLE temp_" + tableName);
    }

    private List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;
        try {
            c = db.rawQuery("select * from " + tableName + " limit 1", null);
            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    private String join(List<String> list, String divider) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(divider);
            buf.append(list.get(i));
        }
        return buf.toString();
    }

    public int getTotalItemOfCart() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
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
                DBModel cartList = new DBModel();
                cartList.setType(cursor.getString(0));
                cartList.setImge(cursor.getString(1));
                cartList.setModel(cursor.getString(2));
                cartList.setNumber(cursor.getString(3));
                cartList.setCarprice(cursor.getString(4));
                cartList.setCarid(cursor.getString(5));
                cartList.setPaidmonth(cursor.getString(6));
                cartList.setFine(cursor.getString(7));
                cartList.setGstamount(cursor.getString(8));
                cartList.setTotal(cursor.getString(9));
                cartList.setSub_total(cursor.getString(10));
                cartList.setDate(cursor.getString(11));
                cartList.setTime(cursor.getString(12));

                arrayList.add(cartList);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }


    public void AddOrderData(String type,
                             String imge,
                             String model,
                             String number,
                             String carprice,
                             String carid,
                             String paidmonth,
                             String fine,
                             String gst_amount,
                             String total,
                             String sub_total,
                             String date,
                             String time) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ACTION, type);
            values.put(IMG, imge);
            values.put(MODEL, model);
            values.put(NUMBER, number);
            values.put(CAR_PRICE, carprice);
            values.put(CAR_ID, carid);
            values.put(P_MONTHS, paidmonth);
            values.put(FINE, fine);
            values.put(GST_AMOUNT, gst_amount);
            values.put(TOTAL, total);
            values.put(SUB_TOTAL, sub_total);
            values.put(SCH_DATE, date);
            values.put(SCH_TIME, time);

            db.insert(TABLE_NAME, null, values);
            db.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UpdateOrderData(String type,
                                String carid,
                                String paidmonth,
                                String fine,
                                String gst_amount,
                                String total,
                                String sub_total,
                                String date,
                                String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(P_MONTHS, paidmonth);
        values.put(FINE, fine);
        values.put(GST_AMOUNT, gst_amount);
        values.put(TOTAL, total);
        values.put(SUB_TOTAL, sub_total);
        values.put(SCH_DATE, date);
        values.put(SCH_TIME, time);

        db.update(TABLE_NAME, values, ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{type, carid});
        db.close();
    }

    public void DeleteOrderData(String type, String carid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{type, carid});
        database.close();
    }

    public void DeleteAllOrderData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME);
        database.close();
    }

    public String AddUpdateOrder(String type,
                                 String imge,
                                 String model,
                                 String number,
                                 String carprice,
                                 String carid,
                                 String paidmonth,
                                 String fine,
                                 String gstamount,
                                 String total,
                                 String sub_total,
                                 String date,
                                 String time) {

        int qty = Integer.parseInt(CheckOrderExists(type, carid));

        if (qty == 0) {
            AddOrderData(type+"", imge+"", model+"", number+"", carprice+"", carid+"", paidmonth+"", fine+"", gstamount+"",total+"", sub_total+"", date+"", time+"");
        } else {
            UpdateOrderData(type+"", carid+"", paidmonth+"", fine+"", gstamount+"", total+"", sub_total+"", date+"", time+"");
        }
        return "1";
    }

    public String CheckOrderExists(String action, String carid) {
        String count = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{action, carid});
        if (cursor.moveToFirst()) {
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{action, carid});
        }
        cursor.close();
        db.close();

        return count;
    }

    public ArrayList<String> getCartList() {
        final ArrayList<String> ids = new ArrayList<>();
        String selectQuery = "SELECT *  FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String count = cursor.getString(cursor.getColumnIndex(CAR_PRICE));
                if (count.equals("0")) {
                    db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ACTION + " = ? AND " + CAR_ID + " = ?", new String[]{cursor.getString(cursor.getColumnIndexOrThrow(ACTION)), cursor.getString(cursor.getColumnIndexOrThrow(CAR_ID))});

                } else
                    ids.add(cursor.getString(cursor.getColumnIndexOrThrow(ACTION)) + "=" + cursor.getString(cursor.getColumnIndexOrThrow(CAR_ID)) + "=" + cursor.getString(cursor.getColumnIndexOrThrow(CAR_PRICE)) + "=" + cursor.getDouble(cursor.getColumnIndexOrThrow(TOTAL)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return ids;
    }

}
