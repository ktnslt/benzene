package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.util.TreeTraveler;

public class AdjustDoubleBondTypeRule implements ICompoundRule {
    @Override
    public Compound apply(Compound compound) {
        CompoundArranger.adjustDoubleBondType(compound);

        TreeTraveler.returnFirstAtom(new TreeTraveler.IAtomVisitor() {
            @Override
            public boolean visit(Atom atom, Object... args) {
                if (atom.numberOfBonds() == 2 && CompoundInspector.allBondsAreDouble(atom)) {
                    atom.getAtomDecoration().setShowElementName(true);
                }
                return false;
            }
        }, compound);

        return compound;
    }
}
