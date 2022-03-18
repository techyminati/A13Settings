package com.google.android.settings.biometrics.face.anim;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.ImageView;
import androidx.window.R;
import com.google.android.settings.biometrics.face.Debouncer;
import com.google.android.settings.biometrics.face.FaceEnrollSidecar;
import com.google.android.settings.biometrics.face.FaceUtils;
import com.google.android.settings.biometrics.face.Utils;
/* loaded from: classes2.dex */
public abstract class FaceEnrollAnimationBase extends Drawable implements FaceEnrollSidecar.Listener {
    private static final AudioAttributes SONIFICATION_AUDIO_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private boolean mCenterAcquired;
    private final Paint mCircleCutoutPaint;
    private final Context mContext;
    private final Debouncer mDebouncer;
    private final ImageView mFaceIcon;
    private final FaceOutlineIndicatorController mFaceOutlineIndicatorController;
    private boolean mFinishing;
    private boolean mFromSetupWizard;
    private Bitmap mInverseCutoutBitmap;
    private final AnimationListener mListener;
    private final Paint mScrimPaint;
    private final Paint mSquarePaint;
    private TimeAnimator mTimeAnimator;
    private final Vibrator mVibrator;
    private int mFOVState = 2;
    private final VibrationEffect mVibrationEffect = VibrationEffect.get(2);

    /* loaded from: classes2.dex */
    public interface AnimationListener {
        void clearHelp();

        void onEnrollAnimationFinished();

        void onEnrollAnimationStarted();

        void showHelp(CharSequence charSequence);
    }

    protected void bucketAcquiredWhileScrimShowing(int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isBucket(int i) {
        return i >= 1101 && i <= 1125;
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    protected void update(long j, long j2) {
    }

    public FaceEnrollAnimationBase(Context context, AnimationListener animationListener, ImageView imageView, boolean z) {
        this.mContext = context;
        this.mListener = animationListener;
        this.mFromSetupWizard = z;
        this.mVibrator = (Vibrator) context.getSystemService(Vibrator.class);
        Paint paint = new Paint();
        this.mSquarePaint = paint;
        paint.setColor(-1);
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mCircleCutoutPaint = paint2;
        paint2.setColor(0);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint2.setAntiAlias(true);
        Paint paint3 = new Paint();
        this.mScrimPaint = paint3;
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setColor(0);
        ImageView imageView2 = (ImageView) ((Activity) context).findViewById(R.id.face_smiley);
        this.mFaceIcon = imageView2;
        imageView2.setImageDrawable(context.getDrawable(R.drawable.face_smiley));
        this.mFaceOutlineIndicatorController = new FaceOutlineIndicatorController(context, imageView);
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.append(1, 6);
        sparseIntArray.append(2, 10);
        sparseIntArray.append(3, 10);
        this.mDebouncer = new Debouncer(sparseIntArray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void vibrate() {
        this.mVibrator.vibrate(this.mVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AnimationListener getListener() {
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isCenterAcquired() {
        return this.mCenterAcquired;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUserEnterGood() {
        Log.i("FaceEnroll/AnimationBase", "onUserEnterGood");
        getListener().clearHelp();
        this.mFaceOutlineIndicatorController.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUserLeaveGood(CharSequence charSequence) {
        Log.i("FaceEnroll/AnimationBase", "onUserLeaveGood");
        getListener().showHelp(charSequence);
        this.mFaceOutlineIndicatorController.show();
    }

    public void onFirstFrameReceived() {
        onUserLeaveGood(null);
        this.mFOVState = 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect rect) {
        if (this.mTimeAnimator == null) {
            TimeAnimator timeAnimator = new TimeAnimator();
            this.mTimeAnimator = timeAnimator;
            timeAnimator.setTimeListener(new TimeAnimator.TimeListener() { // from class: com.google.android.settings.biometrics.face.anim.FaceEnrollAnimationBase$$ExternalSyntheticLambda0
                @Override // android.animation.TimeAnimator.TimeListener
                public final void onTimeUpdate(TimeAnimator timeAnimator2, long j, long j2) {
                    FaceEnrollAnimationBase.this.lambda$onBoundsChange$0(timeAnimator2, j, j2);
                }
            });
            this.mTimeAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBoundsChange$0(TimeAnimator timeAnimator, long j, long j2) {
        update(j, j2);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.save();
        int dpToPx = (int) Utils.dpToPx(this.mContext, 30);
        if (this.mInverseCutoutBitmap == null) {
            this.mInverseCutoutBitmap = Cutout.createCutoutBitmap(this.mContext, getBounds().width() + (dpToPx * 2), getBounds().width() / 2);
        }
        float f = -dpToPx;
        canvas.drawBitmap(this.mInverseCutoutBitmap, f, f, (Paint) null);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2, this.mScrimPaint);
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean outOfFOVScrimShowing() {
        return this.mFOVState != 1;
    }

    @Override // com.google.android.settings.biometrics.face.FaceEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        if (!this.mFinishing) {
            if (!isBucket(i) || this.mFOVState == 1) {
                if (i != 11) {
                    switch (i) {
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            break;
                        default:
                            switch (i) {
                                case 1126:
                                case 1127:
                                case 1128:
                                case 1129:
                                case 1130:
                                case 1131:
                                case 1132:
                                case 1133:
                                    handleOutOfFovState(3, charSequence);
                                    break;
                            }
                    }
                }
                handleOutOfFovState(2, charSequence);
            } else {
                this.mDebouncer.updateBuffer(1);
                if (this.mDebouncer.passesDebounce(1)) {
                    this.mFOVState = 1;
                    onUserEnterGood();
                } else if (!FaceUtils.isOneOfCenterBuckets(i)) {
                    bucketAcquiredWhileScrimShowing(i);
                }
            }
            if (!this.mCenterAcquired && FaceUtils.isOneOfCenterBuckets(i)) {
                this.mCenterAcquired = true;
            }
        }
    }

    private void handleOutOfFovState(int i, CharSequence charSequence) {
        this.mDebouncer.updateBuffer(i);
        if (this.mFOVState != i && this.mDebouncer.passesDebounce(i)) {
            onUserLeaveGood(charSequence);
            this.mFOVState = i;
        }
    }

    @Override // com.google.android.settings.biometrics.face.FaceEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        if (i2 == 0) {
            if (this.mFOVState != 1) {
                this.mFOVState = 1;
                onUserEnterGood();
            }
            this.mListener.onEnrollAnimationStarted();
            startFinishing();
            this.mListener.clearHelp();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startFinishing() {
        this.mFinishing = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFinishing() {
        return this.mFinishing;
    }
}
