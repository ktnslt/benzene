package com.coldradio.benzene.compound;

import android.graphics.PointF;
import android.util.Pair;

import com.coldradio.benzene.geometry.Geometry;
import com.coldradio.benzene.project.Project;

import java.util.List;

public class CompoundReactor {
    private Pair<Compound, Atom> mSynthesisSource = null;

    private Pair<Compound, Atom> selectSource(float x, float y, List<Compound> compounds) {
        Compound selectedCompound = null;
        Atom selectedAtom = null;

        for (Compound compound : compounds) {
            selectedAtom = Geometry.selectAtom(x, y, compound);
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

        }

        mSynthesisSource = null;
    }

    public boolean synthesis(float x, float y, List<Compound> compounds) {
        Pair<Compound, Atom> selectedSource = selectSource(x, y, compounds);

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
