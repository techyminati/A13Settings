package com.google.android.libraries.material.autoresizetext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.widget.TextView;
/* loaded from: classes.dex */
public class AutoResizeTextView extends TextView {
    private int maxLines;
    private float maxTextSize;
    private int maxWidth;
    private final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    private final RectF availableSpaceRect = new RectF();
    private final SparseIntArray textSizesCache = new SparseIntArray();
    private final TextPaint textPaint = new TextPaint();
    private int resizeStepUnit = 0;
    private float minTextSize = 16.0f;
    private float lineSpacingMultiplier = 1.0f;
    private float lineSpacingExtra = 0.0f;

    public AutoResizeTextView(Context context) {
        super(context, null, 0);
        initialize(context, null, 0, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        initialize(context, attributeSet, 0, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context, attributeSet, i, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context, attributeSet, i, i2);
    }

    private void initialize(Context context, AttributeSet attributeSet, int i, int i2) {
        readAttrs(context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.AutoResizeTextView, i, i2));
        this.textPaint.set(getPaint());
    }

    @Override // android.widget.TextView
    public final int getMaxLines() {
        return super.getMaxLines();
    }

    @Override // android.widget.TextView
    public final void setMaxLines(int i) {
        super.setMaxLines(i);
        this.maxLines = i;
    }

    @Override // android.widget.TextView
    public final float getLineSpacingMultiplier() {
        return super.getLineSpacingMultiplier();
    }

    @Override // android.widget.TextView
    public final float getLineSpacingExtra() {
        return super.getLineSpacingExtra();
    }

    @Override // android.widget.TextView
    public final void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this.lineSpacingMultiplier = f2;
        this.lineSpacingExtra = f;
    }

    @Override // android.widget.TextView
    public final void setTextSize(int i, float f) {
        float applyDimension = TypedValue.applyDimension(i, f, this.displayMetrics);
        if (this.maxTextSize != applyDimension) {
            this.maxTextSize = applyDimension;
            this.textSizesCache.clear();
            requestLayout();
        }
    }

    public final void setMinTextSize(int i, float f) {
        float applyDimension = TypedValue.applyDimension(i, f, this.displayMetrics);
        if (this.minTextSize != applyDimension) {
            this.minTextSize = applyDimension;
            this.textSizesCache.clear();
            requestLayout();
        }
    }

    public final void setResizeStepUnit(int i) {
        if (this.resizeStepUnit != i) {
            this.resizeStepUnit = i;
            requestLayout();
        }
    }

    private void readAttrs(TypedArray typedArray) {
        this.resizeStepUnit = typedArray.getInt(R$styleable.AutoResizeTextView_autoResizeText_resizeStepUnit, 0);
        this.minTextSize = (int) typedArray.getDimension(R$styleable.AutoResizeTextView_autoResizeText_minTextSize, 16.0f);
        this.maxTextSize = (int) getTextSize();
    }

    private void adjustTextSize() {
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        int measuredHeight = (getMeasuredHeight() - getPaddingBottom()) - getPaddingTop();
        if (measuredWidth > 0 && measuredHeight > 0) {
            this.maxWidth = measuredWidth;
            RectF rectF = this.availableSpaceRect;
            rectF.right = measuredWidth;
            rectF.bottom = measuredHeight;
            super.setTextSize(this.resizeStepUnit, computeTextSize((int) Math.ceil(convertToResizeStepUnits(this.minTextSize)), (int) Math.floor(convertToResizeStepUnits(this.maxTextSize)), this.availableSpaceRect));
        }
    }

    private boolean suggestedSizeFitsInSpace(float f, RectF rectF) {
        this.textPaint.setTextSize(f);
        String charSequence = getText().toString();
        int maxLines = getMaxLines();
        if (maxLines == 1) {
            return this.textPaint.getFontSpacing() <= rectF.bottom && this.textPaint.measureText(charSequence) <= rectF.right;
        }
        StaticLayout staticLayout = new StaticLayout(charSequence, this.textPaint, this.maxWidth, Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), true);
        return (maxLines == -1 || staticLayout.getLineCount() <= maxLines) && ((float) staticLayout.getHeight()) <= rectF.bottom;
    }

    private float computeTextSize(int i, int i2, RectF rectF) {
        CharSequence text = getText();
        if (text != null && this.textSizesCache.get(text.hashCode()) != 0) {
            return this.textSizesCache.get(text.hashCode());
        }
        int binarySearchSizes = binarySearchSizes(i, i2, rectF);
        this.textSizesCache.put(text == null ? 0 : text.hashCode(), binarySearchSizes);
        return binarySearchSizes;
    }

    private int binarySearchSizes(int i, int i2, RectF rectF) {
        int i3 = i + 1;
        while (i3 <= i2) {
            int i4 = (i3 + i2) / 2;
            if (suggestedSizeFitsInSpace(TypedValue.applyDimension(this.resizeStepUnit, i4, this.displayMetrics), rectF)) {
                i3 = i4 + 1;
                i = i3;
            } else {
                i = i4 - 1;
                i2 = i;
            }
        }
        return i;
    }

    private float convertToResizeStepUnits(float f) {
        return f * (1.0f / TypedValue.applyDimension(this.resizeStepUnit, 1.0f, this.displayMetrics));
    }

    @Override // android.widget.TextView
    protected final void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        adjustTextSize();
    }

    @Override // android.view.View
    protected final void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            this.textSizesCache.clear();
            adjustTextSize();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected final void onMeasure(int i, int i2) {
        adjustTextSize();
        super.onMeasure(i, i2);
    }
}
