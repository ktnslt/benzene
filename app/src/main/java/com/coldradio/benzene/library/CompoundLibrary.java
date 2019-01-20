package com.coldradio.benzene.library;

import android.graphics.Bitmap;

import com.coldradio.benzene.library.local.LocalSearch;
import com.coldradio.benzene.library.pubchem.PubChemSearch;

import java.util.ArrayList;
import java.util.List;

public class CompoundLibrary {
    private List<ICompoundSearch> mCompoundSearchers = new ArrayList<>();
    private List<CompoundIndex> mSearchResults = new ArrayList<>();
    private static CompoundLibrary smInstance = new CompoundLibrary();
    private OnSearchResultArrived mSearchResultListener;

    private CompoundLibrary() {
        mCompoundSearchers.add(new LocalSearch());
        mCompoundSearchers.add(new PubChemSearch());
    }

    private void notifySearchResultListener(List<CompoundIndex> compoundIndexList, int posStart, int itemCount) {
        if (mSearchResultListener != null) {
            mSearchResultListener.arrived(compoundIndexList, posStart, itemCount);
        }
    }

    private void notifySearchResultListener(CompoundIndex compoundIndex, int position) {
        if (mSearchResultListener != null) {
            mSearchResultListener.arrived(compoundIndex, position);
        }
    }

    private void notifySearchResultListener(int position) {
        if (mSearchResultListener != null) {
            mSearchResultListener.updated(position);
        }
    }

    public static CompoundLibrary instance() {
        return smInstance;
    }

    public CompoundIndex getCompoundIndex(int index) {
        if (index >= 0 && index < mSearchResults.size()) {
            return mSearchResults.get(index);
        }
        return null;
    }

    public int size() {
        return mSearchResults.size();
    }

    public void search(String keyword) {
        mSearchResults.clear();

        for (ICompoundSearch search : mCompoundSearchers) {
            List<CompoundIndex> results = search.search(ICompoundSearch.KeywordType.TEXT, keyword);

            if (results != null) {
                // in case that results are ready immediately
                int posStart = mSearchResults.size();

                mSearchResults.addAll(results);
                notifySearchResultListener(results, posStart, results.size());
            }
        }
    }

    public void setSearchResultReadyListener(OnSearchResultArrived listener) {
        mSearchResultListener = listener;
    }

    public void arrived(CompoundIndex compoundIndex) {
        if (compoundIndex != null) {
            mSearchResults.add(compoundIndex);
            notifySearchResultListener(compoundIndex, mSearchResults.size() - 1);
        }
    }

    public void arrived(int cid, Bitmap bitmap) {
        for (int ii = 0; ii < mSearchResults.size(); ii++) {
            CompoundIndex index = mSearchResults.get(ii);

            if (index.cid == cid) {
                index.setBitmap(bitmap);
                notifySearchResultListener(ii);
                break;
            }
        }
    }
}
