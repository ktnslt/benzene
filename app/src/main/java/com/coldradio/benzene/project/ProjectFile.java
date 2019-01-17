package com.coldradio.benzene.project;

import com.coldradio.benzene.util.Environment;
import com.coldradio.benzene.util.FileUtil;

import java.util.Comparator;

public class ProjectFile {
    public static class NameComparator implements Comparator<ProjectFile> {
        private boolean mReversed = false;

        public NameComparator() {
        }

        public NameComparator(boolean reversed) {
            mReversed = reversed;
        }

        @Override
        public int compare(ProjectFile o1, ProjectFile o2) {
            return mReversed ? o2.mName.compareTo(o1.mName) : o1.mName.compareTo(o2.mName);
        }
    }
    public static class LastModifiedTimeComparator implements Comparator<ProjectFile> {
        private boolean mReversed = false;

        public LastModifiedTimeComparator() {
        }

        public LastModifiedTimeComparator(boolean reversed) {
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

    public ProjectFile(String fileName) {
        mName = fileName;
        mLastModifiedTime = FileUtil.lastModifiedTime(Environment.instance().projectFilePath() + mName + Configuration.PROJECT_FILE_EXT);
    }

    public ProjectFile(String fileName, boolean createdNew) {
        mName = fileName;
        mLastModifiedTime = FileUtil.lastModifiedTime(Environment.instance().projectFilePath() + mName + Configuration.PROJECT_FILE_EXT);

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
        mLastModifiedTime = System.currentTimeMillis();
    }

    public boolean rename(String newName) {
        if (!FileUtil.validFileName(newName)) {
            return false;
        }
        String path = Environment.instance().projectFilePath();

        FileUtil.rename(path + mName + Configuration.IMAGE_FILE_EXT, path + newName + Configuration.IMAGE_FILE_EXT);

        if (FileUtil.rename(path + mName + Configuration.PROJECT_FILE_EXT, path + newName + Configuration.PROJECT_FILE_EXT)) {
            mName = newName;
            return true;
        }
        return false;
    }

    public boolean delete() {
        String path = Environment.instance().projectFilePath();

        // delete preview
        FileUtil.delete(path + mName + Configuration.IMAGE_FILE_EXT);
        if (FileUtil.delete(path + mName + Configuration.PROJECT_FILE_EXT)) {
            return true;
        }
        return false;
    }

    public ProjectFile copy() {
        String path = Environment.instance().projectFilePath();

        String copiedName = FileUtil.nameWithoutExtension(FileUtil.availableFileName(path, mName, Configuration.PROJECT_FILE_EXT), Configuration.PROJECT_FILE_EXT);

        FileUtil.copy(path + mName + Configuration.IMAGE_FILE_EXT, path + copiedName + Configuration.IMAGE_FILE_EXT);
        if (FileUtil.copy(path + mName + Configuration.PROJECT_FILE_EXT, path + copiedName + Configuration.PROJECT_FILE_EXT)) {
            return new ProjectFile(copiedName);
        }
        return null;
    }
}