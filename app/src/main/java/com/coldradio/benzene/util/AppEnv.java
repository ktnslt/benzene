package com.coldradio.benzene.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class AppEnv {
    private static AppEnv msInstance = new AppEnv();
    private String mProjectFileRootDir;
    private String mScreenShotDir;
    private String mTemporaryDir;

    public static AppEnv instance() {
        return msInstance;
    }

    public void setContext(Context context) {
        mProjectFileRootDir = context.getFilesDir().getPath() + File.separator;
        mScreenShotDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/BenzeneScreenshots/";
        mTemporaryDir = mProjectFileRootDir + "temp/";
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
}
