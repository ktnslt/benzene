package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomCondition;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.library.rule.RuleSet;

public class CONH2_FG extends COOH_FG {
    public CONH2_FG(Atom a_atom) {
        super(a_atom);

        Atom o_with_h = CompoundInspector.findBoundAtom(super.appendAtom(), new AtomCondition().hasHydrogen());

        o_with_h.setAtomicNumber(AtomicNumber.N);
        super.getCompound().addAtom(o_with_h, Bond.BondType.SINGLE, new Atom(-1, AtomicNumber.H));
        CompoundArranger.adjustHydrogenPosition(o_with_h);

        RuleSet.instance().apply(getCompound());
    }

    @Override
    public String getName() {
        return "CONH2";
    }
}
