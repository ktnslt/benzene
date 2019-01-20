package com.coldradio.benzene.library.pubchem;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.library.ICompoundSearch;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Notifier;

import java.util.List;

/**
 * PUG Examples. see for details: https://pubchemdocs.ncbi.nlm.nih.gov/pug-rest$_Toc494865567
 * to retrieve name
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244,112/synonyms/JSON
 * search
 *   - TEXT search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/glucose/cids/JSON?name_type=word
 *   - TEXT search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/glucose/cids/JSON?name_type=complete
 *   - CID search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/cids/JSON
 *   - SMILES search: https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/smiles/C1CCCCCC1/JSON
 * Property Request
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244,112/property/MolecularFormula,MolecularWeight,IUPACName/JSON
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/benzene/property/MolecularFormula,MolecularWeight,IUPACName/JSON
 * JSON structure
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/JSON
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/benzene/JSON
 * PNG
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/PNG
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/PNG?image_size=small
 * description
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/2244/description/JSON
 *  autocomplete
 *   - https://pubchem.ncbi.nlm.nih.gov/rest/autocomplete/compound/ben/json?limit=30
 */
public class PubChemSearch implements ICompoundSearch {
    private RequestQueue mRequestQueue;
    private Context mContext;

    private void requestPNG(int cid) {
        String uri = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + String.valueOf(cid) + "/PNG?image_size=small";

    }

    private void requestPropertyForEachCompound(List<String> compoundNames) {
        for (final String name : compoundNames) {
            String uri = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/" + name + "/property/MolecularFormula,MolecularWeight,IUPACName/JSON";
            PubChemRequest<CompoundProperty_JSON> request = new PubChemRequest<>(uri, CompoundProperty_JSON.class, new Response.Listener<CompoundProperty_JSON>() {
                @Override
                public void onResponse(CompoundProperty_JSON response) {
                    try {
                        CompoundProperty_JSON.Property_JSON prop = response.PropertyTable.Properties.get(0);
                        String camelName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                        CompoundLibrary.instance().arrived(new CompoundIndex(camelName, prop.CID, prop.MolecularFormula, prop.MolecularWeight, prop.IUPACName, ""), -1);
                        requestPNG(prop.CID);
                    } catch (Exception e) {}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

        String uri = "https://pubchem.ncbi.nlm.nih.gov/rest/autocomplete/compound/" + keyword + "/json?limit=" + Configuration.MAX_RESPONSE_FOR_SEARCH;
        PubChemRequest<AutoComplete_JSON> request = new PubChemRequest<>(uri, AutoComplete_JSON.class, new Response.Listener<AutoComplete_JSON>() {
            @Override
            public void onResponse(AutoComplete_JSON response) {
                requestPropertyForEachCompound(response.dictionary_terms.compound);
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
