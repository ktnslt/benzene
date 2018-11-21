package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.library.SearchFilter;
import com.coldradio.benzene.util.Helper;
import com.coldradio.benzene.util.ScreenInfo;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.view.drawer.GenericDrawer;
import com.coldradio.benzene.view.drawer.PaintSet;

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

            if (preview != null) {
                mCompoundPreview = new CompoundPreview(v.getContext());
                preview.addView(mCompoundPreview);
            }
        }
    }

    private String styleKeyword(String msg, SearchFilter filter) {
        if (filter == null || filter.getKeyword().length() == 0)
            return msg;

        StringBuilder sb = new StringBuilder();
        String keyword = filter.getKeyword();

        for (int ii = 0, next; ii >= 0 && ii < msg.length(); ) {
            next = msg.indexOf(keyword, ii);

            if (next < 0) {
                sb.append(msg.substring(ii, msg.length()));
                break;
            } else {
                sb.append(msg.substring(ii, next));
                sb.append("<font color=black>");
                sb.append(keyword);
                sb.append("</font>");
                ii = next + keyword.length();
            }
        }

        return sb.toString();
    }
    @Override
    public CompoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.compound_holder_main, parent, false);
        return new CompoundViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CompoundViewHolder holder, int position) {
        CompoundIndex index = CompoundLibrary.instance().getCompoundIndex(position);
        SearchFilter filter = CompoundLibrary.instance().getSearchFilter();

        holder.mCompoundNameTextView.setText(Html.fromHtml(styleKeyword(index.preferredIUPACName, filter)));
        holder.mCompoundDescriptionTextView.setText(Html.fromHtml(styleKeyword(index.otherNames, filter)));
        holder.mCompoundPreview.setCompoundIndex(index);
    }

    @Override
    public int getItemCount() {
        return CompoundLibrary.instance().size();
    }
}

class CompoundPreview extends View implements View.OnClickListener {
    private CompoundIndex mCompoundIndex;

    public CompoundPreview(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public void setCompoundIndex(CompoundIndex compoundIndex) {
        mCompoundIndex = compoundIndex;
        CompoundArranger.alignCenter(mCompoundIndex.compound, new PointF(getWidth()/5, getHeight()/2));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        CompoundArranger.alignCenter(mCompoundIndex.compound, new PointF(w/5, h/2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        GenericDrawer.draw(mCompoundIndex.compound, canvas, PaintSet.instance().general());
    }

    @Override
    public void onClick(View v) {
        Compound compound = mCompoundIndex.compound.copy();

        CompoundArranger.zoomToStandard(compound, 1);
        CompoundArranger.alignCenter(compound, ScreenInfo.instance().centerPoint());
        Project.instance().addCompoundAsSelected(compound);

        Helper.instance().notification(mCompoundIndex.preferredIUPACName + " is added");
    }
}