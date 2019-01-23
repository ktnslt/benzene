package com.coldradio.benzene.library.pubchem;

import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PC_Compound_JSON {
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
        if (bonds != null && bonds.aid1 != null) {
            return bonds.aid1.length;
        } else {
            return 0;
        }
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