package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SingleAttachPoint {
    private class AtomCwComparator implements Comparator<Atom> {
        private PointF mBasePoint;
        private PointF mCenter;

        public AtomCwComparator(PointF base, PointF center) {
            mBasePoint = base;
            mCenter = center;
        }

        @Override
        public int compare(Atom o1, Atom o2) {
            return Float.compare(Geometry.cwAngle(mBasePoint, o1.getPoint(), mCenter), Geometry.cwAngle(mBasePoint, o2.getPoint(), mCenter));
        }
    }

    private List<PointF> mCandidates = new ArrayList<>();

    private void fillCandidateWithCenterOfAngle(Atom selectedAtom) {
        List<Atom> boundSkeleton = CompoundInspector.boundSkeletonAtom(selectedAtom);

        //    3        this case shall be sorted to    2
        //    |                                        |
        // 1--+--2                                  1--+--3
        Collections.sort(boundSkeleton, new AtomCwComparator(boundSkeleton.get(0).getPoint(), selectedAtom.getPoint()));

        for (int ii = 0; ii < boundSkeleton.size(); ++ii) {
            // circular. If there are three skeleton. 0-1, 1-2, 2-0
            PointF a1p = boundSkeleton.get(ii).getPoint();
            PointF a2p = boundSkeleton.get((ii + 1) % boundSkeleton.size()).getPoint();

            mCandidates.add(Geometry.cwCenterOfAngle(a1p, a2p, selectedAtom.getPoint()));
        }
    }

    public SingleAttachPoint(Atom selectedAtom) {
        int boundCarbon = CompoundInspector.numberOfBoundSkeletonAtom(selectedAtom);

        // fill candidates position
        if (boundCarbon == 0) {
            // attach to methane
            PointF p = new PointF();

            p.set(selectedAtom.getPoint());
            p.offset(0, -Configuration.BOND_LENGTH);

            for (int ii = 0; ii < 360 / 45; ++ii) {
                mCandidates.add(Geometry.rotatePoint(p, selectedAtom.getPoint(), (float) Math.toRadians(45) * ii));
            }
        } else if (boundCarbon == 1) {
            PointF boundCarbonPoint = selectedAtom.getBonds().get(0).getBoundAtom().getPoint();

            mCandidates.add(Geometry.rotatePoint(boundCarbonPoint, selectedAtom.getPoint(), (float) Math.toRadians(120)));
            mCandidates.add(Geometry.rotatePoint(boundCarbonPoint, selectedAtom.getPoint(), (float) Math.toRadians(-120)));
        } else {
            fillCandidateWithCenterOfAngle(selectedAtom);
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
