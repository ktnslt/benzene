package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

import java.util.ArrayList;
import java.util.List;

public class SingleAttachPoint {
    private List<PointF> mCandidates = new ArrayList<>();

    private void fillCandidateWithCenterOfAngle(Atom attachAtom, boolean largerAngleFirst) {
        List<Atom> allBoundCarbon = CompoundInspector.allBoundCarbon(attachAtom);

        for (int ii = 0; ii < allBoundCarbon.size() - 1; ++ii) {
            PointF a1p = allBoundCarbon.get(ii).getPoint();
            PointF a2p = allBoundCarbon.get(ii + 1).getPoint();

            mCandidates.add(Geometry.centerOfAngle(a1p, a2p, attachAtom.getPoint(), largerAngleFirst));
        }

        for (int ii = 0; ii < allBoundCarbon.size() - 1; ++ii) {
            PointF a1p = allBoundCarbon.get(ii).getPoint();
            PointF a2p = allBoundCarbon.get(ii + 1).getPoint();

            mCandidates.add(Geometry.centerOfAngle(a1p, a2p, attachAtom.getPoint(), !largerAngleFirst));
        }
    }

    public SingleAttachPoint(Atom attachAtom) {
        int boundCarbon = CompoundInspector.numberOfBoundCarbon(attachAtom);

        // fill candidates position
        if (boundCarbon == 0) {
            // attach to methane
            PointF p = new PointF();

            p.set(attachAtom.getPoint());
            p.offset(0, -Configuration.LINE_LENGTH);

            for (int ii = 0; ii < 360 / 45; ++ii) {
                mCandidates.add(Geometry.rotatePoint(p, attachAtom.getPoint(), (float) Math.toRadians(45) * ii));
            }
        } else if (boundCarbon == 1) {
            PointF boundCarbonPoint = attachAtom.getBonds().get(0).getBoundAtom().getPoint();

            mCandidates.add(Geometry.rotatePoint(boundCarbonPoint, attachAtom.getPoint(), (float) Math.toRadians(120)));
            mCandidates.add(Geometry.rotatePoint(boundCarbonPoint, attachAtom.getPoint(), (float) Math.toRadians(-120)));
        } else if (boundCarbon == 2) {
            fillCandidateWithCenterOfAngle(attachAtom, true);
        } else {
            fillCandidateWithCenterOfAngle(attachAtom, false);
        }
    }

    public PointF nextPoint(PointF curPoint) {
        if (curPoint == null) {
            return mCandidates.get(0);
        } else {
            int index = mCandidates.indexOf(curPoint);

            return mCandidates.get((index + 1) % mCandidates.size());
        }
    }

    public PointF prevPoint(PointF curPoint) {
        if (curPoint == null) {
            return mCandidates.get(0);
        } else {
            int index = mCandidates.indexOf(curPoint) - 1;

            index = index >= 0 ? index : mCandidates.size() - 1;
            return mCandidates.get(index);
        }
    }
}
