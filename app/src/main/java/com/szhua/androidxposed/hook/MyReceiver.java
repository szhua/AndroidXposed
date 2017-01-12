package com.szhua.androidxposed.hook;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;
import com.orhanobut.logger.Logger;
import com.szhua.androidxposed.Constant;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * AndroidXposed
 * Create   2017/1/6 10:34;
 * https://github.com/szhua
 * @author sz.hua
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

       String action =intent.getAction() ;

        switch (action){
            case "test_test":
                final String extraInfo =intent.getStringExtra("app");
                if(!TextUtils.isEmpty(extraInfo)) {

                    try {
                        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> runningTasks = manager .getRunningTasks(1);
                        ActivityManager.RunningTaskInfo cinfo = runningTasks.get(0);
                        ComponentName component = cinfo.topActivity;
                        if(component!=null){
                            Logger.d("current activity is "+component.getClassName());
                            if(!component.getClassName().contains("com.tencent.mm")){
                                startActivityInWechat(context);
                                sendBroadCastToTencent(context,extraInfo);
                            }else{
                                sendBroadCastToTencent(context,extraInfo);
                            }
                        }else{
                            Logger.d("class is null!!!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case  "test_share":
             String filePath =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/a.png";
             File file =new File(filePath);
             shareToFriend(file,context);
                break;
        }




    }

   /*给hook中注册的receiver发送广播打开相应的界面*/
    private void sendBroadCastToTencent(final Context context , final String extraInfo){

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                 Toast.makeText(context,extraInfo,Toast.LENGTH_LONG).show();
                Intent sendBroadCasetIntent =new Intent() ;
                sendBroadCasetIntent.setAction(Constant.TENCENT_JUMP_ACTION);
                if("search".equals(extraInfo)){
                    sendBroadCasetIntent.putExtra("startActivityName",Constant.search_ui);
                }else if("shake".equals(extraInfo)){
                    sendBroadCasetIntent.putExtra("startActivityName",Constant.shake_ui);
                }else if ("share".equals(extraInfo)){
                    String filePath =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/share.jpg";
                    Logger.d(filePath);
                    File file =new File(filePath);
                    Uri uri =Uri.fromFile(file);
                    sendBroadCasetIntent.putExtra("uri",uri);
                    sendBroadCasetIntent.putExtra("startActivityName","share");
                }
                context.sendBroadcast(sendBroadCasetIntent);
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(Long aLong) {

            }
        }) ;
    }

    /*打开微信的最上层activity*/
    /*start tencent-WeChat's toppest activity*/
    private void startActivityInWechat(Context context){
                        Intent startWxIntent = new Intent();
                        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                        startWxIntent.setAction(Intent.ACTION_MAIN);
                        startWxIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startWxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startWxIntent.setComponent(cmp);
                        context.startActivity(startWxIntent);
    }

    private void shareToFriend(@NonNull File file , Context context) {

        Intent intent = new Intent();
        Uri uri =Uri.fromFile(file);
        intent.putExtra("uri",uri);
        intent.putExtra("startActivityName","share");
        intent.setAction(Constant.TENCENT_JUMP_ACTION);
        context.sendBroadcast(intent);

    }


}

