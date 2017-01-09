package com.szhua.androidxposed.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * AndroidXposed
 * Create   2017/1/6 17:11;
 * https://github.com/szhua
 * @author sz.hua
 */
public class DbHelper  extends SQLiteOpenHelper{

     public static  final String TABLE_NAME= "intention";
     private static  final String DB_NAME ="intention.db";

     public DbHelper(Context context){
         super(context,DB_NAME,null,1);
     }

     public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql ="create table intention (id INTEGER PRIMARY KEY,tag varchar(40) not null ,is_show  int );";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
