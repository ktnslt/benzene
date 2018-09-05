package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Benzene;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.geometry.Geometry;
import com.coldradio.benzene.project.Configuration;
import java.util.List;

public class BenzeneDrawer implements CompoundDrawer.IComponentDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        if(compound instanceof Benzene) {
            Benzene benzene = (Benzene)compound;
            PointF benzeneCenter = benzene.center();
            List<Atom> atoms = compound.getAtoms();

            for (int ii = 0; ii < atoms.size(); ++ii) {
                Atom curAtom = atoms.get(ii), nextAtom = atoms.get((ii+1) % 6);
                PointF p1 = curAtom.getPoint(), p2 = nextAtom.getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                if(benzene.getDrawingMode() == Benzene.DrawingMode.LINE && curAtom.hasBond(nextAtom) == Bond.BondType.DOUBLE) {
                    drawDoubleBond(p1.x, p1.y, p2.x, p2.y, benzeneCenter, canvas, paint);
                }
            }

            if(benzene.getDrawingMode() == Benzene.DrawingMode.CIRCLE) {
                canvas.drawCircle(benzeneCenter.x, benzeneCenter.y, Configuration.LINE_LENGTH / 2, paint);
            }
            return true;
        }
        else {
            return false;
        }
    }
    private void drawDoubleBond(float x1, float y1, float x2, float y2, PointF benzeneCenter, Canvas canvas, Paint paint) {
        PointF p1 = Geometry.zoomOut(x1, y1, benzeneCenter, 0.8f);
        PointF p2 = Geometry.zoomOut(x2, y2, benzeneCenter, 0.8f);

        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
    }
    @Override
    public String getID() {
        return "BenzeneDrawer";
    }
}
