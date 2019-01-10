package com.coldradio.benzene.project;

import java.util.ArrayList;
import java.util.List;

public class ProjectHistory {
    private boolean mIsChanged;
    private List<Project> mHistory = new ArrayList<>();

    public boolean isChanged() {
        return mIsChanged;
    }

    public void save(Project project) {
        mIsChanged = true;
    }

    public void redo(Project project) {

    }

    public void undo(Project project) {

    }
}
