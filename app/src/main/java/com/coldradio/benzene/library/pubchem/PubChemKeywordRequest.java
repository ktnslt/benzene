package com.coldradio.benzene.library.pubchem;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

class PubChemKeywordRequest extends Request<AutoComplete_JSON> {
    private Response.Listener<CompoundProperty_JSON> mCompoundPropertyListener;
    private String mKeyword;

    PubChemKeywordRequest(String keyword, final Response.Listener<CompoundProperty_JSON> propertyListener, Response.ErrorListener propertyErrorListener) {
        // Notice that errorListener is called for AutoComplete request not for the individual CompoundProperty request
        super(Method.GET,
                "https://pubchem.ncbi.nlm.nih.gov/rest/autocomplete/compound/" + keyword + "/json?limit=" + Configuration.MAX_RESPONSE_FOR_SEARCH,
                propertyErrorListener);

        mCompoundPropertyListener = propertyListener;
        mKeyword = keyword;
    }

    @Override
    protected void deliverResponse(AutoComplete_JSON response) {
        // nothing to deliver since this is AutoComplete_JSON
        if (response.total == 0) {
            Notifier.instance().notification("No Compounds found");
        }
        CompoundLibrary.instance().setTotalSearchedCompounds(response.total);
    }

    @Override
    protected Response<AutoComplete_JSON> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            AutoComplete_JSON autoComplete_json = AppEnv.instance().gson().fromJson(json, AutoComplete_JSON.class);

            if (autoComplete_json.total > 0) {
                for (final String name : autoComplete_json.dictionary_terms.compound) {
                    PubChemPropertyRequest request = new PubChemPropertyRequest(name, mCompoundPropertyListener, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            CompoundLibrary.instance().arrived(new PubChemCompoundIndex(mKeyword, null, -1, null, -1, null));
                        }
                    });

                    AppEnv.instance().addToNetworkQueue(request);
                }
            }
            return Response.success(autoComplete_json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
