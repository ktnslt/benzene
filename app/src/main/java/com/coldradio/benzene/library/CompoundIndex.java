package com.coldradio.benzene.library;

import android.graphics.Bitmap;
import android.text.Spanned;

import com.android.volley.Response;
import com.coldradio.benzene.compound.Compound;

import java.util.List;

public abstract class CompoundIndex {
    final public int searchID;
    final public String title;
    final public int cid;
    final public String mf;
    final public float mw;
    final public String IUPAC;
    private Bitmap bitmap;
    public Spanned description;

    public CompoundIndex(int searchID, String title, int cid, String mf, float mw, String IUPAC) {
        this.searchID = searchID;
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

    public abstract void requestCompound(Response.Listener<List<Compound>> onCompoundReady);

    public abstract void requestDescription(Response.Listener<Spanned> listener);
}