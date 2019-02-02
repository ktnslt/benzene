package com.coldradio.benzene.project.history;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

public class CompoundChangedHistory extends History {
    private Compound mOriginalCompound;

    public CompoundChangedHistory(Compound compound) {
        super(compound.getID());
        mOriginalCompound = compound.copy();
    }

    @Override
    public void undo() {
        mOriginalCompound = Project.instance().replaceCompound(mCID, mOriginalCompound);
    }

    @Override
    public void redo() {
        mOriginalCompound = Project.instance().replaceCompound(mCID, mOriginalCompound);
    }
}
