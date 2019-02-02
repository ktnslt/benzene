package com.coldradio.benzene.util;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coldradio.benzene.R;

public class EditTextDialog {
    private AlertDialog mDialog;
    private View mDialogView;
    private TextView mTitleTextView;

    public EditTextDialog(Activity activity) {
        mDialog = new AlertDialog.Builder(activity).create();
        mDialogView = activity.getLayoutInflater().inflate(R.layout.edittext_dialog_main, null);
        mTitleTextView = mDialogView.findViewById(R.id.tv_title);

        // set default listener
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.setView(mDialogView);
    }

    public EditTextDialog setOkListener(View.OnClickListener listener) {
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(listener);
        return this;
    }

    public EditTextDialog setCancelListener(View.OnClickListener listener) {
        mDialogView.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        return this;
    }

    public EditTextDialog show() {
        mDialog.show();
        return this;
    }

    public EditTextDialog dismiss() {
        mDialog.dismiss();
        return this;
    }

    public String getInputText() {
        return ((EditText)mDialogView.findViewById(R.id.edit_text)).getText().toString();
    }

    public EditTextDialog setInitialText(String text) {
        ((EditText)mDialogView.findViewById(R.id.edit_text)).setText(text);
        return this;
    }

    public EditTextDialog setTitle(String title) {
        mTitleTextView.setText(title);
        return this;
    }
}
