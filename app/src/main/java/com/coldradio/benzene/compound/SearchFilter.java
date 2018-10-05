package com.coldradio.benzene.compound;

import java.util.List;

public abstract class SearchFilter {
    protected String mKeyword;

    boolean subsetOf(SearchFilter filter) {
        // such as filter: a, this ab
        return mKeyword.contains(filter.mKeyword);
    }

    public String getKeyword() {
        return mKeyword;
    }

    abstract List<CompoundIndex> filtered(List<CompoundIndex> list);
}