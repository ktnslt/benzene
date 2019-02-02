package com.coldradio.benzene.util;

import android.widget.Toast;

public class Notifier {
    private static Notifier smInstance = new Notifier();
    private Toast mToastMessage;

    public static Notifier instance() {
        return smInstance;
    }

    public void notification(String msg) {
        cancel();
        mToastMessage = Toast.makeText(AppEnv.instance().getApplicationContext(), msg, Toast.LENGTH_SHORT);
        mToastMessage.show();
    }

    public void cancel() {
        if (mToastMessage != null) {
            mToastMessage.cancel();
        }
    }
}