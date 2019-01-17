package com.coldradio.benzene.util;

import android.content.Context;

public class Environment {
    private static Environment msInstance = new Environment();
    private String mProjectFileRootDir;

    public static Environment instance() {
        return msInstance;
    }

    public void setContext(Context context) {
        mProjectFileRootDir = context.getFilesDir().getPath() + "/";
    }

    public String projectFilePath() {
        return mProjectFileRootDir;
    }

}
