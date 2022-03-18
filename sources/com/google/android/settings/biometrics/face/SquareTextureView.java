package com.google.android.settings.biometrics.face;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
/* loaded from: classes2.dex */
public class SquareTextureView extends TextureView {
    public SquareTextureView(Context context) {
        this(context, null);
    }

    public SquareTextureView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SquareTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (size < size2) {
            setMeasuredDimension(size, size);
        } else {
            setMeasuredDimension(size2, size2);
        }
    }
}
