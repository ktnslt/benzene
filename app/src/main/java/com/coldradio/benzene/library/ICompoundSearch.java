package com.coldradio.benzene.library;

import java.util.List;

public interface ICompoundSearch {
    enum KeywordType {
        TEXT, SMILES, CID
    }

    List<CompoundIndex> search(KeywordType keywordType, String keyword);
}
