package com.szhua.androidxposed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * mainActivity :this can cancle if you don't need ;
 */
public class XposedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xposed);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
