package com.example.atlashooktest;

import android.app.Application;
import android.util.Log;

import com.taobao.android.dex.interpret.ARTUtils;
import com.taobao.android.runtime.AndroidRuntime;

/**
 * Created by bshao on 4/12/17.
 */

public class App extends Application {
    public static final String TAG = "AtlasHookTest";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidRuntime.getInstance().init(this);
        Log.w(TAG, "ARTUtils.isDex2oatEnabled(): " + ARTUtils.isDex2oatEnabled());
    }
}
