package com.coldradio.benzene.project;

import android.content.Context;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.util.Helper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

public class ProjectFileManager {
    private static ProjectFileManager smInstance = new ProjectFileManager();
    private List<ProjectFile> mSavedProjects = new ArrayList<>();
    private String mProjectFileRootDir;
    private Type mCompoundListType = new TypeToken<List<Compound>>() {
    }.getType();
    private Gson mGson = new GsonBuilder().create();
    private String FILE_EXTENSION = ".bzn";

    private String defaultProjectName() {
        for (int ii = 1; ; ++ii) {
            String defaultName = "Untitled" + String.valueOf(ii);

            if (getProjectFile(defaultName) == null) {
                return defaultName;
            }
        }
    }

    public static ProjectFileManager instance() {
        return smInstance;
    }

    public void setContext(Context context) {
        mProjectFileRootDir = context.getFilesDir().getPath() + "/";
    }

    public void save(Project project) {
        Writer writer = null;

        try {
            File file = new File(mProjectFileRootDir + project.getProjectFile().getName() + FILE_EXTENSION);
            file.createNewFile();

            writer = new FileWriter(file);
            project.preSerialization();
            mGson.toJson(project.getCompounds(), writer);
        } catch (IOException ioe) {
            Helper.instance().notification("Saving Errors");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    Helper.instance().notification("Saved");
                } catch (Exception e) {/* ignores this */}
            }
        }
    }

    public void load(String fileName, Project project) {
        Reader reader = null;

        try {
            File file = new File(mProjectFileRootDir + fileName + FILE_EXTENSION);
            reader = new FileReader(file);

            List<Compound> readCompounds = mGson.fromJson(reader, mCompoundListType);

            project.resetWithCompounds(readCompounds);
            project.postDeserialization();
        } catch (FileNotFoundException fnfe) {
            Helper.instance().notification("File Not Found.");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {/* ignores this */}
            }
        }
    }

    public void load() {
        mSavedProjects.clear();
        File dir = new File(mProjectFileRootDir);

        if (dir.exists()) {
            File[] files = dir.listFiles();

            for (int ii = 0; ii < files.length; ++ii) {
                if (! files[ii].isDirectory() && files[ii].getName().endsWith(FILE_EXTENSION)) {
                    String nameWithoutExtension = files[ii].getName().substring(0, files[ii].getName().length() - FILE_EXTENSION.length());
                    mSavedProjects.add(new ProjectFile(nameWithoutExtension));
                }
            }
        }
    }

    public ProjectFile createNew() {
        return new ProjectFile(defaultProjectName());
    }

    public ProjectFile getProjectFile(String projectName) {
        for (ProjectFile file : mSavedProjects) {
            if (file.getName().equals(projectName)) {
                return file;
            }
        }
        return null;
    }

    public ProjectFile getProjectFile(int index) {
        return mSavedProjects.get(index);
    }

    public int projectFileNumber() {
        return mSavedProjects.size();
    }
}
