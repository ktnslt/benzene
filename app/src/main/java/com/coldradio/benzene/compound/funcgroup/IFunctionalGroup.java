package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;

public interface IFunctionalGroup {
    Compound nextForm();
    Compound prevForm();
    Compound curForm();
    String getName();
    Atom attachAtom();
}
