package com.coldradio.benzene.library;

import java.util.ArrayList;
import java.util.List;

public class CompoundLibrary implements OnSearchResultArrived {
    private List<ICompoundSearch> mCompoundSearchers = new ArrayList<>();
    private List<CompoundIndex> mSearchResults = new ArrayList<>();
    private static CompoundLibrary smInstance = new CompoundLibrary();
    private OnSearchResultArrived mSearchResultListener;

    private CompoundLibrary() {
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

    public void addCompoundSearch(ICompoundSearch compoundSearch) {
        if (!mCompoundSearchers.contains(compoundSearch)) {
            mCompoundSearchers.add(compoundSearch);
        }
    }

    public void setSearchResultReadyListener(OnSearchResultArrived listener) {
        mSearchResultListener = listener;
    }

    @Override
    public void arrived(List<CompoundIndex> compoundIndexList, int posStart, int itemCount) {

    }

    @Override
    public void arrived(CompoundIndex compoundIndex, int position) {
        if (compoundIndex != null) {
            mSearchResults.add(compoundIndex);
            notifySearchResultListener(compoundIndex, mSearchResults.size() - 1);
        }
    }
}
