package com.szhua.androidxposed.hook;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.szhua.androidxposed.hook.WeChatHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;


public final class Main implements IXposedHookLoadPackage  {
    private  static final String Class_ActivityThread ="android.app.ActivityThread" ;
    private  static final String Method_currentActivityThread ="currentActivityThread" ;
    private  static final  String Method_getSystemContext ="getSystemContext";


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        String packageName = loadPackageParam.packageName;

        if (packageName.contains("com.tencent.mm")||packageName.contains("com.szhua.android.xposed")) {
            //获得基本信息；
            String version = getBaseInfoFromWx(packageName, loadPackageParam);
            //执行hook操作；
            WeChatHook.getInstance(version).hook(loadPackageParam.classLoader);
        }
    }

    /*获得当前微信中的基本信息 ；*/
    public String  getBaseInfoFromWx(String packageName ,XC_LoadPackage.LoadPackageParam loadPackageParam){
        Object currentActivityThread  = callStaticMethod(findClass(Class_ActivityThread,null),Method_currentActivityThread) ;
        Context context = (Context) callMethod(currentActivityThread,Method_getSystemContext);
        try {
            String versionName = context.getPackageManager().getPackageInfo(packageName,0).versionName ;
            int uin =loadPackageParam.appInfo.uid;
            Logger.d("VersionName:"+versionName);
            Logger.d("Uin:"+uin);
            return  versionName ;
        } catch (Exception e) {
            e.printStackTrace();
            return  "6.3.31" ;
        }
    }


}
