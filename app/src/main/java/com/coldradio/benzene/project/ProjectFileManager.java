package com.coldradio.benzene.project;

import android.net.Uri;
import android.view.View;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.history.AllChangedHistory;
import com.coldradio.benzene.project.history.CompoundChangedHistory;
import com.coldradio.benzene.project.history.CompoundDeletedHistory;
import com.coldradio.benzene.project.history.CompoundMovedHistory;
import com.coldradio.benzene.project.history.History;
import com.coldradio.benzene.project.history.HistoryManager;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.FileUtil;
import com.coldradio.benzene.util.Notifier;
import com.coldradio.benzene.util.SearchFilter;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectFileManager {
    public enum SortType {
        SORT_BY_NAME, SORT_BY_NAME_REV, SORT_BY_OLD, SORT_BY_RECENT
    }
    public interface OnChangeListener {
        void saved();
        void changed();
    }

    private enum ChangeEventType {
        SAVED, CHANGED
    }
    private static ProjectFileManager smInstance = new ProjectFileManager();
    private List<ProjectFile> mStoredProjectsInDevice = new ArrayList<>();
    private Type mCompoundListType = new TypeToken<List<Compound>>() {}.getType();
    private List<OnChangeListener> mListener = new ArrayList<>();
    private HistoryManager mHistoryManager = new HistoryManager();
    private boolean mCurrentProjectChanged = false;
    private SortType mSortType = SortType.SORT_BY_RECENT;
    private SearchFilter<ProjectFile> mFilter;
    private List<ProjectFile> mFilteredStoredProjectsInDevice;

    private void sort(List<ProjectFile> projectFiles) {
        if (mSortType == SortType.SORT_BY_NAME) {
            Collections.sort(projectFiles, new ProjectFile.NameComparator());
        } else if (mSortType == SortType.SORT_BY_NAME_REV) {
            Collections.sort(projectFiles, new ProjectFile.NameComparator(true));
        } else if (mSortType == SortType.SORT_BY_OLD) {
            Collections.sort(projectFiles, new ProjectFile.LastModifiedTimeComparator());
        } else if (mSortType == SortType.SORT_BY_RECENT) {
            Collections.sort(projectFiles, new ProjectFile.LastModifiedTimeComparator(true));
        }
    }

    private void notifyListener(ChangeEventType eventType) {
        for (OnChangeListener listener : mListener) {
            if (eventType == ChangeEventType.SAVED) {
                listener.saved();
            } else if (eventType == ChangeEventType.CHANGED) {
                listener.changed();
                mCurrentProjectChanged = true;
            }
        }
    }

    private ProjectFile getProjectFile(String projectName, List<ProjectFile> projectFileList) {
        for (ProjectFile file : projectFileList) {
            if (file.getName().equals(projectName)) {
                return file;
            }
        }
        return null;
    }

    private List<ProjectFile> chooseProjectList() {
        return mFilteredStoredProjectsInDevice != null ? mFilteredStoredProjectsInDevice : mStoredProjectsInDevice;
    }

    public static ProjectFileManager instance() {
        return smInstance;
    }

    public boolean isCurrentProjectChanged() {
        return mCurrentProjectChanged;
    }

    public void saveWithoutPreview(Project project) {
        Writer writer = null;

        try {
            ProjectFile projectFile = project.getProjectFile();

            if (!projectFile.hasSavedFile() && project.isEmpty()) {
                // created as new, but nothing in it case
                return;
            }
            File file = new File(AppEnv.instance().projectFileDir() + project.getProjectFile().getName() + Configuration.PROJECT_FILE_EXT);
            file.createNewFile();

            writer = new FileWriter(file);
            project.preSerialization();
            AppEnv.instance().gson().toJson(project.getCompounds(), writer);

            if (getProjectFile(projectFile.getName()) == null) {
                mStoredProjectsInDevice.add(0, projectFile);
                if (mFilteredStoredProjectsInDevice != null) {
                    mFilteredStoredProjectsInDevice.add(0, projectFile);
                }
            }
            projectFile.saved();
            notifyListener(ChangeEventType.SAVED);
        } catch (IOException ioe) {
            Notifier.instance().notification("Saving Errors");
        } finally {
            FileUtil.closeIgnoreException(writer);
        }
    }

    public void savePreviewOnly(Project project, View view) {
        if (isCurrentProjectChanged() || !PreviewHandler.hasPreviewFile(project.getProjectFile().getName())) {
            PreviewHandler.savePreview(view, project.rectRegion(), project.getProjectFile().getName());
        }
    }

    public void loadToProject(String fileName, Project project) {
        Reader reader = null;

        try {
            File file = new File(AppEnv.instance().projectFileDir() + fileName + Configuration.PROJECT_FILE_EXT);
            reader = new FileReader(file);

            List<Compound> readCompounds = AppEnv.instance().gson().fromJson(reader, mCompoundListType);

            if (readCompounds == null) {
                ProjectFile projectFile = getProjectFile(fileName);

                project.createFromFile(new ArrayList<Compound>(), projectFile);
                Notifier.instance().notification("No Compound loaded from " + projectFile.getName());
            } else {
                project.createFromFile(readCompounds, getProjectFile(fileName));
            }
            project.postDeserialization();
        } catch (FileNotFoundException fnfe) {
            Notifier.instance().notification("File Not Found.");
        } finally {
            FileUtil.closeIgnoreException(reader);
        }
    }

    public void initProjectListAtStartUp() {
        mStoredProjectsInDevice.clear();
        File dir = new File(AppEnv.instance().projectFileDir());

        if (dir.exists()) {
            File[] files = dir.listFiles();

            for (int ii = 0; ii < files.length; ++ii) {
                if (!files[ii].isDirectory() && files[ii].getName().endsWith(Configuration.PROJECT_FILE_EXT)) {
                    mStoredProjectsInDevice.add(new ProjectFile(FileUtil.fileNameWithoutExt(files[ii].getName())));
                }
            }
        }
        sort(mStoredProjectsInDevice);
    }

    public ProjectFile createNew() {
        String fileName = FileUtil.availableFileName("Untitled");

        return new ProjectFile(fileName, true);
    }

    public ProjectFile getProjectFile(String projectName) {
        return getProjectFile(projectName, chooseProjectList());
    }

    public ProjectFile getProjectFile(int index) {
        return chooseProjectList().get(index);
    }

    public int numberOfProjects() {
        return chooseProjectList().size();
    }

    public boolean rename(String projectName, String newProjectName) {
        ProjectFile projectFile = getProjectFile(projectName);

        if (projectFile != null && projectFile.rename(newProjectName)) {
            return true;
        }
        return false;
    }

    public boolean delete(String projectName) {
        ProjectFile projectFile = getProjectFile(projectName);

        if (projectFile != null && projectFile.delete()) {
            mStoredProjectsInDevice.remove(projectFile);
            if (mFilteredStoredProjectsInDevice != null) {
                mFilteredStoredProjectsInDevice.remove(projectFile);
            }
            return true;
        }
        return false;
    }

    public int copy(String projectName) {
        ProjectFile projectFile = getProjectFile(projectName);
        int insertedIndex = -1;

        if (projectFile != null) {
            ProjectFile copiedFile = projectFile.copy();

            if (copiedFile != null) {
                insertedIndex = mStoredProjectsInDevice.indexOf(projectFile) + 1;
                mStoredProjectsInDevice.add(insertedIndex, copiedFile);

                if (mFilteredStoredProjectsInDevice != null) {
                    insertedIndex = mFilteredStoredProjectsInDevice.indexOf(projectFile) + 1;
                    mFilteredStoredProjectsInDevice.add(insertedIndex, copiedFile);
                }
            }
        }
        return insertedIndex;
    }

    public void addListener(OnChangeListener listener) {
        if (!mListener.contains(listener)) {
            mListener.add(listener);
        }
    }

    public void pushForDeletion() {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.selection() == ElementSelector.Selection.COMPOUND) {
            push(new CompoundDeletedHistory(elementSelector.getSelectedCompound()));
        } else {
            pushAllChangedHistory(Project.instance().getCompounds());
        }
    }

    public void pushForChange() {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.selection() == ElementSelector.Selection.PARTIAL) {
            pushAllChangedHistory(Project.instance().getCompounds());
        } else {
            pushCompoundChangedHistory(elementSelector.getSelectedCompound());
        }
    }

    public void pushForMove() {
        ElementSelector elementSelector = Project.instance().getElementSelector();

        if (elementSelector.selection() == ElementSelector.Selection.COMPOUND) {
            push(new CompoundMovedHistory(elementSelector.getSelectedCompound()));
        } else {
            pushForChange();
        }
    }

    public void pushCompoundChangedHistory(Compound compound) {
        mHistoryManager.push(new CompoundChangedHistory(compound));
        notifyListener(ChangeEventType.CHANGED);
    }

    public void push(History history) {
        mHistoryManager.push(history);
        notifyListener(ChangeEventType.CHANGED);
    }

    public void pushAllChangedHistory(List<Compound> compoundList) {
        mHistoryManager.push(new AllChangedHistory(compoundList));
        notifyListener(ChangeEventType.CHANGED);
    }

    public boolean redo() {
        Project.instance().getElementSelector().reset();
        if (mHistoryManager.redo()) {
            notifyListener(ChangeEventType.CHANGED);
            return true;
        } else {
            return false;
        }
    }

    public boolean undo() {
        Project.instance().getElementSelector().reset();
        if (mHistoryManager.undo()) {
            notifyListener(ChangeEventType.CHANGED);
            return true;
        } else {
            return false;
        }
    }

    public void clearHistory() {
        mHistoryManager.reset();
    }

    public void sortByNext() {
        mSortType = SortType.values()[(mSortType.ordinal() + 1) % SortType.values().length];
        sort(chooseProjectList());
        Notifier.instance().notification(mSortType.toString());
    }

    public void setFilter(SearchFilter<ProjectFile> filter) {
        mFilter = filter;

        mFilteredStoredProjectsInDevice = null;
        if (mFilter != null) {
            mFilteredStoredProjectsInDevice = mFilter.filtered(mStoredProjectsInDevice);
        }
    }

    public boolean hasFilter() {
        return mFilter != null;
    }

    public SearchFilter<ProjectFile> getFilter() {
        return mFilter;
    }

    public boolean importProject(Uri uri) {
        String fileName = FileUtil.availableFileName("Imported");

        if (FileUtil.copy(uri, AppEnv.instance().projectFileDir() + fileName + Configuration.PROJECT_FILE_EXT)) {
            mStoredProjectsInDevice.add(0, new ProjectFile(fileName));
            return true;
        }
        return false;
    }
}
