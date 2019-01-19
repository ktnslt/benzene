package com.coldradio.benzene.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.coldradio.benzene.R;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.util.StringSearchFilter;

public class CompoundSearchActivity extends AppCompatActivity implements TextWatcher {
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
        EditText editText = findViewById(R.id.compound_search_edittext);
        if (editText != null) {
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            CompoundLibrary.instance().resetSearchFilter();
        } else {
            CompoundLibrary.instance().setSearchFilter(new StringSearchFilter<CompoundIndex>(s.toString()));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onBackPressed() {
        CompoundLibrary.instance().resetSearchFilter();
        super.onBackPressed();
    }
}