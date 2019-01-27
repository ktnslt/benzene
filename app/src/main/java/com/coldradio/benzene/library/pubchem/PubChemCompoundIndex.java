package com.coldradio.benzene.library.pubchem;

import android.text.Spanned;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;

import java.util.List;

public class PubChemCompoundIndex extends CompoundIndex {
    PubChemCompoundIndex(String searchKeyword, String title, int cid, String mf, float mw, String IUPAC) {
        super(searchKeyword, title, cid, mf, mw, IUPAC);
    }

    @Override
    public void requestCompound(Response.Listener<List<Compound>> onCompoundReady) {
        PubChemCompoundRequest request = new PubChemCompoundRequest(super.cid, onCompoundReady, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Notifier.instance().notification(PubChemCompoundIndex.this.title + " NOT created. " + error.toString());
            }
        });

        AppEnv.instance().addToNetworkQueue(request);
        Notifier.instance().notification(PubChemCompoundIndex.this.title + " Requested");
    }

    @Override
    public void requestDescription(final Response.Listener<Spanned> listener) {
        PubChemDescriptionRequest request = new PubChemDescriptionRequest(super.cid, listener);

        AppEnv.instance().addToNetworkQueue(request);
    }
}
