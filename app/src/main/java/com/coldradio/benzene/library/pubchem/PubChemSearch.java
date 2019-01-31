package com.coldradio.benzene.library.pubchem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.library.ICompoundSearch;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;

import java.io.UnsupportedEncodingException;
import java.util.List;

/*
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
    @Override
    public List<CompoundIndex> search(KeywordType keywordType, final String keyword) {
        AppEnv.instance().cancelAllNetworkRequest();

        try {
            PubChemKeywordRequest request = new PubChemKeywordRequest(keyword, new Response.Listener<CompoundProperty_JSON>() {
                @Override
                public void onResponse(CompoundProperty_JSON response) {
                    try {
                        CompoundProperty_JSON.Property_JSON prop = response.PropertyTable.Properties.get(0);

                        CompoundLibrary.instance().arrived(new PubChemCompoundIndex(keyword, prop.Name, prop.CID, prop.MolecularFormula, prop.MolecularWeight, prop.IUPACName));
                    } catch (Exception e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Notifier.instance().notification(error.toString());
                }
            });

            AppEnv.instance().addToNetworkQueue(request);
        } catch (UnsupportedEncodingException usee) {
            Notifier.instance().notification(usee.toString());
        }
        return null;
    }
}
