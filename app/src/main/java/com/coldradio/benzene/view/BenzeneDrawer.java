package com.coldradio.benzene.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.ConjuatedCyclicCompound;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.project.Configuration;

import java.util.List;

//public class BenzeneDrawer implements CompoundDrawer.IComponentDrawer {
//    @Override
//    public boolean draw(Compound compound, Canvas canvas, Paint paint) {
//        if (compound instanceof ConjuatedCyclicCompound) {
//            ConjuatedCyclicCompound benzene = (ConjuatedCyclicCompound) compound;
//            PointF benzeneCenter = benzene.center();
//            List<Atom> atoms = compound.getAtoms();
//
//            for (int ii = 0; ii < atoms.size(); ++ii) {
//                Atom curAtom = atoms.get(ii), nextAtom = atoms.get((ii + 1) % 6);
//                PointF p1 = curAtom.getPoint(), p2 = nextAtom.getPoint();
//
//                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
//                if (benzene.getDrawingMode() == ConjuatedCyclicCompound.DrawingMode.LINE && curAtom.getBondType(nextAtom) == Bond.BondType.DOUBLE) {
//                    DrawingLibrary.drawDoubleBond(p1.x, p1.y, p2.x, p2.y, benzeneCenter, canvas, paint);
//                }
//            }
//
//            if (benzene.getDrawingMode() == ConjuatedCyclicCompound.DrawingMode.CIRCLE) {
//                canvas.drawCircle(benzeneCenter.x, benzeneCenter.y, Configuration.LINE_LENGTH / 2, paint);
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public String getID() {
//        return "BenzeneDrawer";
//    }
//}
