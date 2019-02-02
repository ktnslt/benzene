package com.coldradio.benzene.library;

import java.util.List;

public interface OnSearchResultArrived {
    void arrived(List<CompoundIndex> compoundIndexList, int posStart, int itemCount);
    void arrived(CompoundIndex compoundIndex, int position);
    void updated(int position);
}
