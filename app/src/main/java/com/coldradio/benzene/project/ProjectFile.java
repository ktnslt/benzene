package com.coldradio.benzene.project;

public class ProjectFile {
    private String mName;
    private boolean mHasSavedFile = true;

    public ProjectFile(String fileName) {
        mName = fileName;
    }

    public ProjectFile(String fileName, boolean createdNew) {
        mName = fileName;

        if (createdNew) {
            mHasSavedFile = false;
        }
    }

    public String getName() {
        return mName;
    }

    public boolean hasSavedFile() {
        return mHasSavedFile;
    }

    public void saved() {
        mHasSavedFile = true;
    }
}