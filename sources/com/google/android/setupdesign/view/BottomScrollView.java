package com.google.android.setupdesign.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
/* loaded from: classes2.dex */
public class BottomScrollView extends ScrollView {
    private BottomScrollListener listener;
    private int scrollThreshold;
    private boolean requiringScroll = false;
    private final Runnable checkScrollRunnable = new Runnable() { // from class: com.google.android.setupdesign.view.BottomScrollView.1
        @Override // java.lang.Runnable
        public void run() {
            BottomScrollView.this.checkScroll();
        }
    };

    /* loaded from: classes2.dex */
    public interface BottomScrollListener {
        void onRequiresScroll();

        void onScrolledToBottom();
    }

    public BottomScrollView(Context context) {
        super(context);
    }

    public BottomScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BottomScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setBottomScrollListener(BottomScrollListener bottomScrollListener) {
        this.listener = bottomScrollListener;
    }

    public BottomScrollListener getBottomScrollListener() {
        return this.listener;
    }

    public int getScrollThreshold() {
        return this.scrollThreshold;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        View childAt = getChildAt(0);
        if (childAt != null) {
            this.scrollThreshold = Math.max(0, ((childAt.getMeasuredHeight() - i4) + i2) - getPaddingBottom());
        }
        if (i4 - i2 > 0) {
            post(this.checkScrollRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (i4 != i2) {
            checkScroll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkScroll() {
        if (this.listener == null) {
            return;
        }
        if (getScrollY() >= this.scrollThreshold) {
            this.listener.onScrolledToBottom();
        } else if (!this.requiringScroll) {
            this.requiringScroll = true;
            this.listener.onRequiresScroll();
        }
    }
}
