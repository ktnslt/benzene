package com.coldradio.benzene.compound;

import android.graphics.PointF;

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
        PointF p1 = first.getPoint(), p2 = second.getPoint();

        return new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
}
