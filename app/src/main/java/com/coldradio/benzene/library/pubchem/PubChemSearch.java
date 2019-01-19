package com.coldradio.benzene.library.pubchem;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.library.ICompoundSearch;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Notifier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.List;

/* PUG Examples. see for details: https://pubchemdocs.ncbi.nlm.nih.gov/pug-rest$_Toc494865567
    to retrieve name
     - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244,112/synonyms/JSON
    search
     - TEXT search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/glucose/cids/JSON?name_type=word
     - TEXT search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/glucose/cids/JSON?name_type=complete
     - CID search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/cids/JSON
     - SMILES search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/smiles/C1CCCCCC1/JSON
    overall info for cid
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244,112/property/MolecularFormula,MolecularWeight,IUPACName/JSON
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/benzene/property/MolecularFormula,MolecularWeight,IUPACName/JSON
    JSON structure
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/JSON
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/benzene/JSON
    PNG
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/PNG
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/PNG?image_size=small
    description
    - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/description/JSON
    autocomplete
    - https://pubchem.ncbi.nlm.nih.gov/rest/autocomplete/compound/ben/jsonp?limit=30
 */
public class PubChemSearch implements ICompoundSearch {
    private RequestQueue mRequestQueue;
    private Context mContext;
    private Gson mGson = new GsonBuilder().create();

    private void requestForEachCompound(List<String> compoundNames) {
        for (final String name : compoundNames) {
            String uri = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/" + name + "/property/MolecularFormula,MolecularWeight,IUPACName/JSON";
            StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        CompoundProperty_JSON propertyTable_json = mGson.fromJson(response, CompoundProperty_JSON.class);
                        CompoundProperty_JSON.Property_JSON prop = propertyTable_json.PropertyTable.Properties.get(0);
                        CompoundLibrary.instance().arrived(new CompoundIndex(name, prop.CID, prop.MolecularFormula, prop.MolecularWeight, prop.IUPACName, null), -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Notifier.instance().notification(error.toString());
                }
            });

            mRequestQueue.add(request);
        }
    }

    public PubChemSearch(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public List<CompoundIndex> search(KeywordType keywordType, String keyword) {
        mRequestQueue.cancelAll(mContext);

        String uri = "https://pubchem.ncbi.nlm.nih.gov/rest/autocomplete/compound/" + keyword + "/json?limit=" + Configuration.MAX_REQUEST_LIMIT_FOR_SEARCH;
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    AutoComplete_JSON autoComplete_json = mGson.fromJson(response, AutoComplete_JSON.class);
                    PubChemSearch.this.requestForEachCompound(autoComplete_json.dictionary_terms.compound);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Notifier.instance().notification(error.toString());
            }
        });

        mRequestQueue.add(request);
        return null;
    }
}
