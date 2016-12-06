package com.szhua.androidxposed;

/**
 * Created by szhua on 2016/12/5.
 *
 * Save cancled information !!
 *
 */
public class SavedInformation {

    /**
     *  findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    try {
    XposedBridge.log("start hook!") ;
    Activity context = (Activity) param.thisObject;
    Toast.makeText(context,"AndroidXposedTest",Toast.LENGTH_SHORT).show();

    }catch (Exception e){
    Log.e("leilei","this is why you are erro:"+e.toString()) ;
    }
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    }
    });
     XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onCreate", Bundle.class , new XC_MethodHook() {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    Context context = (Context) param.thisObject;
    Intent intent = new Intent();
    ComponentName name = new ComponentName("com.szhua.androidxposed"
    ,"com.szhua.androidxposed.AdActivity");
    intent.setComponent(name);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
    context.startActivity(intent);
    }
    });
     */

}
