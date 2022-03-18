package com.android.launcher3.icons;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import androidx.core.graphics.ColorUtils;
/* loaded from: classes.dex */
public class PlaceHolderIconDrawable extends FastBitmapDrawable {
    private final Path mProgressPath = GraphicsUtils.getShapePath(100);

    public PlaceHolderIconDrawable(BitmapInfo bitmapInfo, Context context) {
        super(bitmapInfo);
        this.mPaint.setColor(ColorUtils.compositeColors(GraphicsUtils.getAttrColor(context, R$attr.loadingIconColor), bitmapInfo.color));
    }

    @Override // com.android.launcher3.icons.FastBitmapDrawable
    protected void drawInternal(Canvas canvas, Rect rect) {
        int save = canvas.save();
        canvas.translate(rect.left, rect.top);
        canvas.scale(rect.width() / 100.0f, rect.height() / 100.0f);
        canvas.drawPath(this.mProgressPath, this.mPaint);
        canvas.restoreToCount(save);
    }
}
