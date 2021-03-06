package com.coldradio.benzene.compound;

public class AtomDecoration {
    public enum Direction {
        TOP, BOTTOM, LEFT, RIGHT
    }

    public enum Marker {
        NONE, MARKER_1, MARKER_2, MARKER_3, MARKER_4, MARKER_5, MARKER_6, MARKER_7, MARKER_8
    }

    public enum UnsharedElectron {
        NONE, SINGLE, DOUBLE
    }

    private boolean mShowElementName = true;
    private UnsharedElectron[] mUnsharedElectron = new UnsharedElectron[4];
    private int mCharge = 0;
    private Marker mChargeAsCircle = Marker.NONE;
    private Marker mStarMarker = Marker.NONE;
    private boolean mLettering;

    public AtomDecoration() {
        for (int ii = 0; ii < mUnsharedElectron.length; ++ii) {
            mUnsharedElectron[ii] = UnsharedElectron.NONE;
        }
    }

    public void setMarker(Marker marker) {
        mStarMarker = marker;
    }

    public Marker getMarker() {
        return mStarMarker;
    }

    public void setChargeAsCircle(Marker chargeAsCircle) {
        mChargeAsCircle = chargeAsCircle;
    }

    public Marker getChargeAsCircle() {
        return mChargeAsCircle;
    }

    public UnsharedElectron getUnsharedElectron(Direction direction) {
        return mUnsharedElectron[direction.ordinal()];
    }

    public void setUnsharedElectron(Direction direction, UnsharedElectron unsharedElectron) {
        mUnsharedElectron[direction.ordinal()] = unsharedElectron;
    }

    public void setCharge(int charge) {
        mCharge = charge;
    }

    public int getCharge() {
        return mCharge;
    }

    public boolean getShowElementName() {
        return mShowElementName;
    }

    public void setShowElementName(boolean show) {
        mShowElementName = show;
    }

    public void lettering(boolean on) {
        mLettering = on;
    }

    public boolean isLettering() {
        return mLettering;
    }

    public AtomDecoration copy() {
        AtomDecoration copied = new AtomDecoration();

        copied.mShowElementName = mShowElementName;
        for (int ii = 0; ii < mUnsharedElectron.length; ++ii) {
            copied.mUnsharedElectron[ii] = mUnsharedElectron[ii];
        }
        copied.mCharge = mCharge;
        copied.mChargeAsCircle = mChargeAsCircle;
        copied.mStarMarker = mStarMarker;
        copied.mLettering = mLettering;

        return copied;
    }
}
