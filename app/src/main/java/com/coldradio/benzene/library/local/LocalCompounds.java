package com.coldradio.benzene.library.local;

import android.content.res.Resources;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.ICompoundSearch;
import com.coldradio.benzene.library.pubchem.CompoundStructure_JSON;
import com.coldradio.benzene.library.pubchem.PC_Compound_JSON;
import com.coldradio.benzene.library.pubchem.PubChemCompoundFactory;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.util.FileUtil;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LocalCompounds {
    private List<CompoundIndex> mAllCompounds = new ArrayList<>();
    private static LocalCompounds smInstance = new LocalCompounds();

    private LocalCompounds() {
    }

    public static LocalCompounds instance() {
        return smInstance;
    }

    public void parseLibrary(Resources resources) {
        if (mAllCompounds.isEmpty()) {
            Field[] fields = R.raw.class.getFields();
            Gson gson = new Gson();

            for (Field field : fields) {
                String res_name = field.getName();

                if (res_name.startsWith("structure")) {
                    Reader reader = null;
                    try {
                        reader = new InputStreamReader(resources.openRawResource(field.getInt(field)));
                        PC_Compound_JSON compound_json = gson.fromJson(reader, CompoundStructure_JSON.class).PC_Compounds.get(0);
                        Compound cmpd = RuleSet.instance().apply(PubChemCompoundFactory.create(compound_json));

                        mAllCompounds.add(new LocalCompoundIndex("", -1, "", -1, compound_json.preferredIUPACName()));
                    } catch (IllegalAccessException iae) {
                        // skip this resource
                    } finally {
                        FileUtil.closeIgnoreException(reader);
                    }
                }
            }
        }
    }

    public CompoundIndex getCompoundIndex(int index) {
        if (index >= 0 && index < mAllCompounds.size()) {
            return mAllCompounds.get(index);
        }
        return null;
    }

    public int size() {
        return mAllCompounds.size();
    }

    public List<CompoundIndex> search(ICompoundSearch.KeywordType keywordType, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        List<CompoundIndex> result = new ArrayList<>();

        for (CompoundIndex index : mAllCompounds) {
            if (index.title.contains(keyword) || index.IUPAC.contains(keyword) || String.valueOf(index.cid).contains(keyword)
                || index.mf.contains(keyword))
                result.add(index);
        }
        return result;
    }
}
