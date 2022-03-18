package com.google.android.libraries.hats20.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
/* loaded from: classes.dex */
public final class StarRatingBar extends View {
    private AccessibilityManager accessibilityManager;
    private Bitmap emptyStarBitmap;
    private int numStars = 11;
    private OnRatingChangeListener onRatingChangeListener;
    private Paint paint;
    private int rating;
    private Bitmap starBitmap;

    /* loaded from: classes.dex */
    public interface OnRatingChangeListener {
        void onRatingChanged(int i);
    }

    public StarRatingBar(Context context) {
        super(context);
        init(context);
    }

    public StarRatingBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public StarRatingBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public StarRatingBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    private void init(Context context) {
        this.accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        this.starBitmap = BitmapFactory.decodeResource(context.getResources(), R$drawable.quantum_ic_star_black_24);
        this.emptyStarBitmap = BitmapFactory.decodeResource(context.getResources(), R$drawable.quantum_ic_star_border_grey600_24);
        Paint paint = new Paint(5);
        this.paint = paint;
        paint.setStyle(Paint.Style.FILL);
    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    public void setNumStars(int i) {
        if (i >= 3) {
            this.numStars = i;
            requestLayout();
            return;
        }
        throw new IllegalArgumentException("numStars must be at least 3");
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.resolveSize((this.numStars * this.starBitmap.getWidth()) + getPaddingLeft() + getPaddingRight(), i), View.resolveSize(this.starBitmap.getHeight() + getPaddingTop() + getPaddingBottom(), i2));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() != 0 && getHeight() != 0) {
            int i = 0;
            while (i < this.numStars) {
                canvas.drawBitmap(i < this.rating ? this.starBitmap : this.emptyStarBitmap, getStarXCoord(i), getPaddingTop(), this.paint);
                i++;
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action != 0 && action != 2) {
            return false;
        }
        setRating(getRatingAtTouchPoint(motionEvent.getX(), motionEvent.getY()));
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 21) {
            setRating(this.rating - 1);
            return true;
        } else if (i != 22) {
            return super.onKeyDown(i, keyEvent);
        } else {
            setRating(this.rating + 1);
            return true;
        }
    }

    private void setRating(int i) {
        if (i > 0 && i <= this.numStars && i != this.rating) {
            this.rating = i;
            invalidate();
            OnRatingChangeListener onRatingChangeListener = this.onRatingChangeListener;
            if (onRatingChangeListener != null) {
                onRatingChangeListener.onRatingChanged(this.rating);
            }
            if (this.accessibilityManager.isEnabled()) {
                sendAccessibilityEvent(4);
            }
        }
    }

    private float getStarXCoord(int i) {
        return getPaddingLeft() + (i * getDistanceBetweenStars());
    }

    private float getDistanceBetweenStars() {
        return (((getWidth() - getPaddingLeft()) - getPaddingRight()) - this.starBitmap.getWidth()) / (this.numStars - 1);
    }

    private int getRatingAtTouchPoint(float f, float f2) {
        float distanceBetweenStars = getDistanceBetweenStars();
        int i = 1;
        for (float paddingLeft = getPaddingLeft() + (this.starBitmap.getWidth() / 2.0f) + (distanceBetweenStars / 2.0f); paddingLeft < f && i < this.numStars; paddingLeft += distanceBetweenStars) {
            i++;
        }
        return i;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.numStars = this.numStars;
        savedState.rating = this.rating;
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.numStars = savedState.numStars;
        this.rating = savedState.rating;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.google.android.libraries.hats20.ui.StarRatingBar.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int numStars;
        int rating;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.numStars = parcel.readInt();
            this.rating = parcel.readInt();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.numStars);
            parcel.writeInt(this.rating);
        }
    }
}
