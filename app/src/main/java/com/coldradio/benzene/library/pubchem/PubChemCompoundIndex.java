package com.coldradio.benzene.library.pubchem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;

public class PubChemCompoundIndex extends CompoundIndex {
    PubChemCompoundIndex(String title, int cid, String mf, float mw, String IUPAC) {
        super(title, cid, mf, mw, IUPAC);
    }

    @Override
    public void requestCompound(Response.Listener<Compound> onCompoundReady) {
        PubChemCompoundRequest request = new PubChemCompoundRequest(super.cid, onCompoundReady, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Notifier.instance().notification(PubChemCompoundIndex.this.title + " NOT created");
            }
        });

        AppEnv.instance().addToNetworkQueue(request);
    }
}
