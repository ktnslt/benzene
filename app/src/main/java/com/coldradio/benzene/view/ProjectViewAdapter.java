package com.coldradio.benzene.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.coldradio.benzene.util.TextUtil;

public class ProjectViewAdapter extends RecyclerView.Adapter<ProjectViewAdapter.ProjectViewHolder> {
    private Activity mMainActivity;

    static class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mProjectName;
        private TextView mLastModifiedTime;
        private ImageView mImageView;
        final private ProjectViewAdapter mAdapter;

        private ProjectViewHolder(View v, ProjectViewAdapter adapter) {
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
                    PopupMenu popup = new PopupMenu(mAdapter.mMainActivity, v);

                    popup.getMenuInflater().inflate(R.menu.project_view_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            final String projectNameString = mProjectName.getText().toString();

                            if (id == R.id.action_project_rename) {
                                final EditTextDialog dialog = new EditTextDialog(mAdapter.mMainActivity);

                                dialog.setOkListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (FileUtil.exists(dialog.getInputText())) {
                                            Notifier.instance().notification("Already exists");
                                        } else if (FileUtil.validFileName(projectNameString)) {
                                            if (ProjectFileManager.instance().rename(projectNameString, dialog.getInputText())) {
                                                mAdapter.notifyItemChanged(getAdapterPosition());
                                            } else {
                                                Notifier.instance().notification("Rename File Failed");
                                            }
                                            dialog.dismiss();
                                        } else {
                                            Notifier.instance().notification("Invalid Chars " + FileUtil.INVALID_CHARS);
                                        }
                                    }
                                }).setInitialText(projectNameString).setTitle("New Project Name");
                                dialog.show();
                            } else if (id == R.id.action_project_share_image) {
                                if (!FileUtil.share(AppEnv.instance().projectFileDir() + projectNameString + Configuration.IMAGE_FILE_EXT, mAdapter.mMainActivity)) {
                                    Notifier.instance().notification("Share Failed");
                                }
                            } else if (id == R.id.action_project_share_project_file) {
                                if (!FileUtil.share(AppEnv.instance().projectFileDir() + projectNameString + Configuration.PROJECT_FILE_EXT, mAdapter.mMainActivity)) {
                                    Notifier.instance().notification("Share Failed");
                                }
                            } else if (id == R.id.action_project_copy) {
                                int insertedIndex = ProjectFileManager.instance().copy(projectNameString);

                                if (insertedIndex >= 0) {
                                    mAdapter.notifyItemInserted(insertedIndex);
                                }
                            } else if (id == R.id.action_project_delete) {
                                ProjectFileManager.instance().delete(projectNameString);
                                mAdapter.notifyItemRemoved(getAdapterPosition());
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
            ProjectFileManager.instance().loadToProject(mProjectName.getText().toString(), Project.instance());
            mAdapter.mMainActivity.startActivityForResult(new Intent("com.coldradio.benzene.CANVAS"), ActivityRequestCode.START_CANVAS_REQ.ordinal());
        }
    }

    public ProjectViewAdapter(Activity activity) {
        mMainActivity = activity;
    }

    @NonNull @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_view_holder_main, parent, false);

        return new ProjectViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectFile projectFile = ProjectFileManager.instance().getProjectFile(position);

        if (ProjectFileManager.instance().hasFilter()) {
            holder.mProjectName.setText(Html.fromHtml(TextUtil.styleKeyword(projectFile.getName(), ProjectFileManager.instance().getFilter().getKeyword())));
        } else {
            holder.mProjectName.setText(projectFile.getName());
        }
        holder.mLastModifiedTime.setText(FileUtil.toDateTimeString(projectFile.lastModified()));
        holder.mImageView.setImageBitmap(projectFile.getPreview());
    }

    @Override
    public int getItemCount() {
        return ProjectFileManager.instance().numberOfProjects();
    }
}
