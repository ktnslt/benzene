package com.coldradio.benzene.project.history;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

public class CompoundAddedHistory extends History {
    private Compound mAddedCompound;

    public CompoundAddedHistory(Compound compound) {
        super(compound.getID());
        mAddedCompound = compound.copy();
    }

    @Override
    public void undo() {
        Project.instance().removeCompound(Project.instance().findCompound(mCID));
    }

    @Override
    public void redo() {
        Project.instance().addCompound(mAddedCompound, false);
    }
}