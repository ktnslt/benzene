package com.coldradio.benzene.compound;

public class CompoundFactory {
    public static Compound alkane(int carbonNumber, float x, float y) {
        Compound c = new ChainCompound(carbonNumber);

        c.offset(x, y);
        return c;
    }

    public static Compound cyclicAlkane(int carbonNumber, float x, float y) {
        Compound c = new CyclicCompound(carbonNumber);

        c.offset(x, y);
        return c;
    }

    public static Compound conjugatedCyclicAlkane(int carbonNumber, float x, float y) {
        Compound c = new ConjuatedCyclicCompound(carbonNumber);

        c.offset(x, y);;
        return c;
    }

    public static Compound propane(float x, float y) {
        return alkane(3, x, y);
    }

    public static Compound butane(float x, float y) {
        return alkane(4, x, y);
    }

    public static Compound benzene(float x, float y) {
        return conjugatedCyclicAlkane(6, x, y);
    }
}
