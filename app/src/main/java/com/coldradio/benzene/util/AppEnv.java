package com.coldradio.benzene.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.coldradio.benzene.project.preference.OnPreferenceEvent;
import com.coldradio.benzene.project.preference.PreferenceManager;
import com.coldradio.benzene.view.CanvasView;
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
    private View mCanvasView;
    private Activity mCurrentActivity;
    private PreferenceManager mPreferenceManager = new PreferenceManager();

    public static AppEnv instance() {
        return msInstance;
    }

    public void initialize(Context appContext) {
        // shall called with Application Context, not with the Activity context
        if (mProjectFileRootDir == null) {
            mProjectFileRootDir = appContext.getFilesDir().getPath() + File.separator;
            mScreenShotDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/BenzeneScreenshots/";
            mTemporaryDir = mProjectFileRootDir + "temp/";
            mRequestQueue = Volley.newRequestQueue(appContext);
            mApplicationContext = appContext;
        }
    }

    public void terminate() {
        SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences("BenzenePreference", Context.MODE_PRIVATE).edit();

        mPreferenceManager.save(editor);
        editor.apply();
    }

    public void setCanvasView(View canvasView) {
        mCanvasView = canvasView;
    }

    public void invalidateCanvasView() {
        if (mCanvasView != null) {
            mCanvasView.invalidate();
        }
    }

    public void updateContextMenu() {
        CanvasView canvasView = (CanvasView) mCanvasView;

        if (canvasView != null) {
            canvasView.updateContextMenu();
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

    public void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void addPreferenceListener(OnPreferenceEvent listener) {
        // initialize() might be earlier than addPreferenceListener(). Hence, restore at the time of registration.
        if (listener != null) {
            mPreferenceManager.add(listener);
            listener.onRestore(mApplicationContext.getSharedPreferences("BenzenePreference", Context.MODE_PRIVATE));
        }
    }
}
