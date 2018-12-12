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
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.funcgroup.*;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.util.Helper;
import com.coldradio.benzene.view.drawer.GenericDrawer;
import com.coldradio.benzene.view.drawer.PaintSet;

public class AddToAtomActivity extends AppCompatActivity {
    private IFunctionalGroup mFuncGroup;
    private TextView mFuncGroupName;
    private AddToAtomPreview mPreview;
    private boolean mDeleteHOfSelectedAtom = true;

    private void setFuncGroupNameAndPreview() {
        // assume the mFuncGroup is already assigned
        mFuncGroupName.setText(mFuncGroup.getName());
        mPreview.setFunctionalGroup(mFuncGroup);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_atom_main);

        final Atom attachAtom = Project.instance().getElementSelector().getSelectedAtom();
        mFuncGroupName = findViewById(R.id.a2a_tv_func_group_name);

        if (attachAtom == null) {
            finish();
            Helper.instance().notification("No Atom Selected");
            return;
        }

        // setting for Preview
        ViewGroup previewLayout = findViewById(R.id.a2a_view);

        if (previewLayout != null) {
            mPreview = new AddToAtomPreview(this);
            mPreview.setCenter(attachAtom.getPoint());
            previewLayout.addView(mPreview);
        }

        // setting for prev next button
        findViewById(R.id.a2a_btn_attach_form_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuncGroup != null) {
                    mFuncGroup.prevForm();
                    mPreview.invalidate();
                } else {
                    Helper.instance().notification("Select Functional Group First");
                }
            }
        });

        // setting for checkbox delete H of selected Atom
        ((CheckBox)findViewById(R.id.a2a_cb_delete_H)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDeleteHOfSelectedAtom = isChecked;
            }
        });

        findViewById(R.id.a2a_btn_attach_form_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuncGroup != null) {
                    mFuncGroup.nextForm();
                    mPreview.invalidate();
                } else {
                    Helper.instance().notification("Select Functional Group First");
                }
            }
        });

        // setting for ok, cancel button
        findViewById(R.id.a2a_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Project.instance().getElementSelector().getSelectedCompound().addFunctionalGroupToAtom(mFuncGroup, attachAtom, mDeleteHOfSelectedAtom);
                finish();
            }
        });

        findViewById(R.id.a2a_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // setting for buttons - alkane, alkene, alkyne
        findViewById(R.id.a2a_btn_c1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new Methyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_c2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new Ethyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_c3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new Propyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_iso_c3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new IsoPropyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_c4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new Butyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_iso_c4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new IsoButyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_sec_c4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new SecButyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_tert_c4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new TertButyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });
        // settings buttons - cyclic
        findViewById(R.id.a2a_btn_cyc_c5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new CycPentyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_conj_c5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new ConjCycPentyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_cyc_c6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new CycHexyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });

        findViewById(R.id.a2a_btn_phe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFuncGroup = new Phenyl_FG(attachAtom);
                setFuncGroupNameAndPreview();
            }
        });
        // settings for buttons - oxygen
        findViewById(R.id.a2a_btn_oh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

class AddToAtomPreview extends Preview {
    private IFunctionalGroup mFunctionalGroup;

    public AddToAtomPreview(Context context) {
        super(context);
    }

    public void setFunctionalGroup(IFunctionalGroup funcGroup) {
        mFunctionalGroup = funcGroup;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mFunctionalGroup != null) {
            Paint paint = PaintSet.instance().paint(PaintSet.PaintType.GENERAL);
            PointF selectedAtomPoint = getCenter();
            PointF attachPointInFuncGroup = mFunctionalGroup.appendAtom().getPoint();

            int color = paint.getColor();
            paint.setColor(Color.BLUE);
            canvas.drawLine(selectedAtomPoint.x, selectedAtomPoint.y, attachPointInFuncGroup.x, attachPointInFuncGroup.y, paint);
            GenericDrawer.draw(mFunctionalGroup.curForm(), canvas, paint);
            paint.setColor(color);
        }
    }
}