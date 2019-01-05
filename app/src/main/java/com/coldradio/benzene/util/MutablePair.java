package com.coldradio.benzene.util;

import android.util.Pair;

public class MutablePair<F, S> {
    public F first;
    public S second;

    public static <A, B> MutablePair <A, B> create(A a, B b) {
        return new MutablePair<A, B>(a, b);
    }

    public MutablePair(F f, S s) {
        first = f;
        second = s;
    }

    public Pair<F, S> toPair() {
        return Pair.create(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutablePair)) {
            return false;
        }
        MutablePair<?, ?> p = (MutablePair<?, ?>) o;
        return p.first == this.first && p.second == this.second;
    }
}
