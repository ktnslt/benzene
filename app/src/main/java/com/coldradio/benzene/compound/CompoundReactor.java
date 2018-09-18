package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.util.Pair;

import java.util.List;

public class CompoundReactor {
    private Pair<Compound, Atom> mSynthesisSource = null;

    private Pair<Compound, Atom> selectSource(PointF point, List<Compound> compounds) {
        Compound selectedCompound = null;
        Atom selectedAtom = null;

        for (Compound compound : compounds) {
            selectedAtom = compound.selectAtom(point);
            if (selectedAtom != null) {
                selectedCompound = compound;
                break;
            }
        }

        if(selectedAtom != null) {
            return new Pair<>(selectedCompound, selectedAtom);
        } else {
            return null;
        }
    }

    private void synthesis(Pair<Compound, Atom> source, Pair<Compound, Atom> destination) {
        if (source.first != destination.first) {
            destination.first.merge(source.first);
            destination.second.singleBond(source.second);
        } else {    // this will be cyclic
            destination.second.singleBond(source.second);
        }

        mSynthesisSource = null;
    }

    public boolean synthesis(PointF point, List<Compound> compounds) {
        Pair<Compound, Atom> selectedSource = selectSource(point, compounds);

        if (mSynthesisSource == null) {
            mSynthesisSource = selectedSource;
        } else {
            if (selectedSource == null) {
                mSynthesisSource = null;
            } else {
                synthesis(mSynthesisSource, selectedSource);
            }
        }
        return true;
    }

    public PointF getSynthesisSourcePoint() {
        if (mSynthesisSource != null) {
            return mSynthesisSource.second.getPoint();
        }
        return null;
    }
}
