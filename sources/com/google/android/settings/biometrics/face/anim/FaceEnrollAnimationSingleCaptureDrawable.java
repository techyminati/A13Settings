package com.google.android.settings.biometrics.face.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.os.Handler;
import android.widget.ImageView;
import com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase;
import com.google.android.settings.biometrics.face.anim.single.ArcCollection;
/* loaded from: classes2.dex */
public class FaceEnrollAnimationSingleCaptureDrawable extends FaceEnrollAnimationBase {
    private final Handler mHandler;
    private final ArcCollection mRotatingArcs;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // com.google.android.settings.biometrics.face.FaceEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public FaceEnrollAnimationSingleCaptureDrawable(Context context, FaceEnrollAnimationBase.AnimationListener animationListener, ImageView imageView, boolean z) {
        super(context, animationListener, imageView, z);
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mRotatingArcs = new ArcCollection(context, handler);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase
    public void startFinishing() {
        super.startFinishing();
        this.mRotatingArcs.startFinishing(new Runnable() { // from class: com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationSingleCaptureDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FaceEnrollAnimationSingleCaptureDrawable.this.lambda$startFinishing$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startFinishing$0() {
        getListener().onEnrollAnimationFinished();
    }

    @Override // com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase
    protected void update(long j, long j2) {
        this.mRotatingArcs.update(j, j2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase
    public void onUserLeaveGood(CharSequence charSequence) {
        super.onUserLeaveGood(charSequence);
        this.mRotatingArcs.stopRotating();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase
    public void onUserEnterGood() {
        super.onUserEnterGood();
        this.mRotatingArcs.startRotating();
    }

    @Override // com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase, com.google.android.settings.biometrics.face.FaceEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        super.onEnrollmentProgressChange(i, i2);
        if (i2 == 0) {
            vibrate();
        }
    }

    @Override // com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.mRotatingArcs.draw(canvas);
    }
}
