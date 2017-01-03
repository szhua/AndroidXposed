package com.szhua.androidxposed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import de.robv.android.xposed.XC_MethodHook;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


/**
 * AndroidXposed
 * Create   2016/12/30 11:52;
 * https://github.com/szhua
 * @author sz.hua
 * hook 的具体任务执行类 ；
 */
public class WeChatHook {


    private static  final  String  Method_OnCreate="onCreate" ;
    private  static final  String  Method_onResume ="onResume" ;
    private  static final  String  Method_onPause ="onPause" ;

    private String Method_HookIntent_FromContactInfo ;
    private String Method_HookNumber_FromFTSAddFried;

    private  String Class_ConactInfoUI  ;
    private  String Class_LauncherUI ;
    private  String Class_MMPreference;
    private  String Class_FTSAddFriendUI ;

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
        public  static WeChatHook instance(String version){
            return  new WeChatHook(version) ;

        }
    }
    public  static  WeChatHook getInstance(String version){
        return  SingletonHolder.instance(version);
    }

    private ArrayList<TextView> contactInfoUITextViews =new ArrayList<>() ;
  //  private ArrayList<ImageView> contactsInfoUIIMageViews =new ArrayList<>() ;

    /*执行hook操作*/
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


    /*在首页进行Toast；（基于这个原理可以加入自己的广告和一些其他的设置）*/
    private void toastWhenLancherUIonResume(ClassLoader classLoader){
        findAndHookMethod(Class_LauncherUI, classLoader, Method_onResume, new XC_MethodHook() {
              @Override
              protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                  Context context = (Context) param.thisObject;
                  Toast.makeText(context,"testOriginalMethodWithHook",Toast.LENGTH_SHORT).show();
              }
          }) ;
     }



    /*从搜索界面获得手机号，qq号，或者微信号 ==the detail hook method*/
    //a(boolean paramBoolean, String[] paramArrayOfString, long paramLong, int paramInt)
    //public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    private void getInputNumberFromFtsUI(ClassLoader classLoader){

        findAndHookMethod(Class_FTSAddFriendUI, classLoader, "onItemClick", AdapterView.class,View.class,int.class,long.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int paramInt = (int) param.args[2];
                if(paramInt==0){
                     ViewGroup viewGroup = (ViewGroup) param.args[1];
                     TextView phoneNumberView =getPhoneNumberView(viewGroup) ;
                     if(null!=phoneNumberView){
                         String showStr =phoneNumberView.getText().toString();
                         //查找手机/QQ号:552331
                        showStr=showStr.substring("查找手机/QQ号:".length(),showStr.length());
                        log(showStr);
                        Intent broadCastintent = new Intent() ;
                         broadCastintent.putExtra(Constant.IntentExtendedNumName,showStr+",");
                         broadCastintent.putExtra("type",1);
                         broadCastintent.setAction(Constant.Receiver_Action_Num);
                         phoneNumberView.getContext().sendBroadcast(broadCastintent);
                     }
                }
            }
        }) ;
    }
    /* 从联系人界面获得信息*/
    private void getInfoFromContactInfoUI(ClassLoader classLoader){
           findAndHookMethod(Class_LauncherUI, classLoader,Method_OnCreate,Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //获得classLoader实例，以便调用使用dex创建的界面；
                    ClassLoader classLoader =param.thisObject.getClass().getClassLoader();
                  //  hookDexMethod_getContactInfoUIinfo(classLoader);
                    getInfoFromContactInfoUI_by_intent(classLoader);
                    getInputNumberFromFtsUI(classLoader);
                    toastWhenContactInfoUICreated(classLoader);
                }
            });
    }

    /*通过intent从联系人界面拿信息 method is mz ;after this method we can get infos*/
    private void getInfoFromContactInfoUI_by_intent(final ClassLoader classLoader){
        findAndHookMethod(Class_ConactInfoUI, classLoader,Method_HookIntent_FromContactInfo, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try{
                        Activity currentActivity= (Activity) param.thisObject;
                        Intent originaIntent =currentActivity.getIntent() ;
                        String result =  getContactInfoFromIntent(originaIntent);
                        //send message to receiver ;
                        log(result);
                        Intent broadCastintent =new Intent() ;
                        broadCastintent.putExtra(com.szhua.androidxposed.Constant.IntentExtendedStringName,result);
                        broadCastintent.putExtra("type",2);
                        broadCastintent.setAction(Constant.Receiver_Action_Info);
                        currentActivity.sendBroadcast(broadCastintent);
                    }catch (ClassCastException e){
                        Logger.e("the currentObject can not cast to Context !!");
                    }
            }
        });
    }





    /* 进入界面进行展示：toast*/
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



    /**拿到ContactInfoUI中的textview;*/
    /*递归算法从viewGroup中获得textView*/
    private void getTextViewFromViewGroup(ViewGroup viewGroup){
        int childCount =viewGroup.getChildCount() ;
        for (int i = 0; i <childCount ; i++) {
            View chilView =viewGroup.getChildAt(i);
            if(chilView instanceof TextView){
                contactInfoUITextViews.add((TextView) chilView);
            }else if(chilView instanceof ViewGroup){
                getTextViewFromViewGroup((ViewGroup) chilView);
            }
           /* else if(chilView instanceof  ImageView){
                //contactsInfoUIIMageViews.add((ImageView) chilView);
            }*/
        }
    }

    private TextView getPhoneNumberView(ViewGroup viewGroup){
        int childCount =viewGroup.getChildCount() ;
        for (int i = 0; i <childCount ; i++) {
            View childView =viewGroup.getChildAt(i) ;
            if(childView instanceof  TextView){
                return (TextView) childView;
            }else if(childView instanceof ViewGroup){
                return  getPhoneNumberView((ViewGroup) childView);
            }
        }
     return  null ;
    }


    /* @param intent  almost nothing O(∩_∩)O */
    private String  getContactInfoFromIntent(Intent intent){
        String h = intent.getStringExtra("Contact_Alias");
        String j = intent.getStringExtra("Contact_Nick");
        //1:male 2:femaile;
        int k = intent.getIntExtra("Contact_Sex", 0);
        String a2 =intent.getStringExtra("Contact_QuanPin");
        String infos ="" ;
        infos +=(h+",");//Contact_Alias 微信号
        infos +=(j+",");//nickname
        infos+=k+",";//sex
        infos +=(a2+"\r\n");//wechat number lowcase;
        return  infos ;
    }


    /*微信的版本管理*/
    private WeChatHook(String version){
        switch (version){
            case "6.3.31":
                Class_ConactInfoUI ="com.tencent.mm.plugin.profile.ui.ContactInfoUI" ;
                Class_LauncherUI="com.tencent.mm.ui.LauncherUI" ;
                Class_MMPreference="com.tencent.mm.ui.base.preference.MMPreference";
          //    Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSMainUI" ;
            //    Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSBaseUI" ;
                Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.b" ;

                MMFRAME_LISTVIEW ="gsM";

                ContactUI_ID_UserName=2131755477 ;
                ContactUI_ID_WECHAT_NUM =2131756475 ;
                ContactUI_ID_NickName =2131756485 ;
                ContactUI_ID_Labels =2131758763;
                ContactUI_ID_Area =16908304 ;
                ContactUI_ID_SigNature =16908304 ;
                ContactUI_ID_Distance =2131756475;

                Method_HookIntent_FromContactInfo ="MZ";
                Method_HookNumber_FromFTSAddFried ="lX" ;
                break;
            default:

                /*默认的版本是本app支持的最高的微信版本*/
                /*default version is the toppest version this approves*/
                Class_ConactInfoUI ="com.tencent.mm.plugin.profile.ui.ContactInfoUI" ;
                Class_LauncherUI="com.tencent.mm.ui.LauncherUI" ;
                Class_MMPreference="com.tencent.mm.ui.base.preference.MMPreference";
             //   Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSMainUI" ;
              //  Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSBaseUI" ;
                Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.b" ;

                MMFRAME_LISTVIEW ="gsM";

                ContactUI_ID_UserName=2131755477 ;
                ContactUI_ID_WECHAT_NUM =2131756475 ;
                ContactUI_ID_NickName =2131756485 ;
                ContactUI_ID_Labels =2131758763;
                ContactUI_ID_Area =16908304 ;
                ContactUI_ID_SigNature =16908304 ;
                ContactUI_ID_Distance =2131756475 ;

                Method_HookIntent_FromContactInfo ="MZ";
                Method_HookNumber_FromFTSAddFried ="lX" ;
        }
    }



}
