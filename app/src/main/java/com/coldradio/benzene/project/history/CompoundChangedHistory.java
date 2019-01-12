package com.coldradio.benzene.project.history;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

public class CompoundChangedHistory extends History {
    private final Compound mOriginalCompound;
    private Compound mUndoCompound;

    public CompoundChangedHistory(Compound compound) {
        super(compound.getID());
        mOriginalCompound = compound.copy();
    }

    @Override
    public void undo() {
        mUndoCompound = Project.instance().replaceCompound(mCID, mOriginalCompound);
    }

    @Override
    public void redo() {
        Project.instance().replaceCompound(mCID, mUndoCompound);
    }
}
