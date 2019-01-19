package com.coldradio.benzene.util;

import android.content.Context;
import android.widget.Toast;

public class Notifier {
    private Context mContext;
    private static Notifier smInstance = new Notifier();
    Toast mToastMessage;

    public static Notifier instance() {
        return smInstance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void notification(String msg) {
        if (mContext != null) {
            cancel();
            mToastMessage = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            mToastMessage.show();
        }
    }

    public void cancel() {
        if (mToastMessage != null) {
            mToastMessage.cancel();
        }
    }
}