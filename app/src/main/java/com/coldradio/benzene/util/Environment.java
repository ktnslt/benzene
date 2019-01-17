package com.coldradio.benzene.util;

import android.content.Context;

import java.io.File;

public class Environment {
    private static Environment msInstance = new Environment();
    private String mProjectFileRootDir;

    public static Environment instance() {
        return msInstance;
    }

    public void setContext(Context context) {
        mProjectFileRootDir = context.getFilesDir().getPath() + File.separator;
    }

    public String projectFilePath() {
        return mProjectFileRootDir;
    }

}
