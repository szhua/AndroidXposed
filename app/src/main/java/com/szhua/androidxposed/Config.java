package com.szhua.androidxposed;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * AndroidXposed
 * Create   2016/12/30 16:56;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public final class Config {
    public static final String PREFERENCE_NAME = "config";
    public static final int DEFAUL_SHAREPRECENCES_MODE =0 ;
    private SharedPreferences sharedPreferences ;
    /*simple mode*/
    private Config (Context context){
        sharedPreferences =context.getSharedPreferences(PREFERENCE_NAME,DEFAUL_SHAREPRECENCES_MODE);
    };
    private static  class  CongfigSingleton{
        private static  Config getConfig(Context context){
            return  new Config(context) ;
        };
    }
    public static Config getConfig(Context context){
        return  CongfigSingleton.getConfig(context);
    }
    public void setNum(String num){
        sharedPreferences.edit().putString("num",num).commit();
    }
    public String getNum(){
        return  sharedPreferences.getString("num","default");
    }
    public void setInfo(String info){
        sharedPreferences.edit().putString("info",info).commit();
    }
    public String getInfo(){
        return  sharedPreferences.getString("info","default");
    }
    public  boolean isNumFinshed(){
        return  sharedPreferences.getBoolean("isNumFinshed",false);
    }
    public boolean isInfoFinshed(){
        return  sharedPreferences.getBoolean("isInfoFinshed",false);
    }
    public void setIsNumFinished(boolean isEnable){
        sharedPreferences.edit().putBoolean("isNumFinshed",isEnable).commit();
    }
    public void setIsInfoFinished(boolean isEnable){
        sharedPreferences.edit().putBoolean("isInfoFinshed",isEnable).commit();
    }

  public boolean isFinished(){
      if(isNumFinshed()&&isInfoFinshed()){
          return  true ;
      }
      return  false ;
  }


}
