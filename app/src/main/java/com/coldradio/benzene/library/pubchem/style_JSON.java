package com.coldradio.benzene.library.pubchem;

import com.coldradio.benzene.compound.Bond;

public class style_JSON {
    public int[] annotation;
    public int[] aid1;
    public int[] aid2;

    public Bond.BondAnnotation bondAnnotation(int pubChemAnnotation) {
        switch (pubChemAnnotation) {
            case 3: return Bond.BondAnnotation.WAVY;
            case 5: return Bond.BondAnnotation.WEDGE_UP;
            case 6: return Bond.BondAnnotation.WEDGE_DOWN;
            default: return Bond.BondAnnotation.NONE;
        }
    }
}
