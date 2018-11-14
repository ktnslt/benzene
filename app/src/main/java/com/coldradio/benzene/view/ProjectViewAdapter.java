package com.coldradio.benzene.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.ImageCreator;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFile;
import com.coldradio.benzene.project.ProjectFileManager;

public class ProjectViewAdapter extends RecyclerView.Adapter<ProjectViewAdapter.ProjectViewHolder> {
    private static Activity smMainActivity;

    static class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mProjectName;
        private ImageView mImageView;

        ProjectViewHolder(View v) {
            super(v);
            mProjectName = v.findViewById(R.id.project_name);
            mImageView = v.findViewById(R.id.project_preview);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ProjectFileManager.instance().load(mProjectName.getText().toString(), Project.instance());
            ProjectViewAdapter.smMainActivity.startActivityForResult(new Intent("com.coldradio.benzene.CANVAS"), ActivityRequestCode.CANVAS_REQ.ordinal());
        }
    }

    public ProjectViewAdapter(Activity activity) {
        smMainActivity = activity;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_view_holder_main, parent, false);

        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        ProjectFile projectFile = ProjectFileManager.instance().getProjectFile(position);

        holder.mProjectName.setText(projectFile.getName());
        ImageCreator.showPreview(holder.mImageView, projectFile.getName());
    }

    @Override
    public int getItemCount() {
        return ProjectFileManager.instance().projectFileNumber();
    }
}
