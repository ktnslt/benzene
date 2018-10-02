package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.lib.CompoundIndex;
import com.coldradio.benzene.lib.CompoundLibrary;
import com.coldradio.benzene.project.Configuration;

public class CompoundSearchAdapter extends RecyclerView.Adapter<CompoundSearchAdapter.CompoundViewHolder> {
    public static class CompoundViewHolder extends RecyclerView.ViewHolder {
        private TextView mCompoundNameTextView;
        private TextView mCompoundDescriptionTextView;
        private CompoundPreview mCompoundPreview;

        CompoundViewHolder(View v) {
            super(v);
            mCompoundNameTextView = v.findViewById(R.id.compound_name);
            mCompoundDescriptionTextView = v.findViewById(R.id.compound_description);

            // set compound review
            ViewGroup preview = v.findViewById(R.id.compound_holder_main);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            paint.setStrokeWidth(Configuration.LINE_THICKNESS);
            paint.setStyle(Paint.Style.STROKE);

            if (preview != null) {
                mCompoundPreview = new CompoundPreview(v.getContext());
                mCompoundPreview.setPaint(paint);
                preview.addView(mCompoundPreview);
            }
        }
    }

    @Override
    public CompoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.compound_holder_main, parent, false);
        CompoundViewHolder vh = new CompoundViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(CompoundViewHolder holder, int position) {
        CompoundIndex index = CompoundLibrary.instance().getCompoundIndexByIndex(position);
        holder.mCompoundNameTextView.setText(index.preferredIUPACName);
        holder.mCompoundDescriptionTextView.setText(index.otherNames);
        holder.mCompoundPreview.setCompound(index.compound);
    }

    @Override
    public int getItemCount() {
        return CompoundLibrary.instance().size();
    }
}

class CompoundPreview extends View {
    private Compound mCompound;
    private Paint mPaint;

    public CompoundPreview(Context context) {
        super(context);
    }

    public void setCompound(Compound compound) {
        RectF rect = compound.rectRegion();

        mCompound = compound.offset(-rect.left, -rect.top);
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        GenericDrawer.draw(mCompound, canvas, mPaint);
    }
}