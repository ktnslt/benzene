package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;

public class BasicRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        // methane case
        Atom carbon = CompoundInspector.returnCarbonIfC1Hn(compound);

        if (carbon != null) {
            carbon.lettering(true);
            return compound;
        }
        // All Carbons shall not show element name, and hide H bond
        for (Atom atom : compound.getAtoms()) {
            AtomicNumber an = atom.getAtomicNumber();

            if (an == AtomicNumber.C) {
                atom.getAtomDecoration().setShowElementName(false);
                CompoundArranger.showAllHydrogen(atom, false);
            } else if (an != AtomicNumber.H && CompoundInspector.numberOfHydrogen(atom) > 0) {
                atom.lettering(true);
            }
        }
        return compound;
    }
}
