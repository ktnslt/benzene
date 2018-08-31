package com.coldradio.benzene.project;

import com.coldradio.benzene.compound.Compound;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private static final Project project = new Project();
    private List<Compound> mCompoundList = new ArrayList<>();

    public static Project instance() {
        return project;
    }

    public void addCompound(Compound compound) {
        mCompoundList.add(compound);
    }
}
