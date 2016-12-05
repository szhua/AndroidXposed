package com.szhua.androidxposed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findMethodExactIfExists;

/**
 * Created by szhua on 2016/12/5.
 *
 * 本文从微信的启动页开始在，再启动完成后弹出一个广告：
 *
 */



//First: implements IXposedHookLoadPackage
public class Tutorial implements IXposedHookLoadPackage  {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {


        //Second:find  the package that we want get methods from it ;
        if(!"com.tencent.mm".equals(loadPackageParam.packageName)){
            return;
        }


         findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
             @Override
             protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
               try {
                   XposedBridge.log("start hook!") ;
                   Activity context = (Activity) param.thisObject;
                   Toast.makeText(context,"AndroidXposedTest",Toast.LENGTH_SHORT).show();

               }catch (Exception e){
                   Log.e("leilei","this is why you are erro:"+e.toString()) ;
               }
             }
             @Override
             protected void afterHookedMethod(MethodHookParam param) throws Throwable {
             }
         });
         XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onCreate", Bundle.class , new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.thisObject;
                Intent intent = new Intent();
                ComponentName name = new ComponentName("com.szhua.androidxposed"
                        ,"com.szhua.androidxposed.AdActivity");
                intent.setComponent(name);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                context.startActivity(intent);
            }
        });



    }



}
