package com.szhua.androidxposed.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * AndroidXposed
 * Create   2017/1/6 17:10;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public class DbManager {

    private DbHelper dbHelper ;


    /*获取单例*/
   private DbManager(Context context){

       if(dbHelper==null){
          dbHelper =new DbHelper(context);
       }
   }
    private static  class SingleTon {
        private static  final DbManager getInstance(Context context){
            return  new DbManager(context);
        }
    }
    public static DbManager getDbManager(Context context){
        return  SingleTon.getInstance(context);
    }


/*provide cursor for outside apps*/
  public Cursor getTagCusor(){
    String sql ="select id ,tag ,is_show from "+DbHelper.TABLE_NAME;
    Cursor cusor =dbHelper.getWritableDatabase().rawQuery(sql,null);
    return  cusor ;

  }

  public ArrayList<Tag> getDatas (){
      ArrayList<Tag> tags =new ArrayList<>() ;
      String sql ="select id , tag ,is_show from "+DbHelper.TABLE_NAME;
      SQLiteDatabase sqLiteDatabase= dbHelper.getWritableDatabase() ;
      Cursor cusor =sqLiteDatabase.rawQuery(sql,null);
      while (cusor.moveToNext()){
          String tagName =cusor.getString(cusor.getColumnIndex("tag"));
          int  isShow =cusor.getInt(cusor.getColumnIndex("is_show")) ;
          int id =cusor.getInt(cusor.getColumnIndex("id"));
          Tag tag =new Tag(id,tagName,isShow) ;
          tags.add(tag) ;
      }
      cusor.close();
      sqLiteDatabase.close();
      return  tags ;

  }


 public void insert(ContentValues contentValues){

     SQLiteDatabase sqLiteDatabase =  dbHelper.getWritableDatabase() ;
     sqLiteDatabase.insert(DbHelper.TABLE_NAME,null,contentValues);
     sqLiteDatabase.close();

 }

    public  int  delete( String selection, String[] selectionArgs){
      return   dbHelper.getWritableDatabase().delete(DbHelper.TABLE_NAME,selection,selectionArgs);
    }

    public int update(ContentValues contentValues, String s, String[] strings){
        SQLiteDatabase sqLiteDatabase =dbHelper.getReadableDatabase() ;
        int result =sqLiteDatabase.update(DbHelper.TABLE_NAME,contentValues,s,strings) ;
        sqLiteDatabase.close();
        return  result;
    }


   /*get tag string by cursor for outside apps*/
   public String getTagByCursor(Cursor cursor){

       ArrayList<Tag> tags =new ArrayList<>();
       while (cursor.moveToNext()){
       String tag_string = cursor.getString(cursor.getColumnIndex("tag"));
       int is_show =cursor.getInt(cursor.getColumnIndex("is_show")) ;
       int id =cursor.getInt(cursor.getColumnIndex("id")) ;
       Tag tag =new Tag(id,tag_string,is_show);
       tags.add(tag);
       }
       if(!tags.isEmpty()){
           Tag tag =tags.get(0) ;
           if(tag.getIs_show()==1){
               return  tag.getTag();
           }
       }
    cursor.close();

       return  null ;
   }













}
