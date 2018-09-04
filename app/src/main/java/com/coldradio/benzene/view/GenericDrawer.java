package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Compound;
import java.util.List;

public class GenericDrawer implements CompoundDrawer.IComponentDrawer {
    @Override
    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
        List<Atom> atoms = compound.getAtoms();

        for(int ii = 0; ii < atoms.size() - 1; ++ii) {
            canvas.drawLine(atoms.get(ii).getPoint().x, atoms.get(ii).getPoint().y, atoms.get(ii + 1).getPoint().x, atoms.get(ii+1).getPoint().y, paint);
        }
        if(compound.isCyclo()) {
            canvas.drawLine(atoms.get(atoms.size()-1).getPoint().x, atoms.get(atoms.size()-1).getPoint().y, atoms.get(0).getPoint().x, atoms.get(0).getPoint().y, paint);
        }
        return true;
    }

    @Override
    public String getID() {
        return "GenericDrawer";
    }
}
