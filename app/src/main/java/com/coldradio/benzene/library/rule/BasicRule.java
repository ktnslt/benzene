package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundInspector;

public class BasicRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        // methane case
        Atom carbon = CompoundInspector.returnCarbonIfC1Hn(compound);

        if (carbon != null) {
            carbon.setHydrogenMode(Atom.HydrogenMode.LETTERING_H);
            return compound;
        }
        // All Carbons shall not show element name, and hide H bond
        for (Atom atom : compound.getAtoms()) {
            AtomicNumber an = atom.getAtomicNumber();

            if (an == AtomicNumber.C) {
                atom.getAtomDecoration().setShowElementName(false);
                atom.setHydrogenMode(Atom.HydrogenMode.HIDE_H_BOND);
            } else if (an == AtomicNumber.H) {
                atom.getAtomDecoration().setShowElementName(false);
            }
        }
        return compound;
    }
}
