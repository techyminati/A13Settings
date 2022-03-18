package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.window.R;
/* loaded from: classes.dex */
public class MessageBubbleBackground extends LinearLayout {
    private final int mSnapWidthPixels;

    public MessageBubbleBackground(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSnapWidthPixels = context.getResources().getDimensionPixelSize(R.dimen.conversation_bubble_width_snap);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i) - paddingLeft, (int) (Math.ceil((getMeasuredWidth() - paddingLeft) / this.mSnapWidthPixels) * this.mSnapWidthPixels)) + paddingLeft, 1073741824), i2);
    }
}
