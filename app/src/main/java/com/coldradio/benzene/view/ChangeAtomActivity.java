package com.coldradio.benzene.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.util.Helper;

public class ChangeAtomActivity extends AppCompatActivity {
    private void setTextViewOnClickListener(TableLayout tableLayout) {
        if (tableLayout == null) {
            return;
        }

        for (int ii = 0; ii < tableLayout.getChildCount(); ++ii) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(ii);

            for (int jj = 0; jj < tableRow.getChildCount(); ++jj) {
                final TextView textView = (TextView) tableRow.getChildAt(jj);

                if (textView != null && textView.getId() != View.NO_ID) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView selectedAtomTv = findViewById(R.id.selectedAtom);

                            selectedAtomTv.setText(textView.getText());
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_atom_main);

        // attach onClick handler to TextView
        setTextViewOnClickListener((TableLayout) findViewById(R.id.basic_element_table_layout));
        setTextViewOnClickListener((TableLayout) findViewById(R.id.transition_element_table_layout));
        setTextViewOnClickListener((TableLayout) findViewById(R.id.unknown_element_table_layout));

        // attach Listener to EditText
        EditText editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView selectedAtomTv = findViewById(R.id.selectedAtom);
                if (selectedAtomTv != null)
                    selectedAtomTv.setText(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // attack listener to buttons
        Button cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button okBtn = findViewById(R.id.okBtn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                TextView atomName = findViewById(R.id.selectedAtom);

                if (atomName != null && atomName.getText().length() > 0) {
                    data.putExtra("AtomName", atomName.getText());
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Helper.instance().notification("None Selected");
                }
            }
        });
    }
}