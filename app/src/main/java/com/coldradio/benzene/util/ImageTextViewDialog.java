package com.coldradio.benzene.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldradio.benzene.R;

public class ImageTextViewDialog {
    private AlertDialog mDialog;
    private TextView mTitleTextView;
    private ImageView mImageView;
    private TextView mContentTextView;

    public ImageTextViewDialog() {
        Activity activity = AppEnv.instance().getCurrentActivity();

        if (activity != null) {
            View dialogView = activity.getLayoutInflater().inflate(R.layout.image_textview_dialog_main, null);

            mDialog = new AlertDialog.Builder(activity).create();
            mTitleTextView = dialogView.findViewById(R.id.tv_title);
            mImageView = dialogView.findViewById(R.id.image_view);
            mContentTextView = dialogView.findViewById(R.id.text_view);
            mDialog.setView(dialogView);

            // set default listener
            dialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
        }
    }

    public ImageTextViewDialog setTitle(String title) {
        if (mTitleTextView != null)
            mTitleTextView.setText(title);
        return this;
    }

    public ImageTextViewDialog show() {
        if (mDialog != null)
            mDialog.show();
        return this;
    }

    public ImageTextViewDialog dismiss() {
        if (mDialog != null)
            mDialog.dismiss();
        return this;
    }

    public ImageTextViewDialog setImage(Bitmap bitmap) {
        if (mImageView != null)
            mImageView.setImageBitmap(bitmap);
        return this;
    }

    public ImageTextViewDialog setContent(Spanned content) {
        if (mContentTextView != null)
            mContentTextView.setText(content);
        return this;
    }
}
