package com.google.android.libraries.hats20.view;

import android.graphics.Point;
import android.view.View;
import android.view.ViewTreeObserver;
/* loaded from: classes.dex */
public class FragmentViewDelegate implements ViewTreeObserver.OnGlobalLayoutListener {
    private View fragmentView;
    private MeasurementSurrogate measurementSurrogate;

    /* loaded from: classes.dex */
    public interface MeasurementSurrogate {
        Point getMeasureSpecs();

        void onFragmentContentMeasurement(int i, int i2);
    }

    public void watch(MeasurementSurrogate measurementSurrogate, View view) {
        this.measurementSurrogate = measurementSurrogate;
        this.fragmentView = view;
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void cleanUp() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        this.fragmentView = null;
        this.measurementSurrogate = null;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        Point measureSpecs = this.measurementSurrogate.getMeasureSpecs();
        this.fragmentView.measure(measureSpecs.x, measureSpecs.y);
        this.measurementSurrogate.onFragmentContentMeasurement(this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
        cleanUp();
    }
}
