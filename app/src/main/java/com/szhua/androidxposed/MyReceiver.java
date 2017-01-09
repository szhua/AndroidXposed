package com.szhua.androidxposed;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.szhua.androidxposed.db.DbManager;
import com.szhua.androidxposed.db.Tag;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * AndroidXposed
 * Create   2017/1/6 10:34;
 * https://github.com/szhua
 * @author sz.hua
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String log =intent.getStringExtra("app");
        if(!TextUtils.isEmpty(log)) {
            Toast.makeText(context,log,Toast.LENGTH_LONG).show();
            Intent sendBroadCasetIntent =new Intent() ;
            sendBroadCasetIntent.setAction(Constant.TENCENT_JUMP_ACTION);
            if("search".equals(log)){
                sendBroadCasetIntent.putExtra("startActivityName",Constant.search_ui);
            }else if("shake".equals(log)){
                sendBroadCasetIntent.putExtra("startActivityName",Constant.shake_ui);
            }
            context.sendBroadcast(sendBroadCasetIntent);
        }
    }
    private void intsertData(final Context context , final String tagName){
        Observable.just(null)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Object,Object>() {
                    @Override
                    public Object call(Object o) {
                        Logger.d("insert!");
                        List<Tag> tags =  DbManager.getDbManager(context).getDatas();
                        if(tags!=null&&tags.size()>0){
                            Tag tag =tags.get(0);
                            ContentValues contentvalues =new ContentValues() ;
                            contentvalues.put("tag", tagName);
                            contentvalues.put("is_show",1);
                            DbManager.getDbManager(context).update(contentvalues,"id = ?",new String[]{String.valueOf(tag.getId())});
                        }else{
                            ContentValues contenValues =new ContentValues();
                            contenValues.put("tag",tagName);
                            contenValues.put("is_show",1);
                            DbManager.getDbManager(context).insert(contenValues);
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                        /*when call success ==> start activity*/
                        Logger.d("success");
                        Toast.makeText(context.getApplicationContext(),"success",Toast.LENGTH_SHORT).show();

                        Intent startWxIntent = new Intent();
                        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                        startWxIntent.setAction(Intent.ACTION_MAIN);
                        startWxIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startWxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startWxIntent.setComponent(cmp);
                        context.startActivity(startWxIntent);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context.getApplicationContext(),"erro",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(Object o) {
                    }
                });
    }


}

