package com.coldradio.benzene.library.pubchem;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.ScreenInfo;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class PubChemCompoundRequest extends Request<Compound> {
    private Response.Listener<Compound> mListener;

    PubChemCompoundRequest(int cid, final Response.Listener<Compound> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + String.valueOf(cid) + "/JSON", errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(Compound response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<Compound> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            PC_Compound_JSON compound_json = AppEnv.instance().gson().fromJson(json, CompoundStructure_JSON.class).PC_Compounds.get(0);
            Compound compound = RuleSet.instance().apply(PubChemCompoundFactory.create(compound_json));

            CompoundArranger.zoomToStandard(compound, 1);
            CompoundArranger.alignCenter(compound, ScreenInfo.instance().centerPoint());

            return Response.success(compound, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
