package com.coldradio.benzene.library;

import android.content.res.Resources;
import android.graphics.PointF;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CompoundLibrary {
    private List<CompoundIndex> mAllCompounds = new ArrayList<>();
    private List<CompoundIndex> mFilteredCompounds;
    private SearchFilter mSearchFilter;
    private static CompoundLibrary smInstance = new CompoundLibrary();

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

    private CompoundLibrary() {
    }

    public static CompoundLibrary instance() {
        return smInstance;
    }

    public void parseLibrary(Resources resources) {
        Field[] fields = R.raw.class.getFields();
        Gson gson = new Gson();

        for (Field field : fields) {
            String res_name = field.getName();

            if (res_name.startsWith("structure")) {
                try {
                    Reader reader = new InputStreamReader(resources.openRawResource(field.getInt(field)));
                    PC_Compound_JSON compound_json = gson.fromJson(reader, CompoundStructure_JSON.class).PC_Compounds.get(0);

                    mAllCompounds.add(new CompoundIndex(compound_json.preferredIUPACName(), compound_json.otherNames(),
                            compoundFromStructure(compound_json)));
                } catch (IllegalAccessException iae) {
                    // skip this resource
                }
            }
        }
    }

    public CompoundIndex getCompoundIndex(int index) {
        if (mSearchFilter == null) {
            if (index >= 0 && index < mAllCompounds.size()) {
                return mAllCompounds.get(index);
            }
        } else {
            if (index >= 0 && index < mFilteredCompounds.size()) {
                return mFilteredCompounds.get(index);
            }
        }
        return null;
    }

    public int size() {
        if (mSearchFilter == null)
            return mAllCompounds.size();
        else
            return mFilteredCompounds.size();
    }

    public void setSearchFilter(SearchFilter filter) {
        if (mSearchFilter != null && filter.subsetOf(mSearchFilter)) {
            mFilteredCompounds = filter.filtered(mFilteredCompounds);
        } else {
            mFilteredCompounds = filter.filtered(mAllCompounds);
        }
        mSearchFilter = filter;
    }

    public SearchFilter getSearchFilter() {
        return mSearchFilter;
    }

    public void resetSearchFilter() {
        mSearchFilter = null;
    }
}
