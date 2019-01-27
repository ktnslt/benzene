package com.coldradio.benzene.library.local;

import android.text.Spanned;

import com.android.volley.Response;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;

import java.util.List;

public class LocalCompoundIndex extends CompoundIndex {
    LocalCompoundIndex(String searchKeyword, String title, int cid, String mf, float mw, String IUPAC) {
        super(searchKeyword, title, cid, mf, mw, IUPAC);
    }

    @Override
    public void requestCompound(Response.Listener<List<Compound>> onCompoundReady) {

    }

    @Override
    public void requestDescription(Response.Listener<Spanned> listener) {

    }
}
