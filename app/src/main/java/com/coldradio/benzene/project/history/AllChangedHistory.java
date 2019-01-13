package com.coldradio.benzene.project.history;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

import java.util.ArrayList;
import java.util.List;

public class AllChangedHistory extends History {
    private List<Compound> mAllCompounds = new ArrayList<>();

    public AllChangedHistory(List<Compound> compoundList) {
        super((short) -1);

        for (Compound compound : compoundList) {
            mAllCompounds.add(compound.copy());
        }
    }

    @Override
    public void undo() {
        mAllCompounds = Project.instance().replaceAllCompounds(mAllCompounds);
    }

    @Override
    public void redo() {
        mAllCompounds = Project.instance().replaceAllCompounds(mAllCompounds);
    }
}
