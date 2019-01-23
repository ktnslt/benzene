package com.coldradio.benzene.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.library.OnSearchResultArrived;
import com.coldradio.benzene.util.Notifier;

import java.util.List;

public class CompoundSearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compound_search_main);
        RecyclerView recyclerView = findViewById(R.id.compound_search_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an mAdapter (see also next example)
        mAdapter = new CompoundSearchAdapter();
        recyclerView.setAdapter(mAdapter);

        // set listener for EditText
        EditText editText = findViewById(R.id.actv_compound_search);
        if (editText != null) {
            editText.setOnEditorActionListener(this);
        }

        CompoundLibrary.instance().setProgressBarForCompoundSearch((TextView) findViewById(R.id.tv_progress));

        CompoundLibrary.instance().setSearchResultReadyListener(new OnSearchResultArrived() {
            @Override
            public void arrived(List<CompoundIndex> compoundIndexList, int posStart, int itemCount) {
                mAdapter.notifyItemRangeInserted(posStart, itemCount);
            }

            @Override
            public void arrived(CompoundIndex compoundIndex, int position) {
                //mAdapter.notifyItemInserted(position);    // this doesn't work
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void updated(int position) {
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String keyword = v.getText().toString();

        if (!keyword.isEmpty()) {
            // clear the view. this cannot be done in CompoundLibrary.search() since it doesn't have reference to mAdapter
            CompoundLibrary.instance().clearAll();
            mAdapter.notifyDataSetChanged();

            CompoundLibrary.instance().search(keyword);
        } else {
            Notifier.instance().notification("No Search Keywords");
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CompoundLibrary.instance().setProgressBarForCompoundSearch(null);
    }
}