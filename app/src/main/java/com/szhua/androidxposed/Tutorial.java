package com.szhua.androidxposed;


import android.content.Context;

import com.orhanobut.logger.Logger;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by szhua on 2016/12/5.
 *
 */

public class Tutorial implements IXposedHookLoadPackage {
    private  String Class_ActivityThread ="android.app.ActivityThread" ;
    private  String Method_currentActivityThread ="currentActivityThread" ;
    private  String Method_getSystemContext ="getSystemContext" ;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        String packageName = loadPackageParam.packageName;
        if (!(packageName.contains("com.tencen") && packageName.contains("mm")))
            return;
        //获得基本信息；
       String version =getBaseInfoFromWx(packageName,loadPackageParam);
       Logger.d(version);
        //执行hook操作；
       WeChatHook.getInstance(version).hook(loadPackageParam.classLoader);

    }
    /**
     * 获得当前微信中的基本信息 ；
     * @param packageName
     * @param loadPackageParam
     */
    public String  getBaseInfoFromWx(String packageName ,XC_LoadPackage.LoadPackageParam loadPackageParam){
        Object currentActivityThread  =   callStaticMethod(findClass(Class_ActivityThread,null),Method_currentActivityThread) ;
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
