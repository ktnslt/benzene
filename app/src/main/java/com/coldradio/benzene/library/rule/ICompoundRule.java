package com.coldradio.benzene.library.rule;

import com.coldradio.benzene.compound.Compound;

public interface ICompoundRule {
    Compound apply(Compound compound);
}
