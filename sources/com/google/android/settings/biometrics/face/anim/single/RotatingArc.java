package com.google.android.settings.biometrics.face.anim.single;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
/* loaded from: classes2.dex */
public class RotatingArc {
    private float mAngle;
    private ValueAnimator mColorAnimator;
    private final int[] mColors;
    private final int mIndex;
    private final Paint mPaint;
    private float mRotateSpeed;
    private float mSweepAngle;

    public RotatingArc(int i, int i2, int[] iArr) {
        this.mIndex = i;
        this.mColors = iArr;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20.0f);
        paint.setColor(getColorForIndex(i));
        this.mAngle = (360 / i2) * i;
    }

    public void setSweepAngle(float f) {
        this.mSweepAngle = f;
    }

    public void setRotateSpeed(float f) {
        this.mRotateSpeed = f;
    }

    public int getColorForIndex(int i) {
        int[] iArr = this.mColors;
        return iArr[i % iArr.length];
    }

    public void update(long j, long j2) {
        this.mAngle += (this.mRotateSpeed * ((float) j2)) / 1000.0f;
    }

    public void draw(Canvas canvas) {
        float width = (canvas.getWidth() / 2) - (this.mPaint.getStrokeWidth() / 2.0f);
        canvas.drawArc((canvas.getWidth() / 2) - width, (canvas.getHeight() / 2) - width, (canvas.getWidth() / 2) + width, (canvas.getWidth() / 2) + width, this.mAngle, this.mSweepAngle, false, this.mPaint);
    }

    public void stopCurrentAnimation() {
        ValueAnimator valueAnimator = this.mColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void stopRotating(long j) {
        ValueAnimator ofArgb = ValueAnimator.ofArgb(this.mPaint.getColor(), 0);
        this.mColorAnimator = ofArgb;
        ofArgb.setDuration(j);
        this.mColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.settings.biometrics.face.anim.single.RotatingArc$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RotatingArc.this.lambda$stopRotating$0(valueAnimator);
            }
        });
        this.mColorAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopRotating$0(ValueAnimator valueAnimator) {
        this.mPaint.setColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    public void startRotating(long j) {
        ValueAnimator ofArgb = ValueAnimator.ofArgb(this.mPaint.getColor(), getColorForIndex(this.mIndex));
        this.mColorAnimator = ofArgb;
        ofArgb.setDuration(j);
        this.mColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.settings.biometrics.face.anim.single.RotatingArc$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RotatingArc.this.lambda$startRotating$1(valueAnimator);
            }
        });
        this.mColorAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startRotating$1(ValueAnimator valueAnimator) {
        this.mPaint.setColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    public void startFinishing(long j) {
        startRotating(j);
    }
}
