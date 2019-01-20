package com.coldradio.benzene.library;

import android.graphics.Bitmap;

public class CompoundIndex {
    final public String title;
    final public int cid;
    final public String mf;
    final public float mw;
    final public String IUPAC;
    private Bitmap bitmap;

    public CompoundIndex(String title, int cid, String mf, float mw, String IUPAC) {
        this.title = title;
        this.cid = cid;
        this.mf = mf;
        this.mw = mw;
        this.IUPAC = IUPAC;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}