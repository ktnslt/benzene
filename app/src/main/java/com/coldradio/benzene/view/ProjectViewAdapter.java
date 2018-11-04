package com.coldradio.benzene.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.ProjectFile;
import com.coldradio.benzene.project.ProjectFileManager;

public class ProjectViewAdapter extends RecyclerView.Adapter<ProjectViewAdapter.ProjectViewHolder> {
    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        private TextView mProjectName;

        ProjectViewHolder(View v) {
            super(v);
            mProjectName = v.findViewById(R.id.project_name);
        }
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
    }

    @Override
    public int getItemCount() {
        return ProjectFileManager.instance().projectFileNumber();
    }
}
