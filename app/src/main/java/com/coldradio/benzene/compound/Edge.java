package com.coldradio.benzene.compound;

import android.graphics.PointF;

import com.coldradio.benzene.util.Geometry;

public class Edge {
    public final Atom first;
    public final Atom second;

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
}
