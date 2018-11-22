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

    private boolean mShowElement;
    private UnsharedElectron[] mUnsharedElectron = new UnsharedElectron[4];
    private int mCharge = 0;
    private Marker mChargeAsCircle = Marker.NONE;
    private Marker mStarMarker = Marker.NONE;

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

    public boolean getShowElement() {
        return mShowElement;
    }

    public void setShowElement(boolean showElement) {
        mShowElement = showElement;
    }

    public AtomDecoration copy() {
        AtomDecoration copied = new AtomDecoration();

        copied.mShowElement = mShowElement;
        for (int ii = 0; ii < mUnsharedElectron.length; ++ii) {
            copied.mUnsharedElectron[ii] = mUnsharedElectron[ii];
        }
        copied.mCharge = mCharge;
        copied.mChargeAsCircle = mChargeAsCircle;
        copied.mStarMarker = mStarMarker;

        return copied;
    }
}
