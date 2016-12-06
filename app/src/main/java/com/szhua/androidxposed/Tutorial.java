package com.szhua.androidxposed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findMethodExactIfExists;

/**
 * Created by szhua on 2016/12/5.
 *
 * 本文从微信的启动页开始在，再启动完成后弹出一个广告：
 *
 */



//First: implements IXposedHookLoadPackage
public class Tutorial implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        //Second:find  the package that we want get methods from it ;
     if(!"com.tencent.mm".equals(loadPackageParam.packageName)){
         return;
     }
     try{
       XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
           @Override
           protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
               super.beforeHookedMethod(param);
           }
           @Override
           protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.thisObject;
                 Toast.makeText(context,"testOriginalMethodWithHook",Toast.LENGTH_SHORT).show();
           }
       })  ;

     }catch (Exception e){
         XposedBridge.log("erro find class!!");
     }
      try{
          XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onCreate",Bundle.class, new XC_MethodHook() {
              @Override
              protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                  hookDexMethod(param.thisObject.getClass().getClassLoader());
              }
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              }
          });
      }catch (Exception e){
             XposedBridge.log("erro find class!!");
      }

    }


    /**
     * com.tencent.mm/.plugin.profile.ui.ContactInfoUI
     */
    private void hookDexMethod(ClassLoader clsLoader) throws XposedHelpers.ClassNotFoundError {

        findAndHookMethod(
                "com.tencent.mm.plugin.profile.ui.ContactInfoUI",
                clsLoader, "onCreate",Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                     Context context = (Context) param.thisObject;
                      Toast.makeText(context,"测试动态加载类的hook",Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
