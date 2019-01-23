package com.coldradio.benzene.library.pubchem;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.ScreenInfo;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class PubChemCompoundRequest extends Request<List<Compound>> {
    private Response.Listener<List<Compound>> mListener;

    PubChemCompoundRequest(int cid, final Response.Listener<List<Compound>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + String.valueOf(cid) + "/JSON", errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(List<Compound> response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<List<Compound>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            PC_Compound_JSON compound_json = AppEnv.instance().gson().fromJson(json, CompoundStructure_JSON.class).PC_Compounds.get(0);

            Compound rawCompound = PubChemCompoundFactory.create(compound_json);
            // to do alignCenter(), the compound shall be large enough. This is why zoomToStandard shall be called first
            rawCompound = CompoundArranger.alignCenter(CompoundArranger.zoomToStandard(rawCompound, 1), ScreenInfo.instance().centerPoint());
            List<Compound> compounds = CompoundInspector.split(rawCompound);

            for (Compound compound : compounds) {
                RuleSet.instance().apply(compound);
                CompoundArranger.zoomToStandard(compound, 1);
            }

            return Response.success(compounds, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
