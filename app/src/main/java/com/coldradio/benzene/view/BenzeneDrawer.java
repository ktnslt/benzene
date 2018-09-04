package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Benzene;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

public class BenzeneDrawer implements CompoundDrawer.IComponentDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        if(compound instanceof Benzene) {
            Benzene benzene = (Benzene)compound;
            List<Atom> atoms = compound.getAtoms();

            for (int ii = 0; ii < atoms.size() - 1; ++ii) {
                canvas.drawLine(atoms.get(ii).getPoint().x, atoms.get(ii).getPoint().y, atoms.get(ii + 1).getPoint().x, atoms.get(ii + 1).getPoint().y, paint);
            }
            canvas.drawLine(atoms.get(atoms.size() - 1).getPoint().x, atoms.get(atoms.size() - 1).getPoint().y, atoms.get(0).getPoint().x, atoms.get(0).getPoint().y, paint);

            if(benzene.getDrawingMode() == Benzene.DrawingMode.CIRCLE) {
                canvas.drawCircle(benzene.center().x, benzene.center().y, Configuration.LINE_LENGTH / 2, paint);
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String getID() {
        return "BenzeneDrawer";
    }
}
