package comsri.gps_buzz;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DBAdapter {

    static final String KEY_ID3 = "_id3";

    static final String KEY_ALARM = "name";
    static final String KEY_REMINDER = "reminder";

    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "App";
    static final String DATABASE_TABLE3 = "alarm";
    static final String DATABASE_TABLE4 = "tutorial";


    static final String DATABASE_CREATE = "CREATE TABLE alarm (_id3 INTEGER PRIMARY KEY autoincrement, name TEXT);CREATE TABLE tutorial (_id3 INTEGER PRIMARY KEY, yes NUMERIC);";

    static final int DATABASE_VERSION = 1;
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    public ArrayList<String> alarmnames=new ArrayList<String>();

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            System.out.println("oncreate");
            try {

   /*           db.execSQL(DATABASE_CREATE);

                ContentValues initialValues = new ContentValues();
                initialValues.put("_id3","1");
                initialValues.put("yes","1");
                db.insert(DATABASE_TABLE4, null, initialValues);

                initialValues = new ContentValues();
                initialValues.put("_id3","2");
                initialValues.put("yes","1");
                db.insert(DATABASE_TABLE4, null, initialValues);

                initialValues = new ContentValues();
                initialValues.put("_id3","3");
                initialValues.put("yes","1");
                db.insert(DATABASE_TABLE4, null, initialValues);

                initialValues = new ContentValues();
                initialValues.put("_id3","4");
                initialValues.put("yes","1");
                db.insert(DATABASE_TABLE4, null, initialValues);
*/
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS alarm;DROP TABLE IF EXISTS tutorial;");
            onCreate(db);
        }
    }
    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }


    //---insert a contact into the database---
    public long addAlarm(String alarm,String reminder)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ALARM, alarm);
        initialValues.put(KEY_REMINDER, reminder);
        return db.insert(DATABASE_TABLE3, null, initialValues);
    }


    public ArrayList<String> getAllAlarms()
    {
        Cursor c=db.query(DATABASE_TABLE3, new String[] {KEY_ALARM}, null, null, null, null, null);
        if(c.getCount()==0)
        {
            return null;
        }
            else
        {
            c.moveToFirst();
            while(!c.isLast())
            {
                alarmnames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
            alarmnames.add(c.getString(c.getColumnIndex("name")));
            c.close();
            return alarmnames;
        }
    }

    public int deleteAlarm(String val)
    {
        Cursor c=db.query(DATABASE_TABLE3, new String[] {KEY_ID3}, KEY_ALARM+"=\""+val+"\"", null, null, null, null);
        c.moveToFirst();
        int i=c.getInt(c.getColumnIndex("_id3"));
        db.delete(DATABASE_TABLE3, KEY_ALARM + "=\"" +val+"\"", null);
        c.close();
        return i;
    }


    public boolean notPresent(String alarm)
    {
        Cursor c=db.query(DATABASE_TABLE3, new String[] {KEY_ID3}, KEY_ALARM+"=\""+alarm+"\"", null, null, null, null);
        if(c.getCount()==0)
        {
            c.close();
            return true;

        }
        else
        {
            c.close();
            return false;
        }
    }

    public boolean tutorialcheck(int i)
    {
        Cursor c=db.query(DATABASE_TABLE4, new String[] {"yes"}, KEY_ID3+"=\""+i+"\"", null, null, null, null);
        c.moveToFirst();
        int k=c.getInt(c.getColumnIndex("yes"));
        if(k==1)
            return true;
        else
            return false;
    }

    public void settutorial(int which,int i)
    {
        ContentValues args = new ContentValues();
        args.put("yes",i);
        db.update(DATABASE_TABLE4,args,"_id3="+"\""+which+"\"",null);
    }
}