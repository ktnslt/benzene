package com.coldradio.benzene.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.coldradio.benzene.R;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.library.CompoundIndex;
import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.project.history.CompoundAddedHistory;
import com.coldradio.benzene.util.Notifier;

import java.util.List;

public class CompoundSearchAdapter extends RecyclerView.Adapter<CompoundSearchAdapter.CompoundViewHolder> {
    static class CompoundViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mCID;
        private TextView mMF;
        private TextView mMW;
        private TextView mIUPACName;
        private ImageView mPreview;

        private CompoundViewHolder(View v) {
            super(v);
            mTitle = v.findViewById(R.id.tv_title);
            mCID = v.findViewById(R.id.tv_cid);
            mMF = v.findViewById(R.id.tv_mf);
            mMW = v.findViewById(R.id.tv_mw);
            mIUPACName = v.findViewById(R.id.tv_iupac);
            mPreview = v.findViewById(R.id.iv_preview);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CompoundLibrary.instance().requestCompound(getAdapterPosition(), new Response.Listener<List<Compound>>() {
                @Override
                public void onResponse(List<Compound> compound) {
                    if (compound != null) {
                        for (Compound c : compound) {
                            Project.instance().addCompound(c, true);
                            ProjectFileManager.instance().push(new CompoundAddedHistory(c));
                        }
                        Notifier.instance().notification(mTitle.getText().toString() + " is added. " + (compound.size() > 1 ? "Total " + compound.size() + " Compounds" : ""));
                    }
                }
            });
        }
    }

    @Override @NonNull
    public CompoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.compound_holder_main, parent, false);
        return new CompoundViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompoundViewHolder holder, int position) {
        CompoundIndex index = CompoundLibrary.instance().getCompoundIndex(position);

        holder.mTitle.setText(index.title);
        holder.mCID.setText(String.valueOf(index.cid));
        holder.mMF.setText(index.mf);
        holder.mMW.setText(String.valueOf(index.mw));
        holder.mIUPACName.setText(index.IUPAC);
        holder.mPreview.setImageBitmap(index.getBitmap());  // when getBitmap() returns null, this will reset the Preview
    }

    @Override
    public int getItemCount() {
        return CompoundLibrary.instance().size();
    }
}
