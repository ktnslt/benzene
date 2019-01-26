package com.coldradio.benzene.compound.funcgroup;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.project.Configuration;
import com.coldradio.benzene.util.Geometry;
import com.coldradio.benzene.util.MathConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppendPointProducer {
    private class AtomCwComparator implements Comparator<Atom> {
        private PointF mBasePoint;
        private PointF mCenter;

        public AtomCwComparator(PointF base, PointF center) {
            mBasePoint = base;
            mCenter = center;
        }

        @Override
        public int compare(Atom a1, Atom a2) {
            return Float.compare(Geometry.cwAngle(mBasePoint, a1.getPoint(), mCenter), Geometry.cwAngle(mBasePoint, a2.getPoint(), mCenter));
        }
    }

    private List<PointF> mCandidates = new ArrayList<>();

    private void reorderBoundSkeleton(List<Atom> boundSkeleton, PointF center) {
        //    3        this case shall be sorted to    2
        //    |                                        |
        // 1--+--2                                  1--+--3
        Collections.sort(boundSkeleton, new AtomCwComparator(boundSkeleton.get(0).getPoint(), center));

        // then find the largest angle, and make it to the first
        int maxIndex = 0;
        float maxAngle = 0;

        for (int ii = 0; ii < boundSkeleton.size(); ++ii) {
            PointF p1 = boundSkeleton.get(ii).getPoint();
            PointF p2 = boundSkeleton.get((ii + 1) % boundSkeleton.size()).getPoint();
            float angle = Geometry.cwAngle(p1, p2, center);

            if (angle > maxAngle) {
                maxAngle = angle;
                maxIndex = ii;
            }
        }

        if (maxIndex > 0) {
            // move the elements to the end of the list
            Collections.rotate(boundSkeleton, -maxIndex);
        }
    }

    private void fillCandidateWithCenterOfAngle(Atom selectedAtom) {
        List<Atom> boundSkeleton = CompoundInspector.boundSkeletonAtoms(selectedAtom);

        reorderBoundSkeleton(boundSkeleton, selectedAtom.getPoint());

        for (int ii = 0; ii < boundSkeleton.size(); ++ii) {
            // circular. If there are three skeleton. 0-1, 1-2, 2-0
            PointF a1p = boundSkeleton.get(ii).getPoint();
            PointF a2p = boundSkeleton.get((ii + 1) % boundSkeleton.size()).getPoint();

            mCandidates.add(Geometry.cwCenterOfAngle(a1p, a2p, selectedAtom.getPoint()));
        }
    }

    public AppendPointProducer(Atom a_atom) {
        int numberOfNeighbors = CompoundInspector.numberOfSkeletonAtoms(a_atom);

        // fill candidates position
        if (numberOfNeighbors == 0) {
            // attach to methane
            PointF p = new PointF();

            p.set(a_atom.getPoint());
            p.offset(0, -Configuration.BOND_LENGTH);

            for (int ii = 0; ii < 360 / 45; ++ii) {
                mCandidates.add(Geometry.cwRotate(p, a_atom.getPoint(), MathConstant.RADIAN_45 * ii));
            }
        } else if (numberOfNeighbors == 1) {
            // C---C---C(attach point) <-- C (append atom)
            // c*  b*  a* carbon
            Atom b_atom = a_atom.getSkeletonAtom();
            PointF a_atom_point = a_atom.getPoint(), b_atom_point = b_atom.getPoint();

            if (CompoundInspector.numberOfSkeletonAtoms(b_atom) > 1) {
                // this includes chain cases. In case >= 3. c_atom is assigned randomly
                Atom c_atom = b_atom.getSkeletonAtomExcept(a_atom);
                PointF c_atom_point = c_atom.getPoint();

                if (Geometry.cwAngle(c_atom_point, a_atom_point, b_atom_point) < MathConstant.RADIAN_180) {
                    // might be 120 degree
                    mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_240));
                    mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_120));
                } else {
                    mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_120));
                    mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_240));
                }
                mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_180));
            } else {
                // this is append carbon to ethane
                mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_120));
                mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_240));
                mCandidates.add(Geometry.cwRotate(b_atom_point, a_atom_point, MathConstant.RADIAN_180));
            }
        } else {
            fillCandidateWithCenterOfAngle(a_atom);
        }
    }

    public PointF firstPoint() {
        return mCandidates.get(0);
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
