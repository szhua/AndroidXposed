package com.szhua.androidxposed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


/**
 * Created by szhua on 2016/12/7.
 * 执行职责单一原则的类；
 * hook 的具体任务执行类 ；
 */
public class WeChatHook {



    private  String  Method_OnCreate="onCreate" ;
    private  String Method_onResume ="onResume" ;
    private  String Method_onPause ="onPause" ;



    /**
     * default version 6.3.31 !!
     */
    private  String Class_ConactInfoUI  ;
    private  String Class_LauncherUI ;
    private  String Class_MMPreference;
    private  String  MMFRAME_LISTVIEW  ;


    private int ContactUI_ID_UserName ;
    private int ContactUI_ID_WECHAT_NUM ;
    private int ContactUI_ID_NickName ;
    private int ContactUI_ID_Labels ;
    private int ContactUI_ID_Area;
    private int ContactUI_ID_SigNature ;
    private int ContactUI_ID_Distance ;






    /**
     * 使用内部类实现单例模式；
     */
    private static class SingletonHolder{
        public final   static WeChatHook instance(String version){
            return  new WeChatHook(version) ;
        };
    }
    public  static  WeChatHook getInstance(String version){
        return  SingletonHolder.instance(version);
    }



    private ArrayList<TextView> contactInfoUITextViews =new ArrayList<TextView>() ;
    private ArrayList<ImageView> contactsInfoUIIMageViews =new ArrayList<ImageView>() ;

    /**
     * 执行hook操作；
     * @param classLoader
     */
    public  void hook(ClassLoader classLoader) {
        try {
            toastWhenLancherUIonResume(classLoader);
        } catch (Exception e) {
            log("erro when lancherUI");
        }
        try {
            getInfoFromContactInfoUI(classLoader);
        } catch (Exception e) {
          log("erro when getInfoFromContactInfoUi");
        }
    }


    /**
     * 在首页进行Toast；（基于这个原理可以加入自己的广告和一些其他的设置）
     * @param classLoader
     */
    private void toastWhenLancherUIonResume(ClassLoader classLoader){
        findAndHookMethod(Class_LauncherUI, classLoader, Method_onResume, new XC_MethodHook() {
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
           findAndHookMethod(Class_LauncherUI, classLoader,Method_OnCreate,Bundle.class, new XC_MethodHook() {
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
                    Class_ConactInfoUI,
                    clsLoader, Method_onPause , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                            Object currentObject = param.thisObject;

                            //获得ContactInfoUI的父类；并且拿到控件中的数据；
                            Class mmpreference =XposedHelpers.findClass(Class_MMPreference,clsLoader);
                            for (Field field : mmpreference.getDeclaredFields()) { //遍历类成员
                                field.setAccessible(true);
                                if((field.getName()).equals(MMFRAME_LISTVIEW))
                                {
                                    ListView listview = (ListView) field.get(currentObject);
                                    if(listview!=null){
                                        XposedBridge.log("we get listview suceess!");
                                        contactInfoUITextViews.clear();
                                        contactsInfoUIIMageViews.clear();
                                        StringBuffer stringBuffer =new StringBuffer();

                                        getTextViewFromViewGroup(listview);

                                        for (TextView textView : contactInfoUITextViews) {
                                            if(!TextUtils.isEmpty(textView.getText().toString())){
                                                   int textViewId =textView.getId();
                                                   if(textViewId==ContactUI_ID_UserName){
                                                       stringBuffer.append("用户显示名称："+textView.getText().toString()+"\n");
                                                   }else if(textViewId==ContactUI_ID_Area){
                                                       stringBuffer.append("用户的地区："+textView.getText().toString()+"\n");
                                                   }else  if(textViewId==ContactUI_ID_Distance||textViewId==ContactUI_ID_WECHAT_NUM){
                                                       stringBuffer.append("用户的微信号或者距离："+textView.getText().toString()+"\n");
                                                   }else  if (textViewId==ContactUI_ID_Labels){
                                                       stringBuffer.append("标签："+textView.getText().toString()+"\n");
                                                   }else  if (textViewId==ContactUI_ID_NickName){
                                                       stringBuffer.append("微信昵称："+textView.getText().toString()+"\n");
                                                   }else  if (textViewId==ContactUI_ID_SigNature){
                                                       stringBuffer.append("个性签名："+textView.getText().toString()+"\n");
                                                   }

                                                XposedBridge.log(textView.getText().toString());

                                            }
                                        }

                                        try{
                                            //send message to receiver ;
                                            Intent intent =new Intent() ;
                                            intent.putExtra(com.szhua.androidxposed.Constant.IntentExtendedStringName,stringBuffer.toString());
                                            intent.setAction(com.szhua.androidxposed.Constant.Receiver_Action);
                                            ((Context)currentObject).sendBroadcast(intent);
                                        }catch (ClassCastException e){
                                         log("the currentObject can not cast to Context !!");
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
                Class_ConactInfoUI,
                classLoader, Method_OnCreate,Bundle.class, new XC_MethodHook() {
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
            }else if(chilView instanceof  ImageView){
                contactsInfoUIIMageViews.add((ImageView) chilView);
            }
        }
    }












        /**
         * version 6.3.31;todo : setMoreVersions to adapter ;
         */
    /**
     * 微信的版本；
     * @param version
     */
    private WeChatHook(String version){
        switch (version){
            case "6.3.31":
                Class_ConactInfoUI ="com.tencent.mm.plugin.profile.ui.ContactInfoUI" ;
                Class_LauncherUI="com.tencent.mm.ui.LauncherUI" ;
                Class_MMPreference="com.tencent.mm.ui.base.preference.MMPreference";
                MMFRAME_LISTVIEW ="gsM";
                ContactUI_ID_UserName=2131755477 ;
                ContactUI_ID_WECHAT_NUM =2131756475 ;
                ContactUI_ID_NickName =2131756485 ;
                ContactUI_ID_Labels =2131758763;
                ContactUI_ID_Area =16908304 ;
                ContactUI_ID_SigNature =16908304 ;
                ContactUI_ID_Distance =2131756475 ;
                break;
            default:
                Class_ConactInfoUI ="com.tencent.mm.plugin.profile.ui.ContactInfoUI" ;
                Class_LauncherUI="com.tencent.mm.ui.LauncherUI" ;
                Class_MMPreference="com.tencent.mm.ui.base.preference.MMPreference";
                MMFRAME_LISTVIEW ="gsM";
                ContactUI_ID_UserName=2131755477 ;
                ContactUI_ID_WECHAT_NUM =2131756475 ;
                ContactUI_ID_NickName =2131756485 ;
                ContactUI_ID_Labels =2131758763;
                ContactUI_ID_Area =16908304 ;
                ContactUI_ID_SigNature =16908304 ;
                ContactUI_ID_Distance =2131756475 ;
        }
    }



}
