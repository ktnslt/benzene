package com.coldradio.benzene.lib;

import com.coldradio.benzene.compound.Bond;

import java.util.ArrayList;
import java.util.List;

public class CompoundStructure_JSON {
    public List<PC_Compound_JSON> PC_Compounds = new ArrayList<>();
}

class PC_Compound_JSON {
    public ID_JSON id;
    public atoms_JSON atoms;
    public bonds_JSON bonds;
    public List<coords_JSON> coords;

    public int cid() {
        return id.id.cid;
    }

    public AtomicNumber getAtomicNumber(int aid) {
        return AtomicNumber.values()[atoms.element[aid-1]];
    }

    public int bondLength() {
        return bonds.aid1.length;
    }

    public boolean isNonHydrogenBond(int bondIndex) {
        return getAtomicNumber(bonds.aid1[bondIndex]) != AtomicNumber.H && getAtomicNumber(bonds.aid2[bondIndex]) != AtomicNumber.H;
    }

    public Bond.BondType bondType(int bondIndex) {
        // 1 --> SINGLE, 2 --> DOUBLE, 3 --> TRIPLE
        return Bond.BondType.values()[bonds.order[bondIndex]];
    }
}

class ID_JSON {
    public SID_JSON id;
}

class SID_JSON {
    public int cid;
}

class atoms_JSON {
    public int[] aid;
    public int[] element;
}

class bonds_JSON {
    public int[] aid1;
    public int[] aid2;
    public int[] order;
}

class coords_JSON {
    public int[] aid;
    public List<conformer_JSON> conformers;
}

class conformer_JSON {
    public float[] x;
    public float[] y;
}