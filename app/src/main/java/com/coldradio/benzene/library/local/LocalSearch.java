package com.coldradio.benzene.library.local;

import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.ICompoundSearch;

import java.util.List;

public class LocalSearch implements ICompoundSearch {
    @Override
    public List<CompoundIndex> search(KeywordType keywordType, String keyword) {
        return LocalCompounds.instance().search(keywordType, keyword);
    }
}
