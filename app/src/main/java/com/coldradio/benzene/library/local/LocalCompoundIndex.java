package com.coldradio.benzene.library.local;

import com.android.volley.Response;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;

public class LocalCompoundIndex extends CompoundIndex {
    LocalCompoundIndex(String title, int cid, String mf, float mw, String IUPAC) {
        super(title, cid, mf, mw, IUPAC);
    }

    @Override
    public void requestCompound(Response.Listener<Compound> onCompoundReady) {

    }
}
