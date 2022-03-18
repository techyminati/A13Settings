package com.google.android.settings.gestures.assist;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.window.R;
/* loaded from: classes2.dex */
class IndicatorDrawable extends Drawable {
    private Context mContext;
    private float mProgress;
    private boolean mReversed;
    private long mTime;
    private TimeAnimator mTimeAnimator;
    private Paint mPaint = new Paint();
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.google.android.settings.gestures.assist.IndicatorDrawable.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                IndicatorDrawable.this.mTimeAnimator.start();
            } else if (i == 2) {
                IndicatorDrawable.this.mTimeAnimator.end();
            }
        }
    };

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public IndicatorDrawable(Context context, boolean z) {
        this.mContext = context;
        this.mReversed = z;
        TimeAnimator timeAnimator = new TimeAnimator();
        this.mTimeAnimator = timeAnimator;
        timeAnimator.setTimeListener(new TimeAnimator.TimeListener() { // from class: com.google.android.settings.gestures.assist.IndicatorDrawable.2
            @Override // android.animation.TimeAnimator.TimeListener
            public void onTimeUpdate(TimeAnimator timeAnimator2, long j, long j2) {
                IndicatorDrawable.this.mTime = j;
                if (j >= 150) {
                    timeAnimator2.end();
                }
                IndicatorDrawable.this.invalidateSelf();
            }
        });
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mContext.getResources().getColor(R.color.active_edge_indicator));
        this.mPaint.setAlpha(63);
        int height = canvas.getHeight() / 2;
        int height2 = canvas.getHeight();
        Path path = new Path();
        if (this.mReversed) {
            path.moveTo(canvas.getWidth(), height);
        } else {
            path.moveTo(0.0f, height);
        }
        float f = canvas.getWidth() * this.mProgress;
        if (this.mTimeAnimator.isRunning()) {
            float f2 = 1.0f - (((float) this.mTime) / 150.0f);
            this.mPaint.setAlpha((int) (63.0f * f2));
            f = canvas.getWidth() * f2;
        }
        if (this.mReversed) {
            float f3 = height2;
            path.cubicTo(canvas.getWidth() - f, height + 150.0f, canvas.getWidth() - f, f3 - 150.0f, canvas.getWidth(), f3);
        } else {
            float f4 = height2;
            path.cubicTo(f, height + 150.0f, f, f4 - 150.0f, 0.0f, f4);
        }
        canvas.drawPath(path, this.mPaint);
    }

    public void onGestureProgress(float f) {
        this.mHandler.sendEmptyMessage(2);
        this.mProgress = f;
        invalidateSelf();
    }

    public void onGestureDetected() {
        this.mProgress = 0.0f;
        this.mHandler.sendEmptyMessage(1);
    }
}
