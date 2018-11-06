package com.coldradio.benzene.view;

import android.content.Context;
import android.view.View;

import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.util.Helper;
import com.coldradio.benzene.project.ProjectFileManager;

public class ProjectView extends View {
    public ProjectView(Context context) {
        super(context);
        CompoundLibrary.instance().parseLibrary(this.getResources());

        // set context for those needs
        Helper.instance().setContext(this.getContext());
        ProjectFileManager.instance().setContext(this.getContext());
        ProjectFileManager.instance().load();
    }
}
