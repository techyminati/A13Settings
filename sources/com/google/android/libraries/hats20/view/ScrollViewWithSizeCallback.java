package com.google.android.libraries.hats20.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
/* loaded from: classes.dex */
public class ScrollViewWithSizeCallback extends ScrollView {
    private OnHeightChangedListener onHeightChangedListener;

    /* loaded from: classes.dex */
    interface OnHeightChangedListener {
        void onHeightChanged(int i);
    }

    public ScrollViewWithSizeCallback(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setOnHeightChangedListener(OnHeightChangedListener onHeightChangedListener) {
        this.onHeightChangedListener = onHeightChangedListener;
    }

    @Override // android.widget.ScrollView, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        OnHeightChangedListener onHeightChangedListener = this.onHeightChangedListener;
        if (onHeightChangedListener != null && i4 != i2) {
            onHeightChangedListener.onHeightChanged(i2);
        }
    }
}
