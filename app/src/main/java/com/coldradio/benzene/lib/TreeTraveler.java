package com.coldradio.benzene.lib;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;

import java.util.HashSet;
import java.util.List;

public class TreeTraveler {
    public interface IEdgeVisitor {
        boolean visit(Atom a1, Atom a2, Object... args);
    }

    private static Atom[] doRecursive(Atom parent, IEdgeVisitor edgeVisitor, HashSet<String> visitedEdge, Object... args) {
        for (Bond bond : parent.getBonds()) {
            /*  TODO: since points are used as a key, if the two atoms have exact the same point, this DO NOT work properly, though it is quite rare.
                I have tried to use hashCode of the atom as a key, but it doesn't work.
            */
            Atom child = bond.getBoundAtom();

            if (!visitedEdge.contains(parent.getPoint().toString() + child.getPoint())
                    && !visitedEdge.contains(child.getPoint().toString() + parent.getPoint())) {
                // the edge is not visited
                if (edgeVisitor.visit(parent, child, args)) {
                    return new Atom[]{parent, child};
                }
                visitedEdge.add(parent.getPoint().toString() + child.getPoint());

                Atom[] ret = doRecursive(child, edgeVisitor, visitedEdge, args);
                if(ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    public static Atom[] returnFirstEdge(IEdgeVisitor edgeVisitor, Compound compound, Object... args) {
        if(compound.size() >= 2) {
            HashSet<String> visitedEdge = new HashSet<>();

            return doRecursive(compound.getAtoms().get(0), edgeVisitor, visitedEdge, args);
        } else {
            return null;
        }
    }
}
