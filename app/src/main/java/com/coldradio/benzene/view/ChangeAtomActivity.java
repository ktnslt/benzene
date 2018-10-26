package com.coldradio.benzene.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.lib.Helper;

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
                    data.setData(Uri.parse(atomName.getText().toString()));
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Helper.instance().notification("None Selected");
                }
            }
        });
    }
}