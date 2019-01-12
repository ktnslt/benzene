package com.coldradio.benzene.project.history;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

public class CompoundDeletedHistory extends History {
    public final Compound mDeletedCompound;

    public CompoundDeletedHistory(Compound compound) {
        super(-1);
        mDeletedCompound = compound;
    }

    @Override
    public void undo() {
        Project.instance().addCompound(mDeletedCompound, false);
    }

    @Override
    public void redo() {
        Project.instance().removeCompound(mDeletedCompound);
    }
}
