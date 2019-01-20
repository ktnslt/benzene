package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;

public class LetteringIfNotCarbonRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        // All Carbons shall not show element name, and hide H bond
        for (Atom atom : compound.getAtoms()) {
            AtomicNumber an = atom.getAtomicNumber();

            if (an == AtomicNumber.C) {
                atom.getAtomDecoration().setShowElementName(false);
            } else if (an != AtomicNumber.H) {
                atom.getAtomDecoration().lettering(true);
            }
            CompoundArranger.showAllHydrogen(atom, false);
        }
        return compound;
    }
}
