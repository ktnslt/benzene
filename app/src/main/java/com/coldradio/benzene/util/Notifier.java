package com.coldradio.benzene.util;

import android.content.Context;
import android.widget.Toast;

public class Notifier {
    private Context mContext;
    private static Notifier smInstance = new Notifier();

    public static Notifier instance() {
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