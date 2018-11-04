package com.coldradio.benzene.library;

import java.util.ArrayList;
import java.util.List;

public class StringSearchFilter extends SearchFilter {
    public StringSearchFilter(String keyword) {
        mKeyword = keyword;
    }
    @Override
    public List<CompoundIndex> filtered(List<CompoundIndex> list) {
        List<CompoundIndex> filteredList = new ArrayList<>();

        for (CompoundIndex index : list) {
            if (index.preferredIUPACName.contains(mKeyword) || index.otherNames.contains(mKeyword)) {
                filteredList.add(index);
            }
        }

        return filteredList;
    }
}
