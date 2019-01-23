package com.coldradio.benzene.library;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.android.volley.Response;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.local.LocalSearch;
import com.coldradio.benzene.library.pubchem.PubChemSearch;

import java.util.ArrayList;
import java.util.List;

public class CompoundLibrary {
    private List<ICompoundSearch> mCompoundSearchers = new ArrayList<>();
    private List<CompoundIndex> mSearchResults = new ArrayList<>();
    private static CompoundLibrary smInstance = new CompoundLibrary();
    private OnSearchResultArrived mSearchResultListener;
    private int mTotalSearchedCompounds;
    private int mPropertyRetrievedCompounds;
    private String mSearchKeyword;
    // TODO not sure about below maybe there is something better
    private TextView mCompoundSearchProgressBar;

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

    private void updateProgressBar() {
        if (mCompoundSearchProgressBar != null) {
            mCompoundSearchProgressBar.setText(mPropertyRetrievedCompounds + "/" + mTotalSearchedCompounds);
        }
    }

    private void increasePropertyRetrievedCompounds() {
        mPropertyRetrievedCompounds++;
        updateProgressBar();
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
        clearAll();

        mSearchKeyword = keyword;

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
        if (compoundIndex != null && compoundIndex.searchKeyword.equals(mSearchKeyword)) {
            // search -> search with new keyword in the middle of the previous search.
            // in this case, the previous results are still coming. keyword comparison is necessary
            mSearchResults.add(compoundIndex);
            notifySearchResultListener(compoundIndex, mSearchResults.size() - 1);
            increasePropertyRetrievedCompounds();
        }
    }

    public void arrived(int cid, Bitmap bitmap) {
        for (int ii = 0; ii < mSearchResults.size(); ii++) {
            CompoundIndex index = mSearchResults.get(ii);

            if (index.cid == cid) {
                index.setBitmap(bitmap);
                notifySearchResultListener(ii);
                // don't break here. multiple compounds with the same CID is possible
            }
        }
    }

    public void requestCompound(int position, Response.Listener<List<Compound>> listener) {
        mSearchResults.get(position).requestCompound(listener);
    }

    public void clearAll() {
        mSearchResults.clear();
        mTotalSearchedCompounds = 0;
        mPropertyRetrievedCompounds = 0;
        updateProgressBar();
    }

    public void setTotalSearchedCompounds(int totalSearchedCompounds) {
        mTotalSearchedCompounds = totalSearchedCompounds;
        updateProgressBar();

    }

    public void setProgressBarForCompoundSearch(TextView progressBar) {
        mCompoundSearchProgressBar = progressBar;
        updateProgressBar();
    }
}
