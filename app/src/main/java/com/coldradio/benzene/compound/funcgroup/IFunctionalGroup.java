package com.coldradio.benzene.compound.funcgroup;

import com.coldradio.benzene.compound.Compound;

public interface IFunctionalGroup {
    Compound nextForm();
    Compound prevForm();
    String getName();
}
