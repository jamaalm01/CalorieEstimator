package com.example.jamaal.imtocal;

/**
 * Created by Chaitanya on 8/1/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "Im2Cal.db";
    public static final String TABLE_NAME = "ProjInfoTable";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "PROJ_NAME";
    public static final String COL_3 = "STATUS";
    public static final String COL_4 = "ORIG_IMAGE_NAME";
    public static final String COL_5 = "INPUTSTRING";
    public static final String COL_6 = "OP_IMAGE_NAME";
    public static final String COL_7 = "OPSTRING";
    public static final String COL_8 = "DISP_NAME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //Log.d(LOG_TAG,"Going to delete database");
        //boolean bool = context.deleteDatabase(DATABASE_NAME);
        //Log.d(LOG_TAG,"is deleted: "+bool);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME+" ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_2+" TEXT,"+COL_3+" TEXT,"+COL_4+" TEXT,"+COL_5+" TEXT,"+COL_6+" TEXT,"+COL_7+" TEXT,"+COL_8+" TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String proj_name, String status , String origImName,String inputString,String dispName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,proj_name);
        contentValues.put(COL_3,status);
        contentValues.put(COL_4,origImName);
        contentValues.put(COL_5,inputString);
        contentValues.put(COL_8,dispName);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public Cursor viewProj(String projName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where "+COL_2+"=?",new String[] {projName});
        return res;
    }
    public Integer updateData(String proj_name, String status, String opImName, String opString){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_1,id);
        contentValues.put(COL_2,proj_name);
        contentValues.put(COL_3,status);
        //contentValues.put(COL_4,origImName);
        //contentValues.put(COL_5,inputString);
        contentValues.put(COL_6,opImName);
        contentValues.put(COL_7,opString);

        return db.update(TABLE_NAME,contentValues,"proj_name = ?",new String[] {proj_name});
    }

    public Integer deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        return 0;
    }
    public Integer deleteRow(String projName){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,COL_2+" = ?",new String[] {projName});

    }


    /**
     * Created by Chaitanya on 8/1/2016.
     */
    public static class DbTaskViewAll extends AsyncTask<String,String,Cursor> {

        private Context context;
        public DbTaskViewAll(Context context){
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return new DatabaseHelper(context).getAllData();
        }
    }
    public static class DbTaskViewProj extends AsyncTask<String,String,Cursor> {

        private Context context;
        public DbTaskViewProj(Context context){
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return new DatabaseHelper(context).viewProj(params[0]);
        }
    }
    /**
     * Created by Chaitanya on 8/1/2016.
     */
    public static class DbTaskInsert extends AsyncTask<String,String,String> {

        private Context context;
        public DbTaskInsert(Context context){
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            new DatabaseHelper(context).insertData(params[0],params[1],params[2],params[3],params[4]);
            return null;
        }
    }
    public static class DbTaskUpdate extends AsyncTask<String,String,String> {

        private Context context;
        public DbTaskUpdate(Context context){
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            new DatabaseHelper(context).updateData(params[0],params[1],params[2],params[3]);
            return null;
        }
    }
    public static class DbTaskDeleteAll extends AsyncTask<String,String,Integer> {

        private Context context;
        public DbTaskDeleteAll(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer val = new DatabaseHelper(context).deleteAllData();
            return val;
        }
    }
    public static class DbTaskDeleteProj extends AsyncTask<String,String,Integer> {

        private Context context;
        public DbTaskDeleteProj(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer val = new DatabaseHelper(context).deleteRow(params[0]);
            return val;
        }
    }
}
