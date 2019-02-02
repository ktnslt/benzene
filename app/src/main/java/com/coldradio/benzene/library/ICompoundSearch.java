package com.coldradio.benzene.library;

import java.util.List;

public interface ICompoundSearch {
    enum KeywordType {
        TEXT, SMILES, CID
    }

    List<CompoundIndex> search(int searchID, KeywordType keywordType, String keyword);
}
