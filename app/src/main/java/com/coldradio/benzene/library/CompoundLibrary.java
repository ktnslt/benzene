package com.coldradio.benzene.library;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.android.volley.Response;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.local.LocalSearch;
import com.coldradio.benzene.library.pubchem.PubChemSearch;
import com.coldradio.benzene.project.preference.OnPreferenceEvent;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;

import java.util.ArrayList;
import java.util.List;

public class CompoundLibrary {
    private List<ICompoundSearch> mCompoundSearchers = new ArrayList<>();
    private List<CompoundIndex> mSearchResults = new ArrayList<>();
    private static CompoundLibrary smInstance = new CompoundLibrary();
    private OnSearchResultArrived mSearchResultListener;
    private int mTotalSearchedCompounds;
    private int mPropertySuccessCompounds;
    private int mPropertyFailedCompounds;
    private String mSearchKeyword;
    private SearchHistory mSearchHistory = new SearchHistory();
    private int mSearchID = 0;
    // TODO not sure about below maybe there is something better
    private TextView mCompoundSearchProgressBar;

    private CompoundLibrary() {
        mCompoundSearchers.add(new LocalSearch());
        mCompoundSearchers.add(new PubChemSearch());
        AppEnv.instance().addPreferenceListener(new OnPreferenceEvent() {
            @Override
            public void onRestore(SharedPreferences sharedPreferences) {
                mSearchHistory.onRestore(sharedPreferences);
            }

            @Override
            public void onSave(SharedPreferences.Editor editor) {
                mSearchHistory.onSave(editor);
            }
        });
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

    private Spanned finishMark(String progressText) {
        if (mTotalSearchedCompounds != 0 && mPropertyFailedCompounds + mPropertySuccessCompounds == mTotalSearchedCompounds) {
            return Html.fromHtml("<u>" + progressText + "</u>");
        } else {
            return Html.fromHtml(progressText);
        }
    }

    private void updateProgressBar() {
        if (mCompoundSearchProgressBar != null) {
            mCompoundSearchProgressBar.setText(finishMark(mPropertySuccessCompounds + "/" + mTotalSearchedCompounds));
        }
    }

    private void propertyRetrieved(boolean success) {
        if (success) {
            mPropertySuccessCompounds++;
        } else {
            mPropertyFailedCompounds++;
        }
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

    public void search(String keyword, String pushKeyword) {
        clearAll();
        mSearchKeyword = keyword;
        mSearchID++;

        for (ICompoundSearch search : mCompoundSearchers) {
            List<CompoundIndex> results = search.search(mSearchID, ICompoundSearch.KeywordType.TEXT, mSearchKeyword);

            if (results != null) {
                // in case that results are ready immediately
                int posStart = mSearchResults.size();

                mSearchResults.addAll(results);
                notifySearchResultListener(results, posStart, results.size());
            }
        }
        mSearchHistory.add(pushKeyword != null ? pushKeyword : mSearchKeyword);
    }

    public String[] getSearchHistory() {
        return mSearchHistory.getSearchHistory();
    }

    public void setSearchResultReadyListener(OnSearchResultArrived listener) {
        mSearchResultListener = listener;
    }

    public void arrived(CompoundIndex compoundIndex) {
        if (compoundIndex != null && compoundIndex.searchID == mSearchID) {
            // search -> search with new keyword in the middle of the previous search.
            // in this case, the previous results are still coming. keyword comparison is necessary
            if (compoundIndex.cid >= 0) {
                mSearchResults.add(compoundIndex);
                notifySearchResultListener(compoundIndex, mSearchResults.size() - 1);
                propertyRetrieved(true);
            } else {
                propertyRetrieved(false);
            }
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
        if (position >= 0 && position < mSearchResults.size()) {
            mSearchResults.get(position).requestCompound(listener);
        } else {
            Notifier.instance().notification("Wait. List under construction");
        }
    }

    public void requestDescription(final int position, final Response.Listener<Spanned> listener) {
        if (position >= 0 && position < mSearchResults.size()) {
            CompoundIndex index = mSearchResults.get(position);

            if (index.description != null && index.description.length() > 0 && index.description.charAt(0) != 'E') { // error starts with E
                listener.onResponse(index.description);
            } else {
                mSearchResults.get(position).requestDescription(new Response.Listener<Spanned>() {
                    @Override
                    public void onResponse(Spanned response) {
                        mSearchResults.get(position).description = response;
                        listener.onResponse(response);
                    }
                });
            }
        } else {
            Notifier.instance().notification("Wait. List under construction");
        }
    }

    public void clearAll() {
        mSearchResults.clear();
        mTotalSearchedCompounds = 0;
        mPropertySuccessCompounds = 0;
        mPropertyFailedCompounds = 0;
        AppEnv.instance().cancelAllNetworkRequest();
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

    public String getSearchKeyword() {
        return mSearchKeyword;
    }
}
