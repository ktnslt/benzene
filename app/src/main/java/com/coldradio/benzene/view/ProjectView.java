package com.coldradio.benzene.view;

import android.content.Context;
import android.view.View;

import com.coldradio.benzene.library.CompoundLibrary;
import com.coldradio.benzene.library.local.LocalCompounds;
import com.coldradio.benzene.library.local.LocalSearch;
import com.coldradio.benzene.library.pubchem.PubChemSearch;
import com.coldradio.benzene.util.Notifier;
import com.coldradio.benzene.project.ProjectFileManager;

public class ProjectView extends View {
    public ProjectView(Context context) {
        super(context);

        //compound library settings
        LocalCompounds.instance().parseLibrary(this.getResources());

        // set context for those needs
        ProjectFileManager.instance().initProjectListAtStartUp();
    }
}
