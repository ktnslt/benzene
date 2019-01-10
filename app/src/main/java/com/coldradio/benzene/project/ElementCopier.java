package com.coldradio.benzene.project;

import android.graphics.PointF;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;
import com.coldradio.benzene.compound.CompoundArranger;
import com.coldradio.benzene.compound.CompoundInspector;
import com.coldradio.benzene.util.ScreenInfo;

import java.util.ArrayList;
import java.util.List;

public class ElementCopier {
    private List<Compound> mCopiedCompound = new ArrayList<>();

    private void restoreBond(Compound compound, List<Atom> selectedAtoms) {
        // here selectedAtoms are assigned with AID that is synchronized in compound as well
        for (Atom atom : selectedAtoms) {
            for (Bond bond : atom.getBonds()) {
                Atom that_atom = bond.getBoundAtom();

                if (selectedAtoms.contains(that_atom)) {
                    compound.makeBond(atom.getAID(), that_atom.getAID(), bond.getBondType());
                }
            }
        }
    }

    public void copy(List<Atom> selectedAtoms) {
        Compound compound = new Compound();
        int aid = 0;

        for (Atom atom : selectedAtoms) {
            atom.setAID(aid++);
        }

        for (Atom atom : selectedAtoms) {
            compound.addAtom(atom.copy());
        }

        // restore the bond
        restoreBond(compound, selectedAtoms);

        mCopiedCompound = CompoundInspector.split(compound);
    }

    public void paste(Project project) {
        List<Compound> pastingCompoundList = new ArrayList<>();

        for (Compound compound : mCopiedCompound) {
            pastingCompoundList.add(compound.copy());
        }

        PointF centerOfCompounds = CompoundArranger.center(pastingCompoundList);
        PointF screenCenter = ScreenInfo.instance().centerPoint();

        for (Compound compound : pastingCompoundList) {
            compound.offset(screenCenter.x - centerOfCompounds.x, screenCenter.y - centerOfCompounds.y);
        }
        project.addCompounds(pastingCompoundList);
    }

    public boolean hasCopied() {
        return ! mCopiedCompound.isEmpty();
    }
}
