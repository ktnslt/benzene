package com.coldradio.benzene.library.pubchem;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

class PubChemPropertyRequest extends PubChemRequest<CompoundProperty_JSON> {
    private String mName;

    PubChemPropertyRequest(String name, Response.Listener<CompoundProperty_JSON> listener, Response.ErrorListener errorListener) {
        super("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/" + name + "/property/MolecularFormula,MolecularWeight,IUPACName/JSON",
                CompoundProperty_JSON.class, listener, errorListener);
        mName = name;
    }

    private void requestPNG(int cid) {
        
    }

    @Override
    protected Response<CompoundProperty_JSON> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            CompoundProperty_JSON compoundProp = super.gson.fromJson(json, CompoundProperty_JSON.class);
            CompoundProperty_JSON.Property_JSON prop = compoundProp.PropertyTable.Properties.get(0);

            prop.Name = mName;
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
