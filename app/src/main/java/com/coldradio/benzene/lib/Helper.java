package com.coldradio.benzene.lib;

import android.content.Context;
import android.widget.Toast;

public class Helper {
    private Context mContext;
    private static Helper smInstance = new Helper();

    public static Helper instance() {
        return smInstance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void notification(String msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
}