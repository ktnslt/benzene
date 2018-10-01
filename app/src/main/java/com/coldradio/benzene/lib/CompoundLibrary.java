package com.coldradio.benzene.lib;

import android.content.res.Resources;
import android.graphics.PointF;
import android.util.SparseArray;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;

public class CompoundLibrary {
    private SparseArray<Compound> mCompounds = new SparseArray<>();
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
            if (compound_json.isNonHydrogenBond(ii)) {
                compound.makeBond(compound_json.bonds.aid1[ii], compound_json.bonds.aid2[ii], compound_json.bondType(ii));
            }
        }

        // setup coordinates
        coords_JSON coord = compound_json.coords.get(0);

        for (int ii = 0; ii < coord.aid.length; ++ii) {
            if (compound_json.getAtomicNumber(coord.aid[ii]) != AtomicNumber.H) {
                conformer_JSON xy = coord.conformers.get(0);

                compound.getAtom(coord.aid[ii]).setPoint(new PointF(xy.x[ii], xy.y[ii]));
            }
        }

        CompoundArranger.autoArrange(compound);

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

                    mCompounds.put(compound_json.cid(), compoundFromStructure(compound_json));
                } catch (IllegalAccessException iae) {
                    // skip this resource
                }
            }
        }
    }

    public Compound getCompound(int cid) {
        return mCompounds.get(cid);
    }
}