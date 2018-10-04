package com.coldradio.benzene.compound;

public class CompoundIndex {
    final public String preferredIUPACName;
    final public String otherNames;
    final public Compound compound;

    public CompoundIndex(String iupacName, String names, Compound c) {
        preferredIUPACName = iupacName;
        otherNames = names;
        compound = c;
    }
}