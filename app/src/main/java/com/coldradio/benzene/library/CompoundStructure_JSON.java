package com.coldradio.benzene.library;

import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;

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
        if (bonds.order[bondIndex] == 2) {
            return Bond.BondType.DOUBLE;
        } else if (bonds.order[bondIndex] == 3) {
            return Bond.BondType.TRIPLE;
        } else {
            return Bond.BondType.SINGLE;
        }
    }

    public String preferredIUPACName() {
        for (props_JSON prop : props) {
            if (prop.urn.label != null && prop.urn.label.equals("IUPAC Name")
                    && prop.urn.name != null && prop.urn.name.equals("Preferred")) {
                return prop.value.sval;
            }
        }
        return "";
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

    public style_JSON getStyle() {
        return coords.get(0).conformers.get(0).style;
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
    public style_JSON style;
}

class style_JSON {
    public int[] annotation;
    public int[] aid1;
    public int[] aid2;

    Bond.BondAnnotation bondAnnotation(int pubChemAnnotation) {
        switch (pubChemAnnotation) {
            case 3: return Bond.BondAnnotation.WAVY;
            case 5: return Bond.BondAnnotation.WEDGE_UP;
            case 6: return Bond.BondAnnotation.WEDGE_DOWN;
            default: return Bond.BondAnnotation.NONE;
        }
    }
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