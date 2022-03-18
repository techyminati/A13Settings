package com.google.android.settings.gestures.columbus;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import androidx.window.R;
import com.google.android.setupdesign.view.Illustration;
/* loaded from: classes2.dex */
public class ColumbusEnrollingIllustration extends Illustration {
    private Animator mAnimator;
    private float mGestureValue = 0.0f;
    private final int mInset;
    private final Paint mPaint;

    public ColumbusEnrollingIllustration(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.columbus_enroll_illustration_stroke_width);
        int color = getContext().getColor(R.color.columbus_highlight);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dimensionPixelSize);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);
        this.mInset = dimensionPixelSize / 2;
    }

    @Override // com.google.android.setupdesign.view.Illustration, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = this.mInset;
        canvas.drawArc(i, i, getWidth() - this.mInset, getHeight() - this.mInset, 270.0f, this.mGestureValue * 180.0f, false, this.mPaint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setGestureCount(final int i, final Runnable runnable) {
        Animator animator = this.mAnimator;
        if (animator != null) {
            animator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.mGestureValue, i);
        ofFloat.setDuration(500L);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusEnrollingIllustration$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ColumbusEnrollingIllustration.this.lambda$setGestureCount$0(valueAnimator);
            }
        });
        ofFloat.addListener(new Animator.AnimatorListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusEnrollingIllustration.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator2) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator2) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator2) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                ColumbusEnrollingIllustration.this.mGestureValue = i;
                if (i == 2) {
                    ColumbusEnrollingIllustration.this.setBackgroundResource(R.drawable.ic_icon_check);
                }
                ColumbusEnrollingIllustration.this.mAnimator = null;
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
        this.mAnimator = ofFloat;
        ofFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setGestureCount$0(ValueAnimator valueAnimator) {
        this.mGestureValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }
}
