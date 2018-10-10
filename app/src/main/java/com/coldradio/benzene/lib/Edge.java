package com.coldradio.benzene.lib;

import com.coldradio.benzene.compound.Atom;

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
}
