package com.szhua.androidxposed;

import android.content.ContentValues;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
 * mainActivity :this can cancle if you don't need ;
 */
public class XposedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xposed);
        TextView hello = (TextView) findViewById(R.id.hello);
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intsertData();
            }
        });

        TextView getdata = (TextView) findViewById(R.id.getdata);
        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getDataAndSetState();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getDataAndSetState(){
        Observable
                .just(null)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Object, List<Tag>>() {
                    @Override
                    public List<Tag> call(Object o) {
                        Logger.d("getdata");
                        return DbManager.getDbManager(XposedActivity.this).getDatas();
                    }
                })
                .map(new Func1<List<Tag>,Object>() {
                    @Override
                    public Object call(List<Tag> tags) {
                        if(tags!=null&&tags.size()>0){
                            Logger.d(tags);
                            Tag tag =tags.get(0);
                            ContentValues contentvalues =new ContentValues() ;
                            contentvalues.put("is_show",0);
                            DbManager.getDbManager(XposedActivity.this).update(contentvalues,"id = ?",new String[]{String.valueOf(tag.getId())});
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object object) {

            }
        }) ;
    }


    private void intsertData(){
        Observable.just(null)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Object,Object>() {
                    @Override
                    public Object call(Object o) {
                        Logger.d("insert!");
                        List<Tag> tags =  DbManager.getDbManager(XposedActivity.this).getDatas();
                        if(tags!=null&&tags.size()>0){
                            Tag tag =tags.get(0);
                            ContentValues contentvalues =new ContentValues() ;
                            if("search".equals(tag.getTag())) {
                                contentvalues.put("tag", "shake");
                            }else{
                                contentvalues.put("tag","search");
                            }
                            contentvalues.put("is_show",1);
                            DbManager.getDbManager(XposedActivity.this).update(contentvalues,"id = ?",new String[]{String.valueOf(tag.getId())});
                        }else{
                            ContentValues contenValues =new ContentValues();
                            contenValues.put("tag","search");
                            contenValues.put("is_show",1);
                            DbManager.getDbManager(XposedActivity.this).insert(contenValues);
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("success");
                        Toast.makeText(XposedActivity.this,"success",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(XposedActivity.this,"erro",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

}
