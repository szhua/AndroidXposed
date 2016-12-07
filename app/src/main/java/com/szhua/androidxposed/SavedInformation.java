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

    // TODO: 2016/12/7
    /**
     *   try{
     Class storageM =  XposedHelpers.findClass("com.tencent.mm.storage.m",clsLoader);
     XposedHelpers.findAndHookConstructor(storageM, String.class, new XC_MethodHook() {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    XposedBridge.log("M constructure:"+param.args[0].toString());
    }
    }) ;

     }catch (Exception e){
     XposedBridge.log("M constructure erro!!!");
     }



     findAndHookMethod(Constant.Class_ConactInfoUI, clsLoader, "wv",String.class, new XC_MethodHook() {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    String params = (String) param.args[0];
    XposedBridge.log("wv:"+params);
    }
    }) ;



     //public final void a(int paramInt, com.tencent.mm.sdk.h.j paramj, Object paramObject)


     findAndHookMethod(Constant.Class_ConactInfoUI, clsLoader, "a",int.class,"com.tencent.mm.sdk.h.j",Object.class, new XC_MethodHook() {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    if(param.args[2] instanceof String) {
    String params = (String) param.args[2];
    XposedBridge.log("a(int,j,Obkject):" + params);
    }else{
    XposedBridge.log("a(int,j,Obkject):unkhnowed method!");
    }
    }
    }) ;

     //public final void a(final String paramString, com.tencent.mm.sdk.h.i parami)
     findAndHookMethod(Constant.Class_ConactInfoUI, clsLoader, "a",String.class,"com.tencent.mm.sdk.h.i", new XC_MethodHook() {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    if(param.args[0] instanceof String) {
    String params = (String) param.args[0];
    XposedBridge.log("a(String,i):" + params);
    }else{
    XposedBridge.log("a(String,i):unkhnowed method!");
    }
    }
    }) ;

     //protected final String atw()

     findAndHookMethod(Constant.Class_ConactInfoUI, clsLoader, "atw", new XC_MethodHook() {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    String result = (String) param.getResult();
    XposedBridge.log("atw():"+result);
    }
    }) ;
     */


    // TODO: 2016/12/7
    /**
     * //                        Object currentObject = param.thisObject;
     //                        for (Method field : currentObject.getClass().getDeclaredMethods()) { //遍历类成员
     //                             field.setAccessible(true);
     //                             XposedBridge.log(field.getName()+"paramType:"+field.getParameterTypes()+"returnType:"+field.getReturnType());
     //                        }
     */

}
