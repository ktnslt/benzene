package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.util.Geometry;

public class Edge {
    public final Atom first;
    public final Atom second;

    private void wedge(Atom atom, Bond.BondAnnotation bondAnnotation) {
        if (atom == first) {
            atom.setBondAnnotation(second, bondAnnotation);
            second.setBondAnnotation(first, Bond.BondAnnotation.NONE);
        } else {
            atom.setBondAnnotation(first, bondAnnotation);
            first.setBondAnnotation(second, Bond.BondAnnotation.NONE);
        }
    }

    public Edge(Atom a1, Atom a2) {
        first = a1;
        second = a2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Edge))
            return false;

        if (this == o) return true;

        Edge that = (Edge)o;

        return (first == that.first && second == that.second) || (first == that.second && second == that.first);
    }

    public PointF center() {
        return Geometry.centerOfLine(first.getPoint(), second.getPoint());
    }

    public Atom atomInUpperDirection() {
        return (first.getPoint().x <= second.getPoint().x) ? first : second;
    }

    public void cycleBondAnnotation(boolean wedgeUp) {
        Bond.BondAnnotation firstBA = first.getBondAnnotation(second);
        Bond.BondAnnotation secondBA = second.getBondAnnotation(first);
        Bond.BondAnnotation upDown = wedgeUp ? Bond.BondAnnotation.WEDGE_UP : Bond.BondAnnotation.WEDGE_DOWN;

        // rotation order: NONE -> edge.first WEDGE_UP -> edge.second WEDGE_UP -> WAVY -> NONE
        if (firstBA == Bond.BondAnnotation.NONE && secondBA == Bond.BondAnnotation.NONE) {
            wedge(first, upDown);
        } else if (firstBA == upDown) {
            wedge(second, upDown);
        } else if (secondBA == upDown) {
            wedge(first, Bond.BondAnnotation.WAVY);
        } else {
            wedge(first, Bond.BondAnnotation.NONE);
        }
    }
}
