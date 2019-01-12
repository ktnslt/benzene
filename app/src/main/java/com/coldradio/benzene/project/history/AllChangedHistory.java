package com.coldradio.benzene.project.history;

import com.coldradio.benzene.compound.Compound;

import java.util.List;

public class AllChangedHistory extends History {
    public final List<Compound> projectCompounds;

    public AllChangedHistory(List<Compound> compoundList) {
        super(-1);
        projectCompounds = compoundList;
    }
}
