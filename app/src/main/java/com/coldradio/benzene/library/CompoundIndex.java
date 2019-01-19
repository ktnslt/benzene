package com.coldradio.benzene.library;

public class CompoundIndex {
    final public String title;
    final public int cid;
    final public String mf;
    final public float mw;
    final public String IUPAC;
    final public String pngPath;

    public CompoundIndex(String title, int cid, String mf, float mw, String IUPAC, String pngPath) {
        this.title = title;
        this.cid = cid;
        this.mf = mf;
        this.mw = mw;
        this.IUPAC = IUPAC;
        this.pngPath = pngPath;
    }
}