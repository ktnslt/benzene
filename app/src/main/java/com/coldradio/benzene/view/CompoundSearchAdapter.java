package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundIndex;
import com.coldradio.benzene.compound.CompoundLibrary;
import com.coldradio.benzene.lib.Helper;
import com.coldradio.benzene.lib.ScreenInfo;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.Project;

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
        holder.mCompoundPreview.setCompound(index.compound, index.preferredIUPACName);
    }

    @Override
    public int getItemCount() {
        return CompoundLibrary.instance().size();
    }
}

class CompoundPreview extends View implements View.OnClickListener {
    private Compound mCompound;
    private String mName;
    private Paint mPaint;

    public CompoundPreview(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public void setCompound(Compound compound, String name) {
        mCompound = compound;
        mName = name;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        CompoundArranger.alignCenter(mCompound, new PointF(w/5, h/2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        GenericDrawer.draw(mCompound, canvas, mPaint);
    }

    @Override
    public void onClick(View v) {
        Compound compound = mCompound.copy();

        CompoundArranger.zoomToStandard(compound, 1);
        CompoundArranger.alignCenter(compound, ScreenInfo.instance().centerPoint());
        Project.instance().addCompoundAsSelected(compound);

        Helper.instance().notification(mName + " is added");
    }
}