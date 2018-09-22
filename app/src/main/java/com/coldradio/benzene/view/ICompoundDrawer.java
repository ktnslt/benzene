package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.coldradio.benzene.compound.Compound;

interface ICompoundDrawer {
    boolean draw(Compound compound, Canvas canvas, Paint paint);
    String getID();
}