package com.example.atlashooktest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dalvik.system.DexClassLoader;

import com.taobao.android.dex.interpret.ARTUtils;
import com.taobao.android.runtime.AndroidRuntime;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private String mApkPathEnableDexOpt;
    private String mOdexPathEnableDexOpt;

    private String mApkPathDisableDexOpt;
    private String mOdexPathDisableDexOpt;

    private TextView mEnableDexOptResult;
    private TextView mDisableDexOptResult;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };
    private Button mBtnLoadDexEnableDexOpt;
    private Button mBtnLoadDexDisableDexOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mEnableDexOptResult = (TextView) findViewById(R.id.load_dex_enable_dexopt_cost);
        mDisableDexOptResult = (TextView) findViewById(R.id.load_dex_disable_dexopt_cost);

        final File cacheDir = getCacheDir();
        mApkPathEnableDexOpt = new File(cacheDir, "base-1.apk").getAbsolutePath();
        mOdexPathEnableDexOpt = new File(cacheDir, "base-1.dex").getAbsolutePath();

        mApkPathDisableDexOpt = new File(cacheDir, "base-2.apk").getAbsolutePath();
        mOdexPathDisableDexOpt = new File(cacheDir, "base-2.dex").getAbsolutePath();

        mBtnLoadDexEnableDexOpt = (Button) findViewById(R.id.btn_load_dex_enable_dexopt);
        mBtnLoadDexEnableDexOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDexEnableDexOptAsync();
            }
        });

        mBtnLoadDexDisableDexOpt = (Button) findViewById(R.id.btn_load_dex_disable_dexopt);
        mBtnLoadDexDisableDexOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDexDisableDexOptAsync();
            }
        });

        setButtonsEnabled(false);
        copyAssetsAsync();
    }

    private void setButtonsEnabled(boolean enabled) {
        mBtnLoadDexEnableDexOpt.setEnabled(enabled);
        mBtnLoadDexDisableDexOpt.setEnabled(enabled);
    }

    private void loadDexEnableDexOptAsync() {
        new AsyncTask<Void, Void, Long>() {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = ProgressDialog.show(MainActivity.this, null,
                        "loadDexEnableDexOptAsync...");
                mBtnLoadDexEnableDexOpt.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Long timeCost) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                mEnableDexOptResult.setText("loadDexDisableDexOpt cost ms: " + timeCost);
            }


            @Override
            protected Long doInBackground(Void... params) {
                deleteOdex();

                long start = SystemClock.elapsedRealtime();
                try {
                    final AndroidRuntime instance = AndroidRuntime.getInstance();
                    instance.setEnabled(false);

                    instance.loadDex(mApkPathEnableDexOpt, mOdexPathEnableDexOpt, 0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                final long timeCost = SystemClock.elapsedRealtime() - start;
                Log.d(App.TAG, "LoadDexEnableDexOpt cost ms: " + timeCost);
                return timeCost;
            }
        }.execute();
    }

    private void loadDexDisableDexOptAsync() {
        new AsyncTask<Void, Void, Long>() {

            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = ProgressDialog.show(MainActivity.this, null,
                        "loadDexDisableDexOptAsync...");
                mBtnLoadDexDisableDexOpt.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Long timeCost) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                mDisableDexOptResult.setText("loadDexDisableDexOpt cost ms: " + timeCost);
            }

            @Override
            protected Long doInBackground(Void... params) {
                deleteOdex();

                final long start = SystemClock.elapsedRealtime();
//                    final AndroidRuntime instance = AndroidRuntime.getInstance();
//                    instance.setEnabled(true);
//                    instance.loadDex(mApkPathDisableDexOpt, mOdexPathDisableDexOpt, 0);

                ARTUtils.setIsDex2oatEnabled(false);

                DexClassLoader classLoader = new DexClassLoader(mApkPathDisableDexOpt,
                        new File(mOdexPathDisableDexOpt).getParent(),
                        new File(mOdexPathDisableDexOpt).getParent(),
                        MainActivity.class.getClassLoader());

                ARTUtils.setIsDex2oatEnabled(true);

                final long timeCost = SystemClock.elapsedRealtime() - start;
                Log.d(App.TAG, "loadDexDisableDexOpt cost ms: " + timeCost);
                return timeCost;
            }
        }.execute();
    }

    private void deleteOdex() {
        File file = new File(mOdexPathEnableDexOpt);
        if (file.isFile()) {
            file.delete();
        }

        file = new File(mOdexPathDisableDexOpt);
        if (file.isFile()) {
            file.delete();
        }
    }

    private void copyAssetsAsync() {
        new AsyncTask<Void, Void, Long>() {

            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = ProgressDialog.show(MainActivity.this, null,
                        "Copying plugin apk to internal storage...");
            }

            @Override
            protected void onPostExecute(Long timeCost) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                final String message = "Copying plugin apk cost ms: " + timeCost;
                Log.d(App.TAG, message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                setButtonsEnabled(true);
            }

            @Override
            protected Long doInBackground(Void... params) {
                final long start = SystemClock.elapsedRealtime();
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = getAssets().open("com.dijkstra.notely.apk");
                    outputStream = new FileOutputStream(mApkPathEnableDexOpt);
                    IOUtils.copy(inputStream, outputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                }

                try {
                    inputStream = getAssets().open("com.dijkstra.notely.apk");
                    outputStream = new FileOutputStream(mApkPathDisableDexOpt);
                    IOUtils.copy(inputStream, outputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                }

                return (SystemClock.elapsedRealtime() - start);
            }
        }.execute();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
