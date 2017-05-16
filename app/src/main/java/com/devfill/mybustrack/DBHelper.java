package com.devfill.mybustrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class DBHelper extends SQLiteOpenHelper {

	public static final String LOG_TAG_DB = "dbLogs";
	
    public DBHelper(Context context) {
      // ����������� �����������
      super(context, "myDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
      Log.d(LOG_TAG_DB, "--- onCreate database ---");
      // ������� ������� � ������
      db.execSQL("create table mytable ("
          + "id integer primary key autoincrement," 
          + "routName text,"
          + "distance text,"
          + "duration text,"
          + "checkReminderStatus integer,"
          + "durationReal text" + ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }