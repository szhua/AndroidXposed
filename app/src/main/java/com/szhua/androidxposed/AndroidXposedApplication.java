package com.szhua.androidxposed;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by szhua on 2016/12/8.
 */
public class AndroidXposedApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //logger初始化
        Logger.init(Constant.TAG).logLevel(LogLevel.FULL);
    }
}
