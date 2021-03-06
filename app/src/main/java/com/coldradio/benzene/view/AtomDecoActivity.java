package com.coldradio.benzene.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomDecoration;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.project.ElementSelector;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;

public class AtomDecoActivity extends AppCompatActivity {
    private Atom mSelectedAtom;
    private AtomDecoration mSelectedAtomDecoration;
    private Preview mAtomDecoView;
    private EditText mChargeET;
    private SeekBar mChargeAsCircleSeekBox;
    private Compound mOrigCompound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_deco_main);

        ElementSelector elementSelector = Project.instance().getElementSelector();

        mSelectedAtom = elementSelector.getSelectedAtom();

        if (mSelectedAtom == null) {
            finish();
            Notifier.instance().notification("No Selected Atom");
            return;
        }
        mSelectedAtomDecoration = mSelectedAtom.getAtomDecoration();
        // save the originals. when copied, the AID is reset and synchronized between compounds
        mOrigCompound = elementSelector.getSelectedCompound().copy();

        setInitialAtomConfiguration();

        // setting for AtomDecoView
        ViewGroup preview = findViewById(R.id.atom_deco_view);

        if (preview != null) {
            mAtomDecoView = new Preview(this);
            mAtomDecoView.setCenter(mSelectedAtom.getPoint());
            preview.addView(mAtomDecoView);
        }

        // setting for charge
        mChargeET = findViewById(R.id.atom_deco_et_charge);

        mChargeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charge = Integer.parseInt(s.toString());

                mSelectedAtomDecoration.setCharge(charge);
                mChargeAsCircleSeekBox.setEnabled(charge * charge == 1);

                mAtomDecoView.invalidate();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.atom_deco_btn_charge_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetCharge(-1);
            }
        });

        findViewById(R.id.atom_deco_btn_charge_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetCharge(1);
            }
        });

        mChargeAsCircleSeekBox.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedAtomDecoration.setChargeAsCircle(AtomDecoration.Marker.values()[progress]);
                mAtomDecoView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // setting for marker
        ((SeekBar)findViewById(R.id.atom_deco_sb_starmark)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedAtomDecoration.setMarker(AtomDecoration.Marker.values()[progress]);
                mAtomDecoView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // setting for checkboxs
        ((CheckBox)findViewById(R.id.atom_deco_cb_show_element)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSelectedAtomDecoration.setShowElementName(isChecked);
                mAtomDecoView.invalidate();
            }
        });
        ((CheckBox)findViewById(R.id.atom_deco_cb_lettering)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSelectedAtom.getAtomDecoration().lettering(isChecked);
                mAtomDecoView.invalidate();
            }
        });
        ((CheckBox)findViewById(R.id.atom_deco_cb_show_h)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CompoundArranger.showAllHydrogen(mSelectedAtom, isChecked);
                mAtomDecoView.invalidate();
            }
        });
        ((CheckBox)findViewById(R.id.atom_deco_cb_change_atom)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.atom_deco_btn_select_element).setEnabled(isChecked);
                mAtomDecoView.invalidate();
            }
        });

        // setting for Select Element
        findViewById(R.id.atom_deco_btn_select_element).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent("com.coldradio.benzene.CHANGE_ATOM"), ActivityRequestCode.SELECT_ELEMENT_REQ.ordinal());
            }
        });

        // setting for OK Cancel Button
        findViewById(R.id.atom_deco_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectFileManager.instance().pushCompoundChangedHistory(mOrigCompound);
                finish();
            }
        });

        findViewById(R.id.atom_deco_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int aid = mSelectedAtom.getAID();

                Project.instance().replaceCompound(mOrigCompound.getID(), mOrigCompound);
                // reselect the Atom since replacement will mis-point the selected atom
                Project.instance().getElementSelector().selectAtom(mOrigCompound, mOrigCompound.getAtom(aid));
                finish();
            }
        });

        // setting for unshared electron
        findViewById(R.id.atom_deco_btn_up_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.TOP, AtomDecoration.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_up_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.TOP, AtomDecoration.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_up_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.TOP, AtomDecoration.UnsharedElectron.DOUBLE);
            }
        });

        findViewById(R.id.atom_deco_btn_down_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.BOTTOM, AtomDecoration.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_down_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.BOTTOM, AtomDecoration.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_down_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.BOTTOM, AtomDecoration.UnsharedElectron.DOUBLE);
            }
        });

        findViewById(R.id.atom_deco_btn_left_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.LEFT, AtomDecoration.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_left_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.LEFT, AtomDecoration.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_left_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.LEFT, AtomDecoration.UnsharedElectron.DOUBLE);
            }
        });

        findViewById(R.id.atom_deco_btn_right_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.RIGHT, AtomDecoration.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_right_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.RIGHT, AtomDecoration.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_right_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(AtomDecoration.Direction.RIGHT, AtomDecoration.UnsharedElectron.DOUBLE);
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

    private void offsetCharge(int offset) {
        if (mChargeET != null) {
            mChargeET.setText(String.valueOf(Integer.parseInt(mChargeET.getText().toString()) + offset));
        }
    }

    private void unsharedElectron(AtomDecoration.Direction direction, AtomDecoration.UnsharedElectron unsharedElectron) {
        mSelectedAtomDecoration.setUnsharedElectron(direction, unsharedElectron);
        mAtomDecoView.invalidate();
    }

    private void setInitialAtomConfiguration() {
        // set the default values
        ((CheckBox)findViewById(R.id.atom_deco_cb_show_element)).setChecked(mSelectedAtomDecoration.getShowElementName());
        ((CheckBox)findViewById(R.id.atom_deco_cb_lettering)).setChecked(mSelectedAtomDecoration.isLettering());
        ((CheckBox)findViewById(R.id.atom_deco_cb_show_h)).setChecked(CompoundInspector.showAnyHydrogen(mSelectedAtom));
        findViewById(R.id.atom_deco_btn_select_element).setEnabled(false);

        ((EditText)findViewById(R.id.atom_deco_et_charge)).setText(String.valueOf(mSelectedAtomDecoration.getCharge()));

        mChargeAsCircleSeekBox = findViewById(R.id.atom_deco_sb_charge_circle);
        mChargeAsCircleSeekBox.setProgress(mSelectedAtomDecoration.getChargeAsCircle().ordinal());
        mChargeAsCircleSeekBox.setEnabled(mSelectedAtomDecoration.getCharge() == 1 || mSelectedAtomDecoration.getCharge() == -1);

        ((SeekBar)findViewById(R.id.atom_deco_sb_starmark)).setProgress(mSelectedAtomDecoration.getMarker().ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.SELECT_ELEMENT_REQ.ordinal()) {
            if (resultCode == RESULT_OK) {
                String atomName = data.getStringExtra("AtomName");

                if (atomName != null && atomName.length() >= 1) {
                    Project.instance().changeSelectedAtom(atomName);
                    mAtomDecoView.invalidate();
                }
            }
        }
    }
}