package com.coldradio.benzene.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.PeriodicTable;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.util.AppEnv;

public class SaturateHActivity extends AppCompatActivity {
    private TextView mCarbonHMaxTV, mNitrogenHMaxTV, mOxygenHMaxTV;
    private TextView[] mCustomHMaxTV = new TextView[1];
    private CheckBox[] mCustomCB = new CheckBox[1];
    private AtomicNumber[] mCustomAN = new AtomicNumber[]{AtomicNumber.NONE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saturate_with_h_main);

        mCarbonHMaxTV = findViewById(R.id.tv_carbon_max_h);
        mNitrogenHMaxTV = findViewById(R.id.tv_nitrogen_max_h);
        mOxygenHMaxTV = findViewById(R.id.tv_oxygen_max_h);
        mCustomHMaxTV[0] = findViewById(R.id.tv_custom1_max_h);
        //mCustomHMaxTV[1] = findViewById(R.id.tv_custom2_max_h);

        // setting for carbon
        ((CheckBox) findViewById(R.id.cb_carbon)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.sb_carbon_max_h).setEnabled(isChecked);
            }
        });
        ((SeekBar) findViewById(R.id.sb_carbon_max_h)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCarbonHMaxTV.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // setting for nitrogen
        ((CheckBox) findViewById(R.id.cb_nitrogen)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.sb_nitrogen_max_h).setEnabled(isChecked);
            }
        });
        ((SeekBar) findViewById(R.id.sb_nitrogen_max_h)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mNitrogenHMaxTV.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // setting for oxygen
        ((CheckBox) findViewById(R.id.cb_oxygen)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.sb_oxygen_max_h).setEnabled(isChecked);
            }
        });
        ((SeekBar) findViewById(R.id.sb_oxygen_max_h)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mOxygenHMaxTV.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // setting for custom1
        findViewById(R.id.sb_custom1_max_h).setEnabled(false);
        mCustomCB[0] = findViewById(R.id.cb_custom1);
        mCustomCB[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.sb_custom1_max_h).setEnabled(isChecked);
                findViewById(R.id.btn_custom1).setEnabled(isChecked);
            }
        });
        findViewById(R.id.btn_custom1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.coldradio.benzene.CHANGE_ATOM");

                intent.putExtra("CustomID", 0);
                startActivityForResult(intent, ActivityRequestCode.SELECT_ELEMENT_REQ.ordinal());
            }
        });
        ((SeekBar) findViewById(R.id.sb_custom1_max_h)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCustomHMaxTV[0].setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // setting for custom2

        // setting for OK Cancel Button
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean carbonEnabled = findViewById(R.id.sb_carbon_max_h).isEnabled();
                boolean nitrogenEnabled = findViewById(R.id.sb_nitrogen_max_h).isEnabled();
                boolean oxygenEnabled = findViewById(R.id.sb_oxygen_max_h).isEnabled();
                boolean customEnabled1 = findViewById(R.id.sb_custom1_max_h).isEnabled();

                if (carbonEnabled || nitrogenEnabled || oxygenEnabled || customEnabled1) {
                    ProjectFileManager.instance().pushCompoundChangedHistory(Project.instance().getElementSelector().getSelectedCompound());
                }
                if (carbonEnabled) {
                    saturateCNO(R.id.cb_carbon_family, PeriodicTable.CARBON_FAMILY, mCarbonHMaxTV);
                }
                if (nitrogenEnabled) {
                    saturateCNO(R.id.cb_nitrogen_family, PeriodicTable.NITROGEN_FAMILY, mNitrogenHMaxTV);
                }
                if (oxygenEnabled) {
                    saturateCNO(R.id.cb_oxygen_family, PeriodicTable.OXYGEN_FAMILY, mOxygenHMaxTV);
                }
                if (customEnabled1) {
                    Project.instance().saturateSelectedWithHydrogen(mCustomAN[0], Integer.parseInt(mCustomHMaxTV[0].getText().toString()));
                }
                finish();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saturateCNO(int family_RID, AtomicNumber[] families, TextView textView) {
        CheckBox family_cb = findViewById(family_RID);
        int max_h = Integer.parseInt(textView.getText().toString());

        if (family_cb.isChecked()) {
            for (AtomicNumber an : families) {
                Project.instance().saturateSelectedWithHydrogen(an, max_h);
            }
        } else {
            Project.instance().saturateSelectedWithHydrogen(families[0], max_h);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.SELECT_ELEMENT_REQ.ordinal()) {
            if (resultCode == RESULT_OK) {
                AtomicNumber an = AtomicNumber.valueOf(data.getStringExtra("AtomName"));
                int index = data.getIntExtra("CustomID", 0);

                mCustomAN[index] = an;
                mCustomCB[index].setText("Saturate " + an.toString() + ". Max Bonds: ");
            }
        }
    }
}
