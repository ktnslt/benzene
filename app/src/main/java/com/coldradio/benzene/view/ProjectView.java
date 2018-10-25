package com.coldradio.benzene.view;

import android.content.Context;
import android.view.View;

import com.coldradio.benzene.compound.CompoundLibrary;

public class ProjectView extends View {
    public ProjectView(Context context) {
        super(context);
        CompoundLibrary.instance().parseLibrary(this.getResources());
    }
}
