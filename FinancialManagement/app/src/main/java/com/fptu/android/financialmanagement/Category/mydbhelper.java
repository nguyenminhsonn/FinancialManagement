package com.fptu.android.financialmanagement.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class mydbhelper extends SQLiteOpenHelper {
    private  Context context;
    public  static  final String DATABASE_NAME="FinancialManagement.db";
    public  static  final  int DATABASE_VESION=1;
    public static final  String TABLE_NAME ="Categories";
    private static final String COLUMN_UID = "UserID";
    private static final String COLUMN_ID = "CategoryID";
    private static final String COLUMN_TITLE = "CategoryName";
    private static final String COLUMN_PID = "ParentID";
    private static final String COLUMN_LEVEL = "Level";

    public mydbhelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VESION);
        this.context= context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                COLUMN_TITLE + " TEXT, " +
                COLUMN_PID + " INTEGER, " +
                COLUMN_LEVEL + " INTEGER,"+
                COLUMN_UID + " TEXT) " ;
        db.execSQL(query);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void addDB(String title, String PID, String level, String uID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_PID, PID);
        cv.put(COLUMN_LEVEL, level);
        cv.put(COLUMN_UID, uID);
        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){//fall to insert data
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readCateLatest(){
        String query = "SELECT * FROM " + TABLE_NAME +" WHERE "+COLUMN_ID + " = (SELECT MAX(" + COLUMN_ID + ") FROM "+TABLE_NAME+" )" ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String name, String PID, String level, String uID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, name);
        cv.put(COLUMN_PID, PID);
        cv.put(COLUMN_LEVEL, level);
        cv.put(COLUMN_UID, uID);

        long result = db.update(TABLE_NAME, cv, "CategoryId=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }


    }
    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "CategoryId=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
