package com.coldradio.benzene.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.Helper;

public class AddToBondActivity extends AppCompatActivity {
    private Preview mPreview;
    private EditText mEdgeNumber;
    private boolean mOppositeSite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_bond_main);

        Edge edge = Project.instance().getElementSelector().getSelectedEdge();

        if (edge == null) {
            finish();
            Helper.instance().notification("No Selected Edge");
            return;
        }

        // setting for Preview
        ViewGroup previewLayout = findViewById(R.id.a2b_view);

        if (previewLayout != null) {
            mPreview = new Preview(this);
            mPreview.setCenter(edge.center());
            previewLayout.addView(mPreview);
        }

        // setting for checkbox
        ((CheckBox)findViewById(R.id.a2b_cb_opposite_site)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mOppositeSite = isChecked;
            }
        });

        // setting for EditText; Edge Number
        mEdgeNumber = findViewById(R.id.a2b_et_edge_number);

        findViewById(R.id.a2b_btn_edge_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetEdgeNumber(-1);
            }
        });

        findViewById(R.id.a2b_btn_edge_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetEdgeNumber(1);
            }
        });

        findViewById(R.id.a2b_btn_tri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdgeNumber(3);
            }
        });

        findViewById(R.id.a2b_btn_tetra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdgeNumber(4);
            }
        });

        findViewById(R.id.a2b_btn_pent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdgeNumber(5);
            }
        });

        findViewById(R.id.a2b_btn_hex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdgeNumber(6);
            }
        });

        // setting for OK Cancel Button
        findViewById(R.id.a2b_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                int edgeNumber = Integer.parseInt(mEdgeNumber.getText().toString());

                if (edgeNumber >= 3) {
                    data.putExtra("EdgeNumber", edgeNumber);
                    data.putExtra("OppositeSite", mOppositeSite);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        findViewById(R.id.a2b_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setEdgeNumber(int edge) {
        if (mEdgeNumber != null) {
            mEdgeNumber.setText(String.valueOf(edge));
        }
    }

    private void offsetEdgeNumber(int offset) {
        if (mEdgeNumber != null) {
            int newVal = Integer.parseInt(mEdgeNumber.getText().toString()) + offset;

            if (newVal >= 3)
                mEdgeNumber.setText(String.valueOf(newVal));
        }
    }
}