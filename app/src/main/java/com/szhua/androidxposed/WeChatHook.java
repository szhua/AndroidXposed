package com.szhua.androidxposed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by szhua on 2016/12/7.
 * 执行职责单一原则的类；
 * hook 的具体任务执行类 ；
 */
public class WeChatHook {


    /**
     * 使用内部类实现单例模式；
     */
    private static class SingletonHolder{
        public final   static WeChatHook instance =new WeChatHook();
    }
    public  static  WeChatHook getInstance(){
        return  SingletonHolder.instance;
    }



    private ArrayList<TextView> contactInfoUITextViews =new ArrayList<TextView>() ;

    /**
     * 执行hook操作；
     * @param classLoader
     */
    public  void hook(ClassLoader classLoader) {
        try {
            toastWhenLancherUIonResume(classLoader);
        } catch (Exception e) {
            XposedBridge.log("erro when lancherUI");
        }
        try {
            getInfoFromContactInfoUI(classLoader);
        } catch (Exception e) {
            XposedBridge.log("erro when getInfoFromContactInfoUi");
        }
    }


    /**
     * 在首页进行Toast；（基于这个原理可以加入自己的广告和一些其他的设置）
     * @param classLoader
     */
    private void toastWhenLancherUIonResume(ClassLoader classLoader){
          XposedHelpers.findAndHookMethod(Constant.Class_LauncherUI, classLoader, Constant.Method_onResume, new XC_MethodHook() {
              @Override
              protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                  Context context = (Context) param.thisObject;
                  Toast.makeText(context,"testOriginalMethodWithHook",Toast.LENGTH_SHORT).show();
              }
          })  ;
  }


    /**
     * 从联系人界面获得信息 ；
     * @param classLoader
     */
    private void getInfoFromContactInfoUI(ClassLoader classLoader){
            XposedHelpers.findAndHookMethod(Constant.Class_LauncherUI, classLoader,Constant.Method_OnCreate,Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //获得classLoader实例，以便调用使用dex创建的界面；
                    ClassLoader classLoader =param.thisObject.getClass().getClassLoader();
                    hookDexMethod_getContactInfoUIinfo(classLoader);
                    toastWhenContactInfoUICreated(classLoader);
                }
            });
    }


    /**
     *
     * @param clsLoader
     * @throws XposedHelpers.ClassNotFoundError
     */
    private void hookDexMethod_getContactInfoUIinfo(final ClassLoader clsLoader) throws XposedHelpers.ClassNotFoundError {
            findAndHookMethod(
                    Constant.Class_ConactInfoUI,
                    clsLoader, Constant.Method_onPause , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                            Object currentObject = param.thisObject;

                            //获得ContactInfoUI的父类；并且拿到控件中的数据；
                            Class mmpreference =XposedHelpers.findClass(Constant.Class_MMPreference,clsLoader);
                            for (Field field : mmpreference.getDeclaredFields()) { //遍历类成员
                                field.setAccessible(true);
                                if((field.getName()).equals(Constant.MMFRAME_LISTVIEW))
                                {
                                    ListView listview = (ListView) field.get(currentObject);
                                    if(listview!=null){
                                        XposedBridge.log("we get listview suceess!");
                                        contactInfoUITextViews.clear();
                                        getTextViewFromViewGroup(listview);
                                        for (TextView textView : contactInfoUITextViews) {
                                            XposedBridge.log(textView.getClass().toString());
                                            XposedBridge.log(textView.getText().toString());
                                        }
                                    }
                                }
                            }
                        }
                    });
    }


    /**
     * 进入界面进行展示：
     * @param classLoader
     */
    private void  toastWhenContactInfoUICreated(ClassLoader classLoader){

        findAndHookMethod(
                Constant.Class_ConactInfoUI,
                classLoader, Constant.Method_OnCreate,Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        Activity activity = (Activity) param.thisObject;
                        Toast.makeText(activity,"测试动态加载类的hook",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获得当前微信中的基本信息 ；
     * @param packageName
     * @param loadPackageParam
     */
    public void getBaseInfoFromWx(String packageName ,XC_LoadPackage.LoadPackageParam loadPackageParam){
        Object currentActivityThread  =   XposedHelpers.callStaticMethod(findClass(Constant.Class_ActivityThread,null),Constant.Method_currentActivityThread) ;
        Context context = (Context) callMethod(currentActivityThread,Constant.Method_getSystemContext);
        try {
            String versionName = context.getPackageManager().getPackageInfo(packageName,0).versionName ;
            int uin =loadPackageParam.appInfo.uid;
            log("VersionName:"+versionName);
            log("Uin:"+uin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿到ContactInfoUI中的textview;
     * @param viewGroup
     */
    private void getTextViewFromViewGroup(ViewGroup viewGroup){
        int childCount =viewGroup.getChildCount() ;
        for (int i = 0; i <childCount ; i++) {
            View chilView =viewGroup.getChildAt(i);
            if(chilView instanceof TextView){
                contactInfoUITextViews.add((TextView) chilView);
            }else if(chilView instanceof ViewGroup){
                getTextViewFromViewGroup((ViewGroup) chilView);
            }
        }
    }




}
