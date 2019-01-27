package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Edge;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.Notifier;
import com.coldradio.benzene.view.drawer.PaintSet;

public class AddToBondActivity extends AppCompatActivity {
    private AddToBondPreview mPreview;
    private EditText mEdgeNumber;
    private boolean mOppositeSite;
    private boolean mDeleteHydrogenBeforeAdd = true;
    private boolean mSaturateWithHydrogen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_bond_main);

        Edge edge = Project.instance().getElementSelector().getSelectedEdge();

        if (edge == null) {
            finish();
            Notifier.instance().notification("No Selected Edge");
            return;
        }

        // setting for Preview
        ViewGroup previewLayout = findViewById(R.id.a2b_view);

        if (previewLayout != null) {
            mPreview = new AddToBondPreview(this, edge);
            previewLayout.addView(mPreview);
        }

        // setting for checkbox
        ((CheckBox)findViewById(R.id.a2b_cb_opposite_site)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mOppositeSite = isChecked;
                mPreview.setOppositeSite(mOppositeSite);
                mPreview.invalidate();
            }
        });
        ((CheckBox)findViewById(R.id.a2b_cb_delete_h_before_add)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDeleteHydrogenBeforeAdd = isChecked;
            }
        });
        ((CheckBox)findViewById(R.id.a2b_cb_saturate_h)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSaturateWithHydrogen = isChecked;
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
                int edgeNumber = Integer.parseInt(mEdgeNumber.getText().toString());

                if (edgeNumber >= 3) {
                    ProjectFileManager.instance().pushCompoundChangedHistory(Project.instance().getElementSelector().getSelectedCompound());
                    Project.instance().addCyclicToSelectedBond(edgeNumber, mOppositeSite, mDeleteHydrogenBeforeAdd, mSaturateWithHydrogen);
                }
                finish();
            }
        });

        findViewById(R.id.a2b_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEnv.instance().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEnv.instance().setCurrentActivity(null);
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

class AddToBondPreview extends Preview {
    private boolean mOppositeSite;
    private Edge mSelectedEdge;
    private PointF mAddSite;

    public AddToBondPreview(Context context, Edge edge) {
        super(context);

        PointF center = edge.center();

        setCenter(center);
        mSelectedEdge = edge;
        setOppositeSite(mOppositeSite);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = PaintSet.instance().paint(PaintSet.PaintType.GENERAL);

        int color = paint.getColor();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(mAddSite.x, mAddSite.y, 20, paint);
        paint.setColor(color);
    }

    public void setOppositeSite(boolean oppositeSite) {
        mOppositeSite = oppositeSite;

        Atom centerAtom = mSelectedEdge.atomInUpperDirection();
        Atom rotatingAtom = mSelectedEdge.first == centerAtom ? mSelectedEdge.second : mSelectedEdge.first;
        mAddSite = Geometry.cwRotate(rotatingAtom.getPoint(), centerAtom.getPoint(), (float)Math.toRadians(60) * (mOppositeSite ? -1 : 1));
    }
}