package com.coldradio.benzene.library.local;

import android.content.res.Resources;
import android.graphics.PointF;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.ICompoundSearch;
import com.coldradio.benzene.library.pubchem.CompoundStructure_JSON;
import com.coldradio.benzene.library.pubchem.PC_Compound_JSON;
import com.coldradio.benzene.library.pubchem.conformer_JSON;
import com.coldradio.benzene.library.pubchem.coords_JSON;
import com.coldradio.benzene.library.rule.RuleSet;
import com.coldradio.benzene.library.pubchem.style_JSON;
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

    private AtomicNumber[] toAtomicNumber(int[] atomicNumberInts) {
        AtomicNumber[] atomicNumberEnums = new AtomicNumber[atomicNumberInts.length];

        for (int ii = 0; ii < atomicNumberEnums.length; ++ii) {
            atomicNumberEnums[ii] = AtomicNumber.values()[atomicNumberInts[ii]];
        }

        return atomicNumberEnums;
    }

    private Compound compoundFromStructure(PC_Compound_JSON compound_json) {
        Compound compound = new Compound(compound_json.atoms.aid, toAtomicNumber(compound_json.atoms.element));

        // setup bonds
        for (int ii = 0; ii < compound_json.bondLength(); ++ii) {
            compound.makeBond(compound_json.bonds.aid1[ii], compound_json.bonds.aid2[ii], compound_json.bondType(ii));
        }

        // set annotation
        // annotation cannot be set at the same time with bonds. e.g.) for annotation 1-->2 Wedge_up, makeBond(2, 1) is called not for the makeBond(1, 2)
        style_JSON style = compound_json.getStyle();

        if (style != null) {
            for (int ii = 0; ii < style.annotation.length; ++ii)
                compound.setBondAnnotation(style.aid1[ii], style.aid2[ii], style.bondAnnotation(style.annotation[ii]));
        }

        // setup coordinates
        coords_JSON coord = compound_json.coords.get(0);

        for (int ii = 0; ii < coord.aid.length; ++ii) {
            conformer_JSON xy = coord.conformers.get(0);
            compound.getAtom(coord.aid[ii]).setPoint(new PointF(xy.x[ii], xy.y[ii]));
        }

        CompoundArranger.zoomToStandard(compound, 0.5f);
        CompoundArranger.adjustDoubleBondType(compound);

        return compound;
    }

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
                        Compound cmpd = RuleSet.instance().apply(compoundFromStructure(compound_json));

                        mAllCompounds.add(new CompoundIndex("", -1, "", -1, compound_json.preferredIUPACName(), ""));
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
