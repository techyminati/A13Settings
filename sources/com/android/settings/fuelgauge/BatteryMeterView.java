package com.android.settings.fuelgauge;

import android.content.Context;
import android.graphics.ColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.window.R;
import com.android.settingslib.Utils;
import com.android.settingslib.graph.ThemedBatteryDrawable;
/* loaded from: classes.dex */
public class BatteryMeterView extends ImageView {
    ColorFilter mAccentColorFilter;
    BatteryMeterDrawable mDrawable;
    ColorFilter mErrorColorFilter;
    ColorFilter mForegroundColorFilter;

    public BatteryMeterView(Context context) {
        this(context, null, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int color = context.getColor(R.color.meter_background_color);
        this.mAccentColorFilter = Utils.getAlphaInvariantColorFilterForColor(Utils.getColorAttrDefaultColor(context, 16843829));
        this.mErrorColorFilter = Utils.getAlphaInvariantColorFilterForColor(context.getColor(R.color.battery_icon_color_error));
        this.mForegroundColorFilter = Utils.getAlphaInvariantColorFilterForColor(Utils.getColorAttrDefaultColor(context, 16842800));
        BatteryMeterDrawable batteryMeterDrawable = new BatteryMeterDrawable(context, color);
        this.mDrawable = batteryMeterDrawable;
        batteryMeterDrawable.setColorFilter(this.mAccentColorFilter);
        setImageDrawable(this.mDrawable);
    }

    public void setBatteryLevel(int i) {
        this.mDrawable.setBatteryLevel(i);
        updateColorFilter();
    }

    public void setPowerSave(boolean z) {
        this.mDrawable.setPowerSaveEnabled(z);
        updateColorFilter();
    }

    public boolean getPowerSave() {
        return this.mDrawable.getPowerSaveEnabled();
    }

    public int getBatteryLevel() {
        return this.mDrawable.getBatteryLevel();
    }

    public void setCharging(boolean z) {
        this.mDrawable.setCharging(z);
        postInvalidate();
    }

    public boolean getCharging() {
        return this.mDrawable.getCharging();
    }

    private void updateColorFilter() {
        boolean powerSaveEnabled = this.mDrawable.getPowerSaveEnabled();
        int batteryLevel = this.mDrawable.getBatteryLevel();
        if (powerSaveEnabled) {
            this.mDrawable.setColorFilter(this.mForegroundColorFilter);
        } else if (batteryLevel < this.mDrawable.getCriticalLevel()) {
            this.mDrawable.setColorFilter(this.mErrorColorFilter);
        } else {
            this.mDrawable.setColorFilter(this.mAccentColorFilter);
        }
    }

    /* loaded from: classes.dex */
    public static class BatteryMeterDrawable extends ThemedBatteryDrawable {
        private final int mIntrinsicHeight;
        private final int mIntrinsicWidth;

        public BatteryMeterDrawable(Context context, int i) {
            super(context, i);
            this.mIntrinsicWidth = context.getResources().getDimensionPixelSize(R.dimen.battery_meter_width);
            this.mIntrinsicHeight = context.getResources().getDimensionPixelSize(R.dimen.battery_meter_height);
        }

        public BatteryMeterDrawable(Context context, int i, int i2, int i3) {
            super(context, i);
            this.mIntrinsicWidth = i2;
            this.mIntrinsicHeight = i3;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.mIntrinsicWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.mIntrinsicHeight;
        }
    }
}
