package com.szhua.androidxposed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
    private void hookMethod_getInputNumberFromFtsUI(ClassLoader classLoader){
        findAndHookMethod(Class_FTSAddFriendUI, classLoader, Method_HookNumber_FromFTSAddFried,String.class , new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log("fsdfjkdsjfkdsjfkdsjfkdjsfksdj\n\n\n\n\n\n\n\n\nfdsfds");
                 String params ="" ;
                 if(param.args!=null&&param.args[0]!=null){
                  params =String.valueOf(param.args[0]);
                 }
                 //send to server
                Intent broadCastintent =new Intent() ;
                broadCastintent.putExtra(com.szhua.androidxposed.Constant.IntentExtendedStringName,params);
                broadCastintent.setAction(com.szhua.androidxposed.Constant.Receiver_Action);
                ((Activity)param.thisObject).sendBroadcast(broadCastintent);

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
                    hookDexMethod_getContactInfoUIinfo(classLoader);
                    getInfoFromContactInfoUI_by_intent(classLoader);
                    toastWhenContactInfoUICreated(classLoader);
                    hookMethod_getInputNumberFromFtsUI(classLoader);
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
                        Intent broadCastintent =new Intent() ;
                        broadCastintent.putExtra(com.szhua.androidxposed.Constant.IntentExtendedStringName,result);
                        broadCastintent.setAction(com.szhua.androidxposed.Constant.Receiver_Action);
                        currentActivity.sendBroadcast(broadCastintent);
                    }catch (ClassCastException e){
                        Logger.e("the currentObject can not cast to Context !!");
                    }
            }
        });
    }




    /*从联系人详情页面的控件中找信息 from listview to get infos */
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
                                //setAccessible能够获得number；
                                field.setAccessible(true);
                                if((field.getName()).equals(MMFRAME_LISTVIEW))
                                {
                                    ListView listview = (ListView) field.get(currentObject);
                                    if(listview!=null){
                                        XposedBridge.log("we get listview suceess!");

                                        contactInfoUITextViews.clear();
                                        //contactsInfoUIIMageViews.clear();
                                        /*使用stringBuilder 虽然性能差点，但是线程安全*/
                                        StringBuilder stringBuffer =new StringBuilder();

                                        getTextViewFromViewGroup(listview);

                                        for (TextView textView : contactInfoUITextViews) {
                                            if(!TextUtils.isEmpty(textView.getText().toString())){
                                                   int textViewId =textView.getId();
                                                   if(textViewId==ContactUI_ID_UserName){
                                                       stringBuffer.append("用户显示名称：").append(textView.getText().toString()).append("\n");
                                                   }else if(textViewId==ContactUI_ID_Area){
                                                       stringBuffer.append("用户的地区：").append(textView.getText().toString()).append("\n");
                                                   }else  if(textViewId==ContactUI_ID_Distance||textViewId==ContactUI_ID_WECHAT_NUM){
                                                       stringBuffer.append("用户的微信号或者距离：").append(textView.getText().toString()).append("\n");
                                                   }else  if (textViewId==ContactUI_ID_Labels){
                                                       stringBuffer.append("标签：").append(textView.getText().toString()).append("\n");
                                                   }else  if (textViewId==ContactUI_ID_NickName){
                                                       stringBuffer.append("微信昵称：").append(textView.getText().toString()).append("\n");
                                                   }else  if (textViewId==ContactUI_ID_SigNature){
                                                       stringBuffer.append("个性签名：").append(textView.getText().toString()).append("\n");
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
                                          Logger.e("the currentObject can not cast to Context !!");
                                        }
                                    }
                                }
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

    /* @param intent  almost nothing O(∩_∩)O */
    private String  getContactInfoFromIntent(Intent intent){
        int a = intent.getIntExtra("Contact_Scene", 9);
        String b  = intent.getStringExtra("Verify_ticket");
        boolean c= intent.getBooleanExtra("Chat_Readonly", false);
        boolean d = intent.getBooleanExtra("User_Verify", false);
        String f= intent.getStringExtra("Contact_ChatRoomId");
        String g= intent.getStringExtra("Contact_User");
        String h = intent.getStringExtra("Contact_Alias");
        String i =intent.getStringExtra("Contact_Encryptusername");
        
  
        String j = intent.getStringExtra("Contact_Nick");
        int k = intent.getIntExtra("Contact_Sex", 0);
        String l= intent.getStringExtra("Contact_Province");
        String m = intent.getStringExtra("Contact_City");
        String n = intent.getStringExtra("Contact_Signature");
        int o = intent.getIntExtra("Contact_VUser_Info_Flag", 0);
        String p = intent.getStringExtra("Contact_VUser_Info");
        String q = intent.getStringExtra("Contact_Distance");
        int r = intent.getIntExtra("Contact_KWeibo_flag", 0);
        String s = intent.getStringExtra("Contact_KWeibo");
        String t = intent.getStringExtra("Contact_KWeiboNick");
        String u = intent.getStringExtra("Contact_KFacebookName");
        long v = intent.getLongExtra("Contact_KFacebookId", 0L);
        String  w = intent.getStringExtra("Contact_BrandIconURL");
        String x= intent.getStringExtra("Contact_RegionCode");
        byte[]  y = intent.getByteArrayExtra("Contact_customInfo");
        boolean z = intent.getBooleanExtra("force_get_contact", false);
        String a1 =intent.getStringExtra("Contact_PyInitial");
        String a2 =intent.getStringExtra("Contact_QuanPin");
        String a3 =intent.getStringExtra("Contact_Search_Mobile");
        String a4 =intent.getStringExtra("Contact_Search_Mobile") ;

        String infos ="" ;
        infos +=("a:"+a+"\n");
        infos +=("b:"+b+"\n");
        infos +=("c:"+c+"\n");
        infos +=("d:"+d+"\n");
        infos +=("f:"+f+"\n");
        infos +=("g:"+g+"\n");
        infos +=("h:"+h+"\n");//Contact_Alias 微信号
        infos +=("i:"+i+"\n");
        infos +=("j:"+j+"\n");//nickname
        infos +=("k:"+k+"\n");
        infos +=("l:"+l+"\n");
        infos +=("m:"+m+"\n");
        infos +=("n:"+n+"\n");
        infos +=("o:"+o+"\n");
        infos +=("p:"+p+"\n");
        infos +=("q:"+q+"\n");
        infos +=("r:"+r+"\n");
        infos +=("s:"+s+"\n");
        infos +=("t:"+t+"\n");
        infos +=("u:"+u+"\n");
        infos +=("v:"+v+"\n");
        infos +=("w:"+w+"\n");
        infos +=("x:"+x+"\n"); //provice ;
        try {
            if(y.length>0)
            infos +=("Contact_customInfo:"+new String(y,"UTF-8")+"\n");
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        infos +=("z:"+z+"\n");
        infos +=("a1:"+a1+"\n");//wechat number  upcase ;
        infos +=("a2:"+a2+"\n");//wechat number lowcase;
        infos +=("a3:"+a3+"\n");
        infos +=("number:"+a4+"\n");
        return  infos ;

    }


    /*微信的版本管理*/
    private WeChatHook(String version){
        switch (version){
            case "6.3.31":
                Class_ConactInfoUI ="com.tencent.mm.plugin.profile.ui.ContactInfoUI" ;
                Class_LauncherUI="com.tencent.mm.ui.LauncherUI" ;
                Class_MMPreference="com.tencent.mm.ui.base.preference.MMPreference";
                Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSAddFriendUI" ;

                MMFRAME_LISTVIEW ="gsM";

                ContactUI_ID_UserName=2131755477 ;
                ContactUI_ID_WECHAT_NUM =2131756475 ;
                ContactUI_ID_NickName =2131756485 ;
                ContactUI_ID_Labels =2131758763;
                ContactUI_ID_Area =16908304 ;
                ContactUI_ID_SigNature =16908304 ;
                ContactUI_ID_Distance =2131756475 ;

                Method_HookIntent_FromContactInfo ="MZ";
                Method_HookNumber_FromFTSAddFried ="xN" ;
                break;
            default:

                /*默认的版本是本app支持的最高的微信版本*/
                /*default version is the toppest version this approves*/
                Class_ConactInfoUI ="com.tencent.mm.plugin.profile.ui.ContactInfoUI" ;
                Class_LauncherUI="com.tencent.mm.ui.LauncherUI" ;
                Class_MMPreference="com.tencent.mm.ui.base.preference.MMPreference";
                Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSAddFriendUI" ;

                MMFRAME_LISTVIEW ="gsM";

                ContactUI_ID_UserName=2131755477 ;
                ContactUI_ID_WECHAT_NUM =2131756475 ;
                ContactUI_ID_NickName =2131756485 ;
                ContactUI_ID_Labels =2131758763;
                ContactUI_ID_Area =16908304 ;
                ContactUI_ID_SigNature =16908304 ;
                ContactUI_ID_Distance =2131756475 ;

                Method_HookIntent_FromContactInfo ="MZ";
                Method_HookNumber_FromFTSAddFried ="xN" ;
        }
    }



}
