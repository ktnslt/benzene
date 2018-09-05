package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Benzene;
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

            for (int ii = 0; ii < atoms.size() - 1; ++ii) {
                PointF p1 = atoms.get(ii).getPoint(), p2 = atoms.get(ii + 1).getPoint();

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                if(benzene.getDrawingMode() == Benzene.DrawingMode.ODD_LINE && (ii % 2) == 0) {
                    drawDoubleBond(p1.x, p1.y, p2.x, p2.y, benzeneCenter, canvas, paint);
                } else if(benzene.getDrawingMode() == Benzene.DrawingMode.EVEN_LINE && (ii % 2) == 1) {
                    drawDoubleBond(p1.x, p1.y, p2.x, p2.y, benzeneCenter, canvas, paint);
                }
            }
            PointF p1 = atoms.get(atoms.size() - 1).getPoint(), p2 = atoms.get(0).getPoint();

            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);

            if(benzene.getDrawingMode() == Benzene.DrawingMode.EVEN_LINE) {
                drawDoubleBond(p1.x, p1.y, p2.x, p2.y, benzeneCenter, canvas, paint);
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
