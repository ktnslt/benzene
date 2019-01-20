package com.coldradio.benzene.library.pubchem;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.TextUtil;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

class PubChemPropertyRequest extends PubChemRequest<CompoundProperty_JSON> {
    private String mCamelName;

    PubChemPropertyRequest(String name, Response.Listener<CompoundProperty_JSON> listener, Response.ErrorListener errorListener) {
        super("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/" + name + "/property/MolecularFormula,MolecularWeight,IUPACName/JSON",
                CompoundProperty_JSON.class, listener, errorListener);
        mCamelName = TextUtil.toCamelStyle(name);
    }

    private void requestPNG(final int cid) {
        String uri = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + String.valueOf(cid) + "/PNG?image_size=small";

        ImageRequest imageRequest = new ImageRequest(uri, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                CompoundLibrary.instance().arrived(cid, response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppEnv.instance().addToNetworkQueue(imageRequest);
    }

    @Override
    protected Response<CompoundProperty_JSON> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            CompoundProperty_JSON compoundProp = super.gson.fromJson(json, CompoundProperty_JSON.class);
            CompoundProperty_JSON.Property_JSON prop = compoundProp.PropertyTable.Properties.get(0);

            prop.Name = mCamelName;
            requestPNG(prop.CID);

            return Response.success(compoundProp, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            // get(0) might throw exception
            return Response.error(new ParseError(e));
        }
    }
}
