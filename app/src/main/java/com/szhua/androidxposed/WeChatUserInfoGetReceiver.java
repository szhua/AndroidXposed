package com.szhua.androidxposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;


public class WeChatUserInfoGetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Constant.Receiver_Action_Num.equals(intent.getAction())){

                    String num   = intent.getStringExtra(Constant.IntentExtendedNumName) ;

                    Logger.d(num);


                    if(!TextUtils.isEmpty(num)){
                        Config.getConfig(context).setNum(num);
                        Config.getConfig(context).setIsNumFinished(true);
                    }
            }else  if(Constant.Receiver_Action_Info.equals(intent.getAction())){


                    String info   = intent.getStringExtra(Constant.IntentExtendedStringName) ;
                    Logger.d(info);


                    if(!TextUtils.isEmpty(info)){
                        Config.getConfig(context).setInfo(info);
                        Config.getConfig(context).setIsInfoFinished(true);

                        if(Config.getConfig(context).isFinished()){
                            SLog.getInstance().setmEnableSaveToFile(true);
                            SLog.getInstance().writIntoFile(Config.getConfig(context).getNum()+Config.getConfig(context).getInfo());
                            Config.getConfig(context).setIsInfoFinished(false);
                            Config.getConfig(context).setIsNumFinished(false);
                        }
                    }

        }
        }

}
