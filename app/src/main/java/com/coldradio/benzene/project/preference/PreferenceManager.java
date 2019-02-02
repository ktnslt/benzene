package com.coldradio.benzene.project.preference;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    private List<OnPreferenceEvent> mListenerList = new ArrayList<>();

    public void add(OnPreferenceEvent listener) {
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    public void restore(SharedPreferences sharedPreferences) {
        if (sharedPreferences != null) {
            for (OnPreferenceEvent listener : mListenerList) {
                listener.onRestore(sharedPreferences);
            }
        }
    }

    public void save(SharedPreferences.Editor editor) {
        if (editor != null) {
            for (OnPreferenceEvent listener : mListenerList) {
                listener.onSave(editor);
            }
        }
    }
}
