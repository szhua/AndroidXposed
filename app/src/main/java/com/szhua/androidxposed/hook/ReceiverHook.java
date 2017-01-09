package com.szhua.androidxposed.hook;

import android.content.Context;
import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;

/**
 * AndroidXposed
 * Create   2017/1/6 15:31;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public class ReceiverHook {

    private ReceiverHook(){};
    /**
     * 使用内部类实现单例模式；
     */
    private static class SingletonHolder{
        public  static ReceiverHook instance =new ReceiverHook();
    }
    public  static  ReceiverHook getInstance(){
        return  SingletonHolder.instance;
    }
    /*执行hook操作*/
    public  void hook(ClassLoader classLoader) {
      getReceiverInfos(classLoader);
     }
    private void getReceiverInfos(ClassLoader classLoader){

        XposedHelpers.findAndHookMethod("com.szhua.androidxposed.MyReceiver", classLoader, "onReceive", Context.class, Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Intent intent = (Intent) param.args[1];
                String logInfo =intent.getStringExtra("app");
                XposedBridge.log(logInfo);



            }
        }) ;

    }







}
