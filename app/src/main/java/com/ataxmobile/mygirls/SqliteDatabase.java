package com.ataxmobile.mygirls;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SqliteDatabase extends SQLiteOpenHelper {
    private	static final int DATABASE_VERSION =	6;
    private	static final String	DATABASE_NAME = "girl";
    private	static final String TABLE_CONTACTS = "girls";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "girlname";
    private static final String COLUMN_NO1 = "duration1";
    private static final String COLUMN_NO2 = "duration2";
    private static final String COLUMN_FD = "firstday";

    // constructor
    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_CONTACTS_TABLE = "CREATE	TABLE " + TABLE_CONTACTS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," + COLUMN_NO1 + " INTEGER, " +
                    COLUMN_NO2 + " INTEGER, " + COLUMN_FD + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // hope, onUpgrade method won't be needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // return list of girls
    public ArrayList<Girls> listContacts(){
        String sql = "select * from " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Girls> storeContacts = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                int D1 = Integer.parseInt(cursor.getString(2));
                int D2 = Integer.parseInt(cursor.getString(3));
                long FD = Long.parseLong(cursor.getString(4));
                storeContacts.add(new Girls(id, name, D1, D2, FD));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeContacts;
    }

    // return list of girls names
    public List<String> getAllNames(){
        List<String> names = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT " + COLUMN_NAME + " FROM " + TABLE_CONTACTS ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning names
        return names;
    }

    public int addContacts(Girls girl){
        Girls locG = null;
        locG = findContacts(0, girl.getName());
        if (locG == null) {
            // we didn't find girl with the given name, so insert new row
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, girl.getName());
            values.put(COLUMN_NO1, girl.getD1());
            values.put(COLUMN_NO2, girl.getD2());
            values.put(COLUMN_FD, girl.getFD());

            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_CONTACTS, null, values);
            return 0;
        } else {
            // girls with the given name already exists, so return error
            return -1;
        }
    }

    public int updateContacts(Girls girl){
        Girls locG = null;
        locG = findContacts(0, girl.getName());
        boolean canUpd = false;
//Log.i("-update, g.name=", girl.getName() + ",id=" + String.valueOf(girl.getId()));
//Log.i("-update, found name=", locG.getName() + ",id=" + String.valueOf(locG.getId()));
        // no girl with given name, can update
        if(locG==null) canUpd = true;
        else {
            if(locG.getId() == girl.getId()) canUpd = true;
            else canUpd = false;
        }
        if(canUpd) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, girl.getName());
            values.put(COLUMN_NO1, girl.getD1());
            values.put(COLUMN_NO2, girl.getD2());
            values.put(COLUMN_FD, girl.getFD());
            SQLiteDatabase db = this.getWritableDatabase();
            db.update(TABLE_CONTACTS, values, COLUMN_ID + "	= ?", new String[]{String.valueOf(girl.getId())});
            return 0;
        } else {
            return -1;
        }
    }

    public void deleteContact(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    // return object Girls with given name
    public Girls findContacts(int gid, String name){
        String query;
        if(name.equals(""))
            query = "Select * FROM "	+ TABLE_CONTACTS + " WHERE " + COLUMN_ID + " = " + String.valueOf(gid);
        else
            query = "Select * FROM "	+ TABLE_CONTACTS + " WHERE " + COLUMN_NAME + " = '" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Girls contacts = null;
        Cursor cursor = db.rawQuery(query,	null);
        if	(cursor.moveToFirst()){
            int id = Integer.parseInt(cursor.getString(0));
            String contactsName = cursor.getString(1);
            int contactsD1 = Integer.parseInt(cursor.getString(2));
            int contactsD2 = Integer.parseInt(cursor.getString(3));
            long contactsFD = Long.parseLong(cursor.getString(4));
            contacts = new Girls(id, contactsName, contactsD1, contactsD2, contactsFD);
        }
        cursor.close();
        return contacts;
    }

    public int getIdByName(String n) {
        int id = -1;
        String query = "Select * FROM "	+ TABLE_CONTACTS + " WHERE " + COLUMN_NAME + " = '" + n + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,	null);
        if	(cursor.moveToFirst()){
            id = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        return id;
    }
}
