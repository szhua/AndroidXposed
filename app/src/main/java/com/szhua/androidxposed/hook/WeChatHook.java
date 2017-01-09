package com.szhua.androidxposed.hook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.orhanobut.logger.Logger;
import com.szhua.androidxposed.Constant;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findClassIfExists;


/**
 * AndroidXposed
 * Create   2016/12/30 11:52;
 * https://github.com/szhua
 * @author sz.hua
 * hook 的具体任务执行类 ；
 */
public class WeChatHook {

    private static  Context wxContext;

    private  static  final  String  Method_OnCreate="onCreate" ;
    private  static final  String  Method_onResume ="onResume" ;
    private  static final  String  Method_onPause ="onPause" ;

    private String Method_HookIntent_FromContactInfo ;
    private String Method_HookNumber_FromFTSAddFried;

    private  String Class_ConactInfoUI  ;
    private  String Class_LauncherUI ;
    private  String Class_MMPreference;
    private  String Class_FTSAddFriendUI ;
    private String Class_MobileFriendUI_Adapter;
    private String Class_SayHiWithSnsPermissionUI  ;

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
//        try{
//            getReiceiveiInfo(classLoader);
//        }catch (Exception e){
//             log("erro when getReceiInfo!!");
//        }
    }

    /* <receiver android:exported="false" android:name="com.tencent.mm.booter.ClickFlowReceiver">
            <intent-filter>
                <action android:name="com.tencent.mm.Intent.ACTION_CLICK_FLOW_REPORT"/>
            </intent-filter>
        </receiver>*/

    /*com.tencent.mm.plugin.base.stub.WXEntryActivity$EntryReceiver*/
    private void getReiceiveiInfo(final ClassLoader classLoader){
        findAndHookMethod("com.tencent.mm.plugin.base.stub.WXEntryActivity.EntryReceiver", classLoader, "onReceive",Context.class,Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                Toast.makeText(context,"ONreceive",Toast.LENGTH_SHORT).show();
            //    WeChatHook.this.wxContext = (Context) param.thisObject;
//                log("receive!!!!");
//                Intent intent = (Intent) param.args[1];
//
//                String action =intent.getAction() ;
//                if("com.tencent.mm.Intent.ACTION_CLICK_FLOW_REPORT".equals(action)){
//
//                    String tag  =intent.getStringExtra("tag") ;
//                    if(TextUtils.isEmpty(tag)) {
//                        log("tag::"+tag);
//                    }
//                }


//                Activity activity = (Activity) param.thisObject;
//
//                ContentResolver contentResolver = activity.getContentResolver();
//                Uri uri = Uri.parse("content://com.szhua.androidxposed.db.IntentionProvider/tags");
//                Cursor cursor =contentResolver.query(uri,null,null,null,null);
//                Tag tag =null ;
//                while (cursor.moveToNext()){
//                    String tagName =cursor.getString(cursor.getColumnIndex("tag"));
//                    int is_show = cursor.getInt(cursor.getColumnIndex("is_show"));
//                    int id =cursor.getInt(cursor.getColumnIndex("id"));
//                    if(!TextUtils.isEmpty(tagName)) {
//                        tag = new Tag(id, tagName, is_show);
//                        break;
//                    }
//                }
//                cursor.close();
//                if(tag!=null&&tag.getIs_show()==1){
//                    String info =tag.getTag() ;
//                    log(info);
//                    //update state!~
//                    uri =Uri.parse("content://com.szhua.androidxposed.db.IntentionProvider/update");
//                    ContentValues contentValues =new ContentValues() ;
//                    contentValues.put("is_show",0);
//                    contentResolver.update(uri,contentValues,"id = ?",new String[]{String.valueOf(tag.getId())});
//
//                    if("shake".equals(info)){
//                        Class<?>  a = findClass("com.tencent.mm.plugin.shake.ui.ShakeReportUI", param.thisObject.getClass().getClassLoader());
//                        Intent startIntent =new Intent(activity,a);
//                        activity.startActivity(startIntent);
//                    }else if("search".equals(info)){
//                        Class<?> b =findClass("com.tencent.mm.plugin.search.ui.FTSMainUI",param.thisObject.getClass().getClassLoader());
//                        Intent startIntent =new Intent(activity,b);
//                        activity.startActivity(startIntent);
//                    }
//                }

            }
        });
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


    private void getListNumInfoFromMobileFriendUI(ClassLoader classLoader){
        XposedHelpers.findAndHookMethod(Class_MobileFriendUI_Adapter, classLoader, "MC", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
               log((String) param.args[0]);
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
    private void getInfoFromContactInfoUI( ClassLoader classLoader){
           findAndHookMethod(Class_LauncherUI, classLoader,Method_OnCreate,Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //获得classLoader实例，以便调用使用dex创建的界面；

                    final ClassLoader classLoader =param.thisObject.getClass().getClassLoader();
                   //  hookDexMethod_getContactInfoUIinfo(classLoader);
                    final Activity activity = (Activity) param.thisObject;
                    BroadcastReceiver myReceiver =new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            log("Onreive");
                            String startActivityName = intent.getStringExtra("startActivityName");
                            try {
                                Activity currentActivity = getGlobleActivity();
                                if (!TextUtils.isEmpty(startActivityName)&&currentActivity!=null&&!currentActivity.getClass().getName().toString().contains(startActivityName)) {
                                            try {
                                                Toast.makeText(context, startActivityName, Toast.LENGTH_SHORT).show();
                                                Class<?> a = null;
                                                if (Constant.shake_ui.equals(startActivityName)) {
                                                    a = XposedHelpers.findClass(Constant.shake_ui, classLoader);
                                                } else if (Constant.search_ui.equals(startActivityName)) {
                                                    a = XposedHelpers.findClass(Constant.search_ui, classLoader);
                                                }
                                                if (a != null) {
                                                    Intent startIntent = new Intent(context, a);
                                                    context.startActivity(startIntent);
                                                }
                                            } catch (Exception e) {
                                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                }

                            } catch (Exception e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                            }

                    }
                    };
                    IntentFilter intentFilter =new IntentFilter();
                    intentFilter.addAction(Constant.TENCENT_JUMP_ACTION);
                    activity.registerReceiver(myReceiver,intentFilter);



                    getInfoFromContactInfoUI_by_intent(classLoader);
                    getInputNumberFromFtsUI(classLoader);
                    toastWhenContactInfoUICreated(classLoader);
                    getListNumInfoFromMobileFriendUI(classLoader);
                    getInfoFromSayHiWithSnsPermissionUIByIntent(classLoader);
                //    getReiceiveiInfo(classLoader);
                }
            });
    }

    private void getInfoFromSayHiWithSnsPermissionUIByIntent(ClassLoader classLoader){
        findAndHookMethod(Class_SayHiWithSnsPermissionUI, classLoader, "MZ", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
               Activity sayHiActivity = (Activity) param.thisObject;
               Intent intent =sayHiActivity.getIntent() ;
               String Contact_User =intent.getStringExtra("Contact_User");
               String source_from_user_name =intent.getStringExtra("source_from_user_name");
               String source_from_nick_name =intent.getStringExtra("source_from_nick_name");
               log(Contact_User+"\n"+source_from_user_name+"\n"+source_from_nick_name+"\n");
            }
        }) ;
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

        int a = intent.getIntExtra("Contact_Scene", 9);
        String b  = intent.getStringExtra("Verify_ticket");
        boolean c= intent.getBooleanExtra("Chat_Readonly", false);
        boolean d = intent.getBooleanExtra("User_Verify", false);
        String f= intent.getStringExtra("Contact_ChatRoomId");
        String g= intent.getStringExtra("Contact_User");
       String i =intent.getStringExtra("Contact_Encryptusername");
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

            String a3 =intent.getStringExtra("Contact_Search_Mobile");
        String a4 =intent.getStringExtra("Contact_Search_Mobile") ;

        log(a+"\n"+
                b+"\n"
                +c+"\n"
                +d+"\n"
                +g+"\n"
                +f+"\n"
                +h+"\n"
                +i+"\n"
                +g+"\n"
                +k+"\n"
                +l+"\n"
                +m+"\n"
                +n+"\n"
                +o+"\n"
                +p+"\n"
                +q+"\n"
                +r+"\n"
                +s+"\n"
                +t+"\n"
                +u+"\n"
                +v+"\n"
                +w+"\n"
                +x+"\n"
                +y+"\n"
                +z+"\n"
                +a1+"\n"
                +a4+"\n"
                +a3+"\n"
        );

        String infos ="" ;
        infos +=(h+",");//Contact_Alias 微信号
        infos +=(g+",");//Contact_Alias 微信号
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
              // Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSMainUI" ;
              // Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.FTSBaseUI" ;
                Class_FTSAddFriendUI="com.tencent.mm.plugin.search.ui.b" ;
                Class_MobileFriendUI_Adapter ="com.tencent.mm.ui.bindmobile.a";
                Class_SayHiWithSnsPermissionUI ="com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";

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
                Class_MobileFriendUI_Adapter ="com.tencent.mm.ui.bindmobile.a";

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
        }
    }


    private  Activity getGlobleActivity() throws Exception{
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        for(Object activityRecord:activities.values()){
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if(!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
    }


}
