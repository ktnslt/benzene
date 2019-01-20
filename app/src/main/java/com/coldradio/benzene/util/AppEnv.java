package com.coldradio.benzene.util;

import android.content.Context;
import android.os.Environment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.coldradio.benzene.library.local.LocalCompounds;
import com.google.gson.Gson;

import java.io.File;

public class AppEnv {
    private static AppEnv msInstance = new AppEnv();
    private String mProjectFileRootDir;
    private String mScreenShotDir;
    private String mTemporaryDir;
    private RequestQueue mRequestQueue;
    private Context mApplicationContext;
    private Gson mGson = new Gson();

    public static AppEnv instance() {
        return msInstance;
    }

    public void setContext(Context appContext) {
        // shall called with Application Context, not with the Activity context
        if (mProjectFileRootDir == null) {
            mProjectFileRootDir = appContext.getFilesDir().getPath() + File.separator;
            mScreenShotDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/BenzeneScreenshots/";
            mTemporaryDir = mProjectFileRootDir + "temp/";
            mRequestQueue = Volley.newRequestQueue(appContext);
            mApplicationContext = appContext;
            LocalCompounds.instance().parseLibrary(appContext.getResources());
        }
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public Gson gson() {
        return mGson;
    }

    public String projectFileDir() {
        return mProjectFileRootDir;
    }

    public String screenShotDir() {
        return mScreenShotDir;
    }

    public String temporaryDir() {
        return mTemporaryDir;
    }

    public void addToNetworkQueue(Request request) {
        mRequestQueue.add(request);
    }

    public void cancelAllNetworkRequest() {
        mRequestQueue.cancelAll(mApplicationContext);
    }
}
