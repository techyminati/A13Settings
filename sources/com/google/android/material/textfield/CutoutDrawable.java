package com.google.android.material.textfield;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
/* loaded from: classes2.dex */
class CutoutDrawable extends MaterialShapeDrawable {
    private final RectF cutoutBounds;
    private final Paint cutoutPaint;
    private int savedLayer;

    CutoutDrawable() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CutoutDrawable(ShapeAppearanceModel shapeAppearanceModel) {
        super(shapeAppearanceModel == null ? new ShapeAppearanceModel() : shapeAppearanceModel);
        this.cutoutPaint = new Paint(1);
        setPaintStyles();
        this.cutoutBounds = new RectF();
    }

    private void setPaintStyles() {
        this.cutoutPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.cutoutPaint.setColor(-1);
        this.cutoutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasCutout() {
        return !this.cutoutBounds.isEmpty();
    }

    void setCutout(float f, float f2, float f3, float f4) {
        RectF rectF = this.cutoutBounds;
        if (f != rectF.left || f2 != rectF.top || f3 != rectF.right || f4 != rectF.bottom) {
            rectF.set(f, f2, f3, f4);
            invalidateSelf();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCutout(RectF rectF) {
        setCutout(rectF.left, rectF.top, rectF.right, rectF.bottom);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCutout() {
        setCutout(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override // com.google.android.material.shape.MaterialShapeDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        preDraw(canvas);
        super.draw(canvas);
        postDraw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.shape.MaterialShapeDrawable
    public void drawStrokeShape(Canvas canvas) {
        if (this.cutoutBounds.isEmpty()) {
            super.drawStrokeShape(canvas);
            return;
        }
        Bitmap createBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(createBitmap);
        super.drawStrokeShape(canvas2);
        canvas2.drawRect(this.cutoutBounds, this.cutoutPaint);
        canvas.drawBitmap(createBitmap, 0.0f, 0.0f, (Paint) null);
    }

    private void preDraw(Canvas canvas) {
        Drawable.Callback callback = getCallback();
        if (useHardwareLayer(callback)) {
            View view = (View) callback;
            if (view.getLayerType() != 2) {
                view.setLayerType(2, null);
                return;
            }
            return;
        }
        saveCanvasLayer(canvas);
    }

    private void saveCanvasLayer(Canvas canvas) {
        this.savedLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null);
    }

    private void postDraw(Canvas canvas) {
        if (!useHardwareLayer(getCallback())) {
            canvas.restoreToCount(this.savedLayer);
        }
    }

    private boolean useHardwareLayer(Drawable.Callback callback) {
        return callback instanceof View;
    }
}
