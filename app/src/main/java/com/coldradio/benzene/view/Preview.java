package com.coldradio.benzene.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Project;
import com.coldradio.benzene.view.drawer.AtomDecorationDrawer;
import com.coldradio.benzene.view.drawer.GenericDrawer;
import com.coldradio.benzene.view.drawer.PaintSet;
import com.coldradio.benzene.view.drawer.SelectedElementBackgroundDrawer;

class Preview extends View {
    private AtomDecorationDrawer mAtomDecorationDrawer = new AtomDecorationDrawer();
    private SelectedElementBackgroundDrawer mSelectedElementBackgroundDrawer = new SelectedElementBackgroundDrawer();
    private PointF mCenter;

    public Preview(Context context) {
        super(context);
    }

    public void setCenter(PointF center) {
        mCenter = center;
    }

    public PointF getCenter() {
        return mCenter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(-mCenter.x + getWidth() / 2, -mCenter.y + getHeight() / 2);

        Paint paint = PaintSet.instance().paint(PaintSet.PaintType.GENERAL);
        Compound compound = Project.instance().getElementSelector().getSelectedCompound();

        mSelectedElementBackgroundDrawer.draw(compound, canvas, paint);
        GenericDrawer.draw(compound, canvas, paint);
        mAtomDecorationDrawer.draw(compound, canvas, paint);
    }
}