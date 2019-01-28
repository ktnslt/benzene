package com.coldradio.benzene.library.pubchem;

import android.text.Html;
import android.text.Spanned;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.coldradio.benzene.util.AppEnv;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

class PubChemDescriptionRequest extends Request<Spanned> {
    private Response.Listener<Spanned> mListener;

    private Spanned buildDescriptionString(Description_JSON desc) {
        StringBuilder builder = new StringBuilder();

        for (Description_JSON.Information_JSON info : desc.InformationList.Information) {
            if (info.Description != null && info.DescriptionSourceName != null) {
                builder.append("<p><b>- Description:</b> ");
                builder.append(info.Description);
                builder.append(" <i>from ");
                builder.append(info.DescriptionSourceName);
                builder.append("</i></p>");
            }
        }

        if (builder.length() == 0) {
            builder.append("No Description for this compound");
        }

        return Html.fromHtml(builder.toString());
    }

    PubChemDescriptionRequest(int cid, final Response.Listener<Spanned> listener) {
        super(Method.GET, "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + cid + "/description/JSON", new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(Html.fromHtml("Error. try next time. " + error.toString()));
            }
        });
        mListener = listener;
    }

    @Override
    protected void deliverResponse(Spanned response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<Spanned> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Description_JSON desc = AppEnv.instance().gson().fromJson(json, Description_JSON.class);

            return Response.success(buildDescriptionString(desc), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
