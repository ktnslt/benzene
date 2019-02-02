package com.coldradio.benzene.project.history;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;

public class CompoundMovedHistory extends History {
    private PointF mOrigPosition = new PointF();
    private PointF mPoint = new PointF();

    public CompoundMovedHistory(Compound compound) {
        super(compound.getID());
        mOrigPosition.set(compound.getAtoms().get(0).getPoint());
    }

    @Override
    public void undo() {
        Compound movedCompound = Project.instance().findCompound(mCID);

        mPoint.set(movedCompound.getAtoms().get(0).getPoint());

        movedCompound.offset(mOrigPosition.x - mPoint.x, mOrigPosition.y - mPoint.y);
        mOrigPosition.set(mPoint);
    }

    @Override
    public void redo() {
        Compound movedCompound = Project.instance().findCompound(mCID);

        mPoint.set(movedCompound.getAtoms().get(0).getPoint());

        movedCompound.offset(mOrigPosition.x - mPoint.x, mOrigPosition.y - mPoint.y);
        mOrigPosition.set(mPoint);
    }
}
