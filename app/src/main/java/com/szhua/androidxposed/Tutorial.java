package com.szhua.androidxposed;


import android.widget.TextView;
import java.util.ArrayList;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
/**
 * Created by szhua on 2016/12/5.
 *
 */

public class Tutorial implements IXposedHookLoadPackage {
    ArrayList<TextView> textViews = new ArrayList<>() ;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        String packageName = loadPackageParam.packageName;
        if (!(packageName.contains("com.tencen") && packageName.contains("mm")))
            return;
        //获得基本信息；
         WeChatHook.getInstance().getBaseInfoFromWx(packageName,loadPackageParam);
        //执行hook操作；
        WeChatHook.getInstance().hook(loadPackageParam.classLoader);

    }











}
