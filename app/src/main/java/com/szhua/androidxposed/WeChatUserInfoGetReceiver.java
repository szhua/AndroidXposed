package com.szhua.androidxposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

/**
 * Created by szhua on 2016/12/8.
 */
public class WeChatUserInfoGetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       if(Constant.Receiver_Action.equals(intent.getAction())){
           String userInfo =intent.getStringExtra(Constant.IntentExtendedStringName);
           Logger.d(userInfo);
       }
    }
}
