package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.View;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.view.drawer.AtomDecorationDrawer;
import com.coldradio.benzene.view.drawer.GenericDrawer;
import com.coldradio.benzene.view.drawer.PaintSet;

class Preview extends View {
    private AtomDecorationDrawer mAtomDecorationDrawer = new AtomDecorationDrawer();
    private PointF mCenter;

    public Preview(Context context) {
        super(context);
    }

    public void setCenter(PointF center) {
        mCenter = center;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Compound compound = Project.instance().getElementSelector().getSelectedCompound();

        canvas.translate(-mCenter.x + getWidth() / 2, -mCenter.y + getHeight() / 2);

        GenericDrawer.draw(compound, canvas, PaintSet.instance().paint(PaintSet.PaintType.GENERAL));
        mAtomDecorationDrawer.draw(compound, canvas, PaintSet.instance().paint(PaintSet.PaintType.GENERAL));
    }
}