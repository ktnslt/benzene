package com.coldradio.benzene.compound;

import com.coldradio.benzene.lib.AtomicNumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompoundStructure_JSON {
    public List<PC_Compound_JSON> PC_Compounds = new ArrayList<>();
}

class PC_Compound_JSON {
    public ID_JSON id;
    public atoms_JSON atoms;
    public bonds_JSON bonds;
    public List<coords_JSON> coords;
    public List<props_JSON> props;

    public int cid() {
        return id.id.cid;
    }

    public AtomicNumber getAtomicNumber(int aid) {
        return AtomicNumber.values()[atoms.element[aid - 1]];
    }

    public int bondLength() {
        return bonds.aid1.length;
    }

    public Bond.BondType bondType(int bondIndex) {
        // 1 --> SINGLE, 2 --> DOUBLE, 3 --> TRIPLE
        return Bond.BondType.values()[bonds.order[bondIndex]];
    }

    public String preferredIUPACName() {
        for (props_JSON prop : props) {
            if (prop.urn.label != null && prop.urn.label.equals("IUPAC Name")
                    && prop.urn.name != null && prop.urn.name.equals("Preferred")) {
                return prop.value.sval;
            }
        }
        return null;
    }

    public String otherNames() {
        StringBuilder names = new StringBuilder();
        Set<String> nameSet = new HashSet<>();

        for (props_JSON prop : props) {
            if (prop.urn.label != null && prop.urn.label.equals("IUPAC Name") || prop.urn.label.equals("InChI")
                    || prop.urn.label.equals("Molecular Formula") || prop.urn.label.equals("SMILES")) {
                nameSet.add(prop.value.sval);
            }
        }

        for (String name : nameSet) {
            names.append(name);
            names.append(", ");
        }
        names.delete(names.length() - 2, names.length());

        return names.toString();
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

class props_JSON {
    public urn_JSON urn;
    public value_JSON value;
}

class urn_JSON {
    String label;
    String name;
}

class value_JSON {
    String sval;
}