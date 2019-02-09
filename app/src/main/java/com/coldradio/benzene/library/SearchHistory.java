package com.coldradio.benzene.library;

import android.content.SharedPreferences;

import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchHistory {
    private List<String> mSearchRecordList;

    public void add(String keyword) {
        int index = mSearchRecordList.indexOf(keyword);

        if (index >= 0) {
            mSearchRecordList.remove(index);
        }
        mSearchRecordList.add(0, keyword);

        if (mSearchRecordList.size() > Configuration.MAX_SEARCH_HISTORY) {
            mSearchRecordList.remove(mSearchRecordList.size() - 1);
        }
    }

    String[] getSearchHistory() {
        if (mSearchRecordList == null) {
            return null;
        }
        return mSearchRecordList.toArray(new String[mSearchRecordList.size()]);
    }

    void onSave(SharedPreferences.Editor editor) {
        if (mSearchRecordList != null) {
            editor.putString("SearchHistory", TextUtil.toCSV(mSearchRecordList));
        }
    }

    void onRestore(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            mSearchRecordList = new ArrayList<>();
        } else {
            mSearchRecordList = TextUtil.toList(sharedPreferences.getString("SearchHistory", ""), ",");
        }
    }

    String top() {
        if (mSearchRecordList.size() > 0) {
            return mSearchRecordList.get(0);
        }
        return "";
    }
}
