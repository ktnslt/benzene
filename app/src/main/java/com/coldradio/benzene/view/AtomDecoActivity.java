package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
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
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.view.drawer.GenericDrawer;
import com.coldradio.benzene.view.drawer.PaintSet;

public class AtomDecoActivity extends AppCompatActivity {
    private EditText mChargeET;
    private Atom.Marker mOriginalMarker;
    private Atom.UnsharedElectron[] mOriginalUnsharedElectron = new Atom.UnsharedElectron[4];
    private int mOriginalCharge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_deco_main);

        readCurrentConfiguration();

        // copy previous values to be modified in this activity. the original values will be used when canceling this activity
        Atom atom = Project.instance().getElementSelector().getSelectedAtom();

        if (atom != null) {
            mOriginalMarker = atom.getMarker();
            for (int ii = 0; ii < mOriginalUnsharedElectron.length; ++ii) {
                mOriginalUnsharedElectron[ii] = atom.getUnsharedElectron(Atom.Direction.values()[ii]);
            }
            mOriginalCharge = atom.getCharge();
        }

        // setting for charge
        mChargeET = findViewById(R.id.atom_deco_et_charge);

        mChargeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Atom atom = Project.instance().getElementSelector().getSelectedAtom();

                if (atom != null)
                    atom.setCharge(Integer.parseInt(s.toString()));
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

        // setting for marker
        SeekBar starmarkSeekbar = findViewById(R.id.atom_deco_sb_starmark);
        starmarkSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Atom atom = Project.instance().getElementSelector().getSelectedAtom();

                if (atom != null && progress >= 0 && progress < Atom.Marker.values().length)
                    atom.setMarker(Atom.Marker.values()[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // setting for show element
        ((CheckBox)findViewById(R.id.atom_deco_cb_show_element)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Atom atom = Project.instance().getElementSelector().getSelectedAtom();

                if (atom != null) {
                    atom.setShowElement(isChecked);
                }
            }
        });

        // setting for OK Cancel Button
        findViewById(R.id.atom_deco_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreOriginal();
                finish();
            }
        });

        findViewById(R.id.atom_deco_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // setting for unshared electron
        findViewById(R.id.atom_deco_btn_up_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.TOP, Atom.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_up_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.TOP, Atom.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_up_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.TOP, Atom.UnsharedElectron.DOUBLE);
            }
        });

        findViewById(R.id.atom_deco_btn_down_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.BOTTOM, Atom.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_down_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.BOTTOM, Atom.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_down_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.BOTTOM, Atom.UnsharedElectron.DOUBLE);
            }
        });

        findViewById(R.id.atom_deco_btn_left_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.LEFT, Atom.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_left_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.LEFT, Atom.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_left_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.LEFT, Atom.UnsharedElectron.DOUBLE);
            }
        });

        findViewById(R.id.atom_deco_btn_right_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.RIGHT, Atom.UnsharedElectron.NONE);
            }
        });

        findViewById(R.id.atom_deco_btn_right_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.RIGHT, Atom.UnsharedElectron.SINGLE);
            }
        });

        findViewById(R.id.atom_deco_btn_right_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsharedElectron(Atom.Direction.RIGHT, Atom.UnsharedElectron.DOUBLE);
            }
        });

        // setting for AtomDecoView
        ViewGroup preview = findViewById(R.id.atom_deco_view);

        if (preview != null) {
            preview.addView(new AtomDecoView(this));
        }
    }

    private void offsetCharge(int offset) {
        if (mChargeET != null) {
            mChargeET.setText(String.valueOf(Integer.parseInt(mChargeET.getText().toString()) + offset));
        }
    }

    private void unsharedElectron(Atom.Direction direction, Atom.UnsharedElectron unsharedElectron) {
        Atom atom = Project.instance().getElementSelector().getSelectedAtom();

        if (atom != null)
            atom.setUnsharedElectron(direction, unsharedElectron);
    }

    private void restoreOriginal() {
        Atom atom = Project.instance().getElementSelector().getSelectedAtom();

        if (atom != null) {
            atom.setMarker(mOriginalMarker);
            atom.setCharge(mOriginalCharge);
            for (int ii = 0; ii < mOriginalUnsharedElectron.length; ++ii) {
                atom.setUnsharedElectron(Atom.Direction.values()[ii], mOriginalUnsharedElectron[ii]);
            }
        }
    }

    private void readCurrentConfiguration() {

    }
}

class AtomDecoView extends View {
    public AtomDecoView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float dx = 0, dy = 0;
        Atom atom = Project.instance().getElementSelector().getSelectedAtom();

        if (atom != null) {
            dx = -atom.getPoint().x + getWidth() / 2;
            dy = -atom.getPoint().y + getHeight() / 2;
        }

        GenericDrawer.draw(Project.instance().getElementSelector().getSelectedCompound(), canvas, PaintSet.instance().general(), dx, dy);
    }
}