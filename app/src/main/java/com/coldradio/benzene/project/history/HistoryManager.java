package com.coldradio.benzene.project.history;

import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Notifier;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private List<History> mHistory = new ArrayList<>();
    private int mCurrentState = -1;

    public void push(History history) {
        // delete after mCurrentState index
        if (mCurrentState < mHistory.size() - 1) {
            mHistory.subList(mCurrentState + 1, mHistory.size()).clear();
        } else if (mHistory.size() >= Configuration.MAX_HISTORY_LIST) {
            mHistory.remove(0);
        }
        mHistory.add(history);
        mCurrentState = mHistory.size() - 1;
        Notifier.instance().notification("History " + mHistory.size());
    }

    public void redo() {
        Notifier.instance().notification("Redo " + (mCurrentState + 1) + "/" + mHistory.size());
        if (mCurrentState < mHistory.size() - 1) {
            ++mCurrentState;
            mHistory.get(mCurrentState).redo();
        }
    }

    public void undo() {
        Notifier.instance().notification("Undo " + (mCurrentState + 1) + "/" + mHistory.size());
        if (mCurrentState >= 0) {
            mHistory.get(mCurrentState).undo();
            --mCurrentState;
        }
    }

    public void reset() {
        mHistory.clear();
        mCurrentState = -1;
    }
}
