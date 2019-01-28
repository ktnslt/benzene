package com.coldradio.benzene.project;

import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.FileUtil;

import java.util.Comparator;

public class ProjectFile {
    public static class NameComparator implements Comparator<ProjectFile> {
        private boolean mReversed = false;

        NameComparator() {
        }

        NameComparator(boolean reversed) {
            mReversed = reversed;
        }

        @Override
        public int compare(ProjectFile o1, ProjectFile o2) {
            return mReversed ? o2.mName.compareToIgnoreCase(o1.mName) : o1.mName.compareToIgnoreCase(o2.mName);
        }
    }
    public static class LastModifiedTimeComparator implements Comparator<ProjectFile> {
        private boolean mReversed = false;

        LastModifiedTimeComparator() {
        }

        LastModifiedTimeComparator(boolean reversed) {
            mReversed = reversed;
        }

        @Override
        public int compare(ProjectFile o1, ProjectFile o2) {
            if (mReversed) {
                return Long.valueOf(o2.mLastModifiedTime).compareTo(o1.mLastModifiedTime);
            } else {
                return Long.valueOf(o1.mLastModifiedTime).compareTo(o2.mLastModifiedTime);
            }
        }
    }
    private String mName;
    private boolean mHasSavedFile = true;
    private long mLastModifiedTime;

    ProjectFile(String fileName) {
        mName = fileName;
        mLastModifiedTime = FileUtil.lastModifiedTime(AppEnv.instance().projectFileDir() + mName + Configuration.PROJECT_FILE_EXT);
    }

    ProjectFile(String fileName, boolean createdNew) {
        mName = fileName;
        mLastModifiedTime = FileUtil.lastModifiedTime(AppEnv.instance().projectFileDir() + mName + Configuration.PROJECT_FILE_EXT);

        if (createdNew) {
            mHasSavedFile = false;
        }
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    public long lastModified() {
        return mLastModifiedTime;
    }

    public boolean hasSavedFile() {
        return mHasSavedFile;
    }

    public void saved() {
        mHasSavedFile = true;
        mLastModifiedTime = System.currentTimeMillis();
    }

    public boolean rename(String newName) {
        if (!FileUtil.validFileName(newName)) {
            return false;
        }
        String path = AppEnv.instance().projectFileDir();

        FileUtil.rename(path + mName + Configuration.IMAGE_FILE_EXT, path + newName + Configuration.IMAGE_FILE_EXT);

        if (FileUtil.rename(path + mName + Configuration.PROJECT_FILE_EXT, path + newName + Configuration.PROJECT_FILE_EXT)) {
            mName = newName;
            return true;
        }
        return false;
    }

    public boolean delete() {
        String path = AppEnv.instance().projectFileDir();

        // delete preview
        FileUtil.delete(path + mName + Configuration.IMAGE_FILE_EXT);
        if (FileUtil.delete(path + mName + Configuration.PROJECT_FILE_EXT)) {
            return true;
        }
        return false;
    }

    public ProjectFile copy() {
        String path = AppEnv.instance().projectFileDir();
        String copiedName = FileUtil.availableFileName(mName);

        FileUtil.copy(path + mName + Configuration.IMAGE_FILE_EXT, path + copiedName + Configuration.IMAGE_FILE_EXT);
        if (FileUtil.copy(path + mName + Configuration.PROJECT_FILE_EXT, path + copiedName + Configuration.PROJECT_FILE_EXT)) {
            return new ProjectFile(copiedName);
        }
        return null;
    }
}