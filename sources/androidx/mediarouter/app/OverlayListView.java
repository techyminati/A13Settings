package androidx.mediarouter.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
final class OverlayListView extends ListView {
    private final List<OverlayObject> mOverlayObjects = new ArrayList();

    public OverlayListView(Context context) {
        super(context);
    }

    public OverlayListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OverlayListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void addOverlayObject(OverlayObject overlayObject) {
        this.mOverlayObjects.add(overlayObject);
    }

    public void startAnimationAll() {
        for (OverlayObject overlayObject : this.mOverlayObjects) {
            if (!overlayObject.isAnimationStarted()) {
                overlayObject.startAnimation(getDrawingTime());
            }
        }
    }

    public void stopAnimationAll() {
        for (OverlayObject overlayObject : this.mOverlayObjects) {
            overlayObject.stopAnimation();
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mOverlayObjects.size() > 0) {
            Iterator<OverlayObject> it = this.mOverlayObjects.iterator();
            while (it.hasNext()) {
                OverlayObject next = it.next();
                BitmapDrawable bitmapDrawable = next.getBitmapDrawable();
                if (bitmapDrawable != null) {
                    bitmapDrawable.draw(canvas);
                }
                if (!next.update(getDrawingTime())) {
                    it.remove();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class OverlayObject {
        private BitmapDrawable mBitmap;
        private Rect mCurrentBounds;
        private int mDeltaY;
        private long mDuration;
        private Interpolator mInterpolator;
        private boolean mIsAnimationEnded;
        private boolean mIsAnimationStarted;
        private OnAnimationEndListener mListener;
        private Rect mStartRect;
        private long mStartTime;
        private float mCurrentAlpha = 1.0f;
        private float mStartAlpha = 1.0f;
        private float mEndAlpha = 1.0f;

        /* loaded from: classes.dex */
        public interface OnAnimationEndListener {
            void onAnimationEnd();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public OverlayObject(BitmapDrawable bitmapDrawable, Rect rect) {
            this.mBitmap = bitmapDrawable;
            this.mStartRect = rect;
            this.mCurrentBounds = new Rect(rect);
            BitmapDrawable bitmapDrawable2 = this.mBitmap;
            if (bitmapDrawable2 != null) {
                bitmapDrawable2.setAlpha((int) (this.mCurrentAlpha * 255.0f));
                this.mBitmap.setBounds(this.mCurrentBounds);
            }
        }

        public BitmapDrawable getBitmapDrawable() {
            return this.mBitmap;
        }

        public boolean isAnimationStarted() {
            return this.mIsAnimationStarted;
        }

        public OverlayObject setAlphaAnimation(float f, float f2) {
            this.mStartAlpha = f;
            this.mEndAlpha = f2;
            return this;
        }

        public OverlayObject setTranslateYAnimation(int i) {
            this.mDeltaY = i;
            return this;
        }

        public OverlayObject setDuration(long j) {
            this.mDuration = j;
            return this;
        }

        public OverlayObject setInterpolator(Interpolator interpolator) {
            this.mInterpolator = interpolator;
            return this;
        }

        public OverlayObject setAnimationEndListener(OnAnimationEndListener onAnimationEndListener) {
            this.mListener = onAnimationEndListener;
            return this;
        }

        public void startAnimation(long j) {
            this.mStartTime = j;
            this.mIsAnimationStarted = true;
        }

        public void stopAnimation() {
            this.mIsAnimationStarted = true;
            this.mIsAnimationEnded = true;
            OnAnimationEndListener onAnimationEndListener = this.mListener;
            if (onAnimationEndListener != null) {
                onAnimationEndListener.onAnimationEnd();
            }
        }

        public boolean update(long j) {
            if (this.mIsAnimationEnded) {
                return false;
            }
            float f = 0.0f;
            f = Math.max(0.0f, Math.min(1.0f, ((float) (j - this.mStartTime)) / ((float) this.mDuration)));
            if (this.mIsAnimationStarted) {
            }
            Interpolator interpolator = this.mInterpolator;
            float interpolation = interpolator == null ? f : interpolator.getInterpolation(f);
            int i = (int) (this.mDeltaY * interpolation);
            Rect rect = this.mCurrentBounds;
            Rect rect2 = this.mStartRect;
            rect.top = rect2.top + i;
            rect.bottom = rect2.bottom + i;
            float f2 = this.mStartAlpha;
            float f3 = f2 + ((this.mEndAlpha - f2) * interpolation);
            this.mCurrentAlpha = f3;
            BitmapDrawable bitmapDrawable = this.mBitmap;
            if (!(bitmapDrawable == null || rect == null)) {
                bitmapDrawable.setAlpha((int) (f3 * 255.0f));
                this.mBitmap.setBounds(this.mCurrentBounds);
            }
            if (this.mIsAnimationStarted && f >= 1.0f) {
                this.mIsAnimationEnded = true;
                OnAnimationEndListener onAnimationEndListener = this.mListener;
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd();
                }
            }
            return !this.mIsAnimationEnded;
        }
    }
}
