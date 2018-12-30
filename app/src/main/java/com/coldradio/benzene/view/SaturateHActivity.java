package com.coldradio.benzene.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.AtomicNumber;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundReactor;
import com.coldradio.benzene.project.Project;

public class SaturateHActivity extends AppCompatActivity {
    private TextView mCarbonHMaxTV, mNitrogenHMaxTV, mOxygenHMaxTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saturate_with_h_main);

        mCarbonHMaxTV = findViewById(R.id.tv_carbon_max_h);
        mNitrogenHMaxTV = findViewById(R.id.tv_nitrogen_max_h);
        mOxygenHMaxTV = findViewById(R.id.tv_oxygen_max_h);

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

        // setting for OK Cancel Button
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.sb_carbon_max_h).isEnabled()) {
                    Project.instance().saturateSelectedWithHydrogen(AtomicNumber.C, Integer.parseInt(mCarbonHMaxTV.getText().toString()));
                }
                if (findViewById(R.id.sb_nitrogen_max_h).isEnabled()) {
                    Project.instance().saturateSelectedWithHydrogen(AtomicNumber.N, Integer.parseInt(mNitrogenHMaxTV.getText().toString()));
                }
                if (findViewById(R.id.sb_oxygen_max_h).isEnabled()) {
                    Project.instance().saturateSelectedWithHydrogen(AtomicNumber.O, Integer.parseInt(mOxygenHMaxTV.getText().toString()));
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
}
