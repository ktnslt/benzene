package com.coldradio.benzene.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldradio.benzene.R;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.project.PreviewHandler;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.project.ProjectFile;
import com.coldradio.benzene.project.ProjectFileManager;
import com.coldradio.benzene.util.EditTextDialog;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.FileUtil;
import com.coldradio.benzene.util.Notifier;

public class ProjectViewAdapter extends RecyclerView.Adapter<ProjectViewAdapter.ProjectViewHolder> {
    private static Activity smMainActivity;

    static class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mProjectName;
        private TextView mLastModifiedTime;
        private ImageView mImageView;
        private int mHolderPosition;
        final private ProjectViewAdapter mAdapter;

        ProjectViewHolder(View v, ProjectViewAdapter adapter) {
            super(v);
            mProjectName = v.findViewById(R.id.project_view_name);
            mLastModifiedTime = v.findViewById(R.id.project_view_last_modified);
            mImageView = v.findViewById(R.id.project_view_preview);
            mAdapter = adapter;
            v.setOnClickListener(this);

            // More Menu Button
            final ImageButton btn = v.findViewById(R.id.project_view_button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(smMainActivity, v);

                    popup.getMenuInflater().inflate(R.menu.project_view_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            final String projectNameString = mProjectName.getText().toString();

                            if (id == R.id.action_project_rename) {
                                final EditTextDialog dialog = new EditTextDialog(smMainActivity);

                                dialog.setOkListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (FileUtil.exists(dialog.getInputText())) {
                                            Notifier.instance().notification("Already exists");
                                        } else if (FileUtil.validFileName(projectNameString)) {
                                            if (ProjectFileManager.instance().rename(projectNameString, dialog.getInputText())) {
                                                mAdapter.notifyItemChanged(mHolderPosition);
                                            } else {
                                                Notifier.instance().notification("Rename File Failed");
                                            }
                                            dialog.dismiss();
                                        } else {
                                            Notifier.instance().notification("Invalid Chars " + FileUtil.INVALID_CHARS);
                                        }
                                    }
                                }).setInitialText(projectNameString);
                                dialog.show();
                            } else if (id == R.id.action_project_share_image) {
                                if (!FileUtil.share(AppEnv.instance().projectFileDir() + projectNameString + Configuration.IMAGE_FILE_EXT, smMainActivity)) {
                                    Notifier.instance().notification("Share Failed");
                                }
                            } else if (id == R.id.action_project_share_project_file) {
                                if (!FileUtil.share(AppEnv.instance().projectFileDir() + projectNameString + Configuration.PROJECT_FILE_EXT, smMainActivity)) {
                                    Notifier.instance().notification("Share Failed");
                                }
                            } else if (id == R.id.action_project_copy) {
                                int insertedIndex = ProjectFileManager.instance().copy(projectNameString);

                                if (insertedIndex >= 0) {
                                    mAdapter.notifyItemInserted(insertedIndex);
                                }
                            } else if (id == R.id.action_project_delete) {
                                ProjectFileManager.instance().delete(projectNameString);
                                mAdapter.notifyItemRemoved(mHolderPosition);
                            } else {
                                return false;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
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

        return new ProjectViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        ProjectFile projectFile = ProjectFileManager.instance().getProjectFile(position);

        holder.mProjectName.setText(projectFile.getName());
        holder.mLastModifiedTime.setText(FileUtil.toDateTimeString(projectFile.lastModified()));
        holder.mHolderPosition = position;
        PreviewHandler.showPreview(holder.mImageView, projectFile.getName());
    }

    @Override
    public int getItemCount() {
        return ProjectFileManager.instance().numberOfProjects();
    }
}
