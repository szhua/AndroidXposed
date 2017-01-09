package com.szhua.androidxposed.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * AndroidXposed
 * Create   2017/1/6 17:21;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public class IntentionProvider extends ContentProvider {

    //创建一个路径识别器
    //常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码,也就是说如果找不到匹配的类型,返回-1
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String AUTHORITY="com.szhua.androidxposed.db.IntentionProvider";

    private static final int GET_TAG = 1 ;
    private static final int INSERT = 2 ;
    private static final int DELETE = 3 ;
    private static final int UPDATE = 4 ;


    static{
        //1.指定一个路径的匹配规则
        uriMatcher.addURI(AUTHORITY,"tags",GET_TAG);
        //4.插入数据,如果路径满足content://com.amos.android_db.provider.PersonProvider/insert,返回值就是(INSERT)=4
        uriMatcher.addURI(AUTHORITY, "insert", INSERT);
        //5.删除数据,如果路径满足content://com.amos.android_db.provider.PersonProvider/delete,返回值就是(DELETE)=5
        uriMatcher.addURI(AUTHORITY, "delete", DELETE);
        //6.更新数据,如果路径满足content://com.amos.android_db.provider.PersonProvider/update,返回值就是(UPDATE)=6
        uriMatcher.addURI(AUTHORITY, "update", UPDATE);
//        //#号为通配符
//        uriMatcher.addURI("com.amos.android_db.provider.PersonProvider","person/#",PERSON);
//        //3.如果路径满足content://com.amos.android_db.provider.PersonProvider/other,返回值就是(OTHER)=3

    }



    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int result =uriMatcher.match(uri);
        switch (result){
            case  GET_TAG:
                return  DbManager.getDbManager(getContext()).getTagCusor();
            default:
                return  null ;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri))
        {
            case GET_TAG:
                return "vnd.android.cursor.dir/tag";
            case INSERT:
                return "vnd.android.cursor.item/tag";
            case DELETE:
                return "vnd.android.cursor.item/tag";
            case UPDATE:
                return "vnd.android.cursor.item/tag";
            default:
                throw new IllegalArgumentException("this is unkown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        int result = uriMatcher.match(uri);
        switch (result) {
            case INSERT:
               DbManager.getDbManager(getContext()).insert(contentValues);
                return uri;
            default:
               return  null ;
        }

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int result = uriMatcher.match(uri);
        switch (result) {
            case DELETE:
                return DbManager.getDbManager(getContext()).delete(selection,selectionArgs);
            default:
               return  0 ;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        int result = uriMatcher.match(uri);
        switch (result) {
            case UPDATE:
                return DbManager.getDbManager(getContext()).update(contentValues,s,strings) ;
            default:
            return  0 ;
        }

    }
}
