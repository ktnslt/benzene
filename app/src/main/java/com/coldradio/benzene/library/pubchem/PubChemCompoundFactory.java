package com.coldradio.benzene.library.pubchem;

import android.graphics.PointF;

import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;

public class PubChemCompoundFactory {
    private static AtomicNumber[] toAtomicNumber(int[] atomicNumberInts) {
        AtomicNumber[] atomicNumberEnums = new AtomicNumber[atomicNumberInts.length];

        for (int ii = 0; ii < atomicNumberEnums.length; ++ii) {
            atomicNumberEnums[ii] = AtomicNumber.values()[atomicNumberInts[ii]];
        }

        return atomicNumberEnums;
    }

    public static Compound create(PC_Compound_JSON compound_json) {
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
}
