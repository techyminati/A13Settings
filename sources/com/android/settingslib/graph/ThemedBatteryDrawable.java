package com.android.settingslib.graph;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.PathParser;
import com.android.settingslib.R$array;
import com.android.settingslib.R$color;
import com.android.settingslib.Utils;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ThemedBatteryDrawable.kt */
/* loaded from: classes.dex */
public class ThemedBatteryDrawable extends Drawable {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private int batteryLevel;
    private boolean charging;
    @NotNull
    private int[] colorLevels;
    @NotNull
    private final Context context;
    private int criticalLevel;
    private boolean dualTone;
    @NotNull
    private final Paint dualToneBackgroundFill;
    @NotNull
    private final Paint errorPaint;
    @NotNull
    private final Paint fillColorStrokePaint;
    @NotNull
    private final Paint fillColorStrokeProtection;
    @NotNull
    private final Paint fillPaint;
    private int intrinsicHeight;
    private int intrinsicWidth;
    private boolean invertFillIcon;
    private boolean powerSaveEnabled;
    @NotNull
    private final Path perimeterPath = new Path();
    @NotNull
    private final Path scaledPerimeter = new Path();
    @NotNull
    private final Path errorPerimeterPath = new Path();
    @NotNull
    private final Path scaledErrorPerimeter = new Path();
    @NotNull
    private final Path fillMask = new Path();
    @NotNull
    private final Path scaledFill = new Path();
    @NotNull
    private final RectF fillRect = new RectF();
    @NotNull
    private final RectF levelRect = new RectF();
    @NotNull
    private final Path levelPath = new Path();
    @NotNull
    private final Matrix scaleMatrix = new Matrix();
    @NotNull
    private final Rect padding = new Rect();
    @NotNull
    private final Path unifiedPath = new Path();
    @NotNull
    private final Path boltPath = new Path();
    @NotNull
    private final Path scaledBolt = new Path();
    @NotNull
    private final Path plusPath = new Path();
    @NotNull
    private final Path scaledPlus = new Path();
    private int fillColor = -65281;
    private int backgroundColor = -65281;
    private int levelColor = -65281;
    @NotNull
    private final Function0<Unit> invalidateRunnable = new ThemedBatteryDrawable$invalidateRunnable$1(this);

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    public ThemedBatteryDrawable(@NotNull Context context, int i) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.criticalLevel = context.getResources().getInteger(17694770);
        Paint paint = new Paint(1);
        paint.setColor(i);
        paint.setAlpha(255);
        paint.setDither(true);
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setBlendMode(BlendMode.SRC);
        paint.setStrokeMiter(5.0f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        this.fillColorStrokePaint = paint;
        Paint paint2 = new Paint(1);
        paint2.setDither(true);
        paint2.setStrokeWidth(5.0f);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setBlendMode(BlendMode.CLEAR);
        paint2.setStrokeMiter(5.0f);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        this.fillColorStrokeProtection = paint2;
        Paint paint3 = new Paint(1);
        paint3.setColor(i);
        paint3.setAlpha(255);
        paint3.setDither(true);
        paint3.setStrokeWidth(0.0f);
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        this.fillPaint = paint3;
        Paint paint4 = new Paint(1);
        paint4.setColor(Utils.getColorStateListDefaultColor(context, R$color.batterymeter_plus_color));
        paint4.setAlpha(255);
        paint4.setDither(true);
        paint4.setStrokeWidth(0.0f);
        paint4.setStyle(Paint.Style.FILL_AND_STROKE);
        paint4.setBlendMode(BlendMode.SRC);
        this.errorPaint = paint4;
        Paint paint5 = new Paint(1);
        paint5.setColor(i);
        paint5.setAlpha(85);
        paint5.setDither(true);
        paint5.setStrokeWidth(0.0f);
        paint5.setStyle(Paint.Style.FILL_AND_STROKE);
        this.dualToneBackgroundFill = paint5;
        float f = context.getResources().getDisplayMetrics().density;
        this.intrinsicHeight = (int) (20.0f * f);
        this.intrinsicWidth = (int) (f * 12.0f);
        Resources resources = context.getResources();
        TypedArray obtainTypedArray = resources.obtainTypedArray(R$array.batterymeter_color_levels);
        TypedArray obtainTypedArray2 = resources.obtainTypedArray(R$array.batterymeter_color_values);
        int length = obtainTypedArray.length();
        this.colorLevels = new int[length * 2];
        int i2 = 0;
        while (i2 < length) {
            i2++;
            int i3 = i2 * 2;
            this.colorLevels[i3] = obtainTypedArray.getInt(i2, 0);
            if (obtainTypedArray2.getType(i2) == 2) {
                this.colorLevels[i3 + 1] = Utils.getColorAttrDefaultColor(this.context, obtainTypedArray2.getThemeAttributeId(i2, 0));
            } else {
                this.colorLevels[i3 + 1] = obtainTypedArray2.getColor(i2, 0);
            }
        }
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        loadPaths();
    }

    public int getCriticalLevel() {
        return this.criticalLevel;
    }

    public final boolean getCharging() {
        return this.charging;
    }

    public final void setCharging(boolean z) {
        this.charging = z;
        postInvalidate();
    }

    public final boolean getPowerSaveEnabled() {
        return this.powerSaveEnabled;
    }

    public final void setPowerSaveEnabled(boolean z) {
        this.powerSaveEnabled = z;
        postInvalidate();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NotNull Canvas c) {
        float f;
        Intrinsics.checkNotNullParameter(c, "c");
        c.saveLayer(null, null);
        this.unifiedPath.reset();
        this.levelPath.reset();
        this.levelRect.set(this.fillRect);
        int i = this.batteryLevel;
        float f2 = i / 100.0f;
        if (i >= 95) {
            f = this.fillRect.top;
        } else {
            RectF rectF = this.fillRect;
            f = (rectF.height() * (1 - f2)) + rectF.top;
        }
        this.levelRect.top = (float) Math.floor(f);
        this.levelPath.addRect(this.levelRect, Path.Direction.CCW);
        this.unifiedPath.addPath(this.scaledPerimeter);
        if (!this.dualTone) {
            this.unifiedPath.op(this.levelPath, Path.Op.UNION);
        }
        this.fillPaint.setColor(this.levelColor);
        if (this.charging) {
            this.unifiedPath.op(this.scaledBolt, Path.Op.DIFFERENCE);
            if (!this.invertFillIcon) {
                c.drawPath(this.scaledBolt, this.fillPaint);
            }
        }
        if (this.dualTone) {
            c.drawPath(this.unifiedPath, this.dualToneBackgroundFill);
            c.save();
            c.clipRect(0.0f, getBounds().bottom - (getBounds().height() * f2), getBounds().right, getBounds().bottom);
            c.drawPath(this.unifiedPath, this.fillPaint);
            c.restore();
        } else {
            this.fillPaint.setColor(this.fillColor);
            c.drawPath(this.unifiedPath, this.fillPaint);
            this.fillPaint.setColor(this.levelColor);
            if (this.batteryLevel <= 15 && !this.charging) {
                c.save();
                c.clipPath(this.scaledFill);
                c.drawPath(this.levelPath, this.fillPaint);
                c.restore();
            }
        }
        if (this.charging) {
            c.clipOutPath(this.scaledBolt);
            if (this.invertFillIcon) {
                c.drawPath(this.scaledBolt, this.fillColorStrokePaint);
            } else {
                c.drawPath(this.scaledBolt, this.fillColorStrokeProtection);
            }
        } else if (this.powerSaveEnabled) {
            c.drawPath(this.scaledErrorPerimeter, this.errorPaint);
            c.drawPath(this.scaledPlus, this.errorPaint);
        }
        c.restore();
    }

    private final int batteryColorForLevel(int i) {
        if (this.charging || this.powerSaveEnabled) {
            return this.fillColor;
        }
        return getColorForLevel(i);
    }

    private final int getColorForLevel(int i) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int[] iArr = this.colorLevels;
            if (i2 >= iArr.length) {
                return i3;
            }
            int i4 = iArr[i2];
            int i5 = iArr[i2 + 1];
            if (i <= i4) {
                return i2 == iArr.length + (-2) ? this.fillColor : i5;
            }
            i2 += 2;
            i3 = i5;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.fillPaint.setColorFilter(colorFilter);
        this.fillColorStrokePaint.setColorFilter(colorFilter);
        this.dualToneBackgroundFill.setColorFilter(colorFilter);
    }

    public void setBatteryLevel(int i) {
        boolean z;
        if (i >= 67) {
            z = true;
        } else {
            z = i <= 33 ? false : this.invertFillIcon;
        }
        this.invertFillIcon = z;
        this.batteryLevel = i;
        this.levelColor = batteryColorForLevel(i);
        invalidateSelf();
    }

    public final int getBatteryLevel() {
        return this.batteryLevel;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(@Nullable Rect rect) {
        super.onBoundsChange(rect);
        updateSize();
    }

    private final void postInvalidate() {
        final Function0<Unit> function0 = this.invalidateRunnable;
        unscheduleSelf(new Runnable() { // from class: com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0
            @Override // java.lang.Runnable
            public final /* synthetic */ void run() {
                Function0.this.invoke();
            }
        });
        final Function0<Unit> function02 = this.invalidateRunnable;
        scheduleSelf(new Runnable() { // from class: com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0
            @Override // java.lang.Runnable
            public final /* synthetic */ void run() {
                Function0.this.invoke();
            }
        }, 0L);
    }

    private final void updateSize() {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            this.scaleMatrix.setScale(1.0f, 1.0f);
        } else {
            this.scaleMatrix.setScale(bounds.right / 12.0f, bounds.bottom / 20.0f);
        }
        this.perimeterPath.transform(this.scaleMatrix, this.scaledPerimeter);
        this.errorPerimeterPath.transform(this.scaleMatrix, this.scaledErrorPerimeter);
        this.fillMask.transform(this.scaleMatrix, this.scaledFill);
        this.scaledFill.computeBounds(this.fillRect, true);
        this.boltPath.transform(this.scaleMatrix, this.scaledBolt);
        this.plusPath.transform(this.scaleMatrix, this.scaledPlus);
        float max = Math.max((bounds.right / 12.0f) * 3.0f, 6.0f);
        this.fillColorStrokePaint.setStrokeWidth(max);
        this.fillColorStrokeProtection.setStrokeWidth(max);
    }

    private final void loadPaths() {
        this.perimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039877)));
        this.perimeterPath.computeBounds(new RectF(), true);
        this.errorPerimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039875)));
        this.errorPerimeterPath.computeBounds(new RectF(), true);
        this.fillMask.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039876)));
        this.fillMask.computeBounds(this.fillRect, true);
        this.boltPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039874)));
        this.plusPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039878)));
        this.dualTone = this.context.getResources().getBoolean(17891384);
    }

    /* compiled from: ThemedBatteryDrawable.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
