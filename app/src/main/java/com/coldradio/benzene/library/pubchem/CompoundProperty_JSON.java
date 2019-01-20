package com.coldradio.benzene.library.pubchem;

import java.util.List;

class CompoundProperty_JSON {
    class PropertyTable_JSON {
        List<Property_JSON> Properties;
    }
    class Property_JSON {
        int CID;
        String MolecularFormula;
        float MolecularWeight;
        String IUPACName;
        String Name;    // custom property not from JSON
    }
    PropertyTable_JSON PropertyTable;
}
