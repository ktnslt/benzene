package com.coldradio.benzene.lib;

import com.coldradio.benzene.compound.Atom;
import com.coldradio.benzene.compound.Bond;
import com.coldradio.benzene.compound.Compound;

import java.util.HashSet;

public class TreeTraveler {
    public interface IEdgeVisitor {
        boolean visit(Atom a1, Atom a2, Object... args);
    }

    public interface IAtomVisitor {
        boolean visit(Atom atom, Object... args);
    }

    private static Edge doEdgeRecursive(Atom parent, IEdgeVisitor edgeVisitor, HashSet<String> visitedEdge, Object... args) {
        for (Bond bond : parent.getBonds()) {
            /* TODO: since points are used as a key, if the two atoms have exact the same point, this DO NOT work properly, though it is quite rare.
                I have tried to use hashCode of the atom as a key, but it doesn't work.
            */
            Atom child = bond.getBoundAtom();

            if (!visitedEdge.contains(parent.getPoint().toString() + child.getPoint())
                    && !visitedEdge.contains(child.getPoint().toString() + parent.getPoint())) {
                // the edge is not visited
                if (edgeVisitor.visit(parent, child, args)) {
                    return new Edge(parent, child);
                }
                visitedEdge.add(parent.getPoint().toString() + child.getPoint());

                Edge ret = doEdgeRecursive(child, edgeVisitor, visitedEdge, args);
                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    private static Atom doAtomRecursive(Atom atom, IAtomVisitor atomVisitor, HashSet<Atom> visitedAtom, Object[] args) {
        for (Bond bond : atom.getBonds()) {
            Atom next = bond.getBoundAtom();

            if (!visitedAtom.contains(next)) {
                if (atomVisitor.visit(next, args)) {
                    return next;
                }
                visitedAtom.add(next);

                Atom ret = doAtomRecursive(next, atomVisitor, visitedAtom, args);

                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    public static Edge returnFirstEdge(IEdgeVisitor edgeVisitor, Compound compound, Object... args) {
        if (compound.size() >= 2) {
            HashSet<String> visitedEdge = new HashSet<>();

            return doEdgeRecursive(compound.getAtoms().get(0), edgeVisitor, visitedEdge, args);
        } else {
            return null;
        }
    }

    public static Atom returnFirstAtom(IAtomVisitor atomVisitor, Compound compound, Object... args) {
        for (Atom atom : compound.getAtoms()) {
            if (atomVisitor.visit(atom, args)) {
                return atom;
            }
        }
        return null;
    }

    public static Atom returnFirstAtom(IAtomVisitor atomVisitor, Atom atom, Object... args) {
        HashSet<Atom> visitedAtom = new HashSet<>();

        visitedAtom.add(atom);
        return doAtomRecursive(atom, atomVisitor, visitedAtom, args);
    }

    public static boolean isCyclicBond(final Atom a1, Atom a2) {
        return null != returnFirstAtom(new IAtomVisitor() {
            @Override
            public boolean visit(Atom atom, Object... args) {
                return atom == a1;
            }
        }, a2);
    }
}