package com.coldradio.benzene.project.preference;

import android.content.SharedPreferences;

public interface OnPreferenceEvent {
    void onRestore(SharedPreferences sharedPreferences);
    void onSave(SharedPreferences.Editor editor);
}
