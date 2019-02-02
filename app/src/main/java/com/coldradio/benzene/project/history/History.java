package com.coldradio.benzene.project.history;

public abstract class History {
    protected final short mCID;

    public History(short cID) {
        mCID = cID;
    }

    public abstract void undo();

    public abstract void redo();
}
