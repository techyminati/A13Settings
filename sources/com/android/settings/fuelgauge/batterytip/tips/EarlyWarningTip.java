package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.window.R;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class EarlyWarningTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip.1
        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new EarlyWarningTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new EarlyWarningTip[i];
        }
    };
    private boolean mPowerSaveModeOn;

    public EarlyWarningTip(int i, boolean z) {
        super(3, i, false);
        this.mPowerSaveModeOn = z;
    }

    public EarlyWarningTip(Parcel parcel) {
        super(parcel);
        this.mPowerSaveModeOn = parcel.readBoolean();
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getTitle(Context context) {
        this.mState = R.string.battery_tip_early_heads_up_title;
        return context.getString(R.string.battery_tip_early_heads_up_title);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        this.mState = R.string.battery_tip_early_heads_up_summary;
        return context.getString(R.string.battery_tip_early_heads_up_summary);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconId() {
        this.mState = R.drawable.ic_battery_status_bad_24dp;
        return R.drawable.ic_battery_status_bad_24dp;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconTintColorId() {
        this.mState = R.color.battery_bad_color_light;
        return R.color.battery_bad_color_light;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void updateState(BatteryTip batteryTip) {
        EarlyWarningTip earlyWarningTip = (EarlyWarningTip) batteryTip;
        if (earlyWarningTip.mState == 0) {
            this.mState = 0;
        } else if (earlyWarningTip.mPowerSaveModeOn) {
            this.mState = 2;
        } else {
            this.mState = earlyWarningTip.getState();
        }
        this.mPowerSaveModeOn = earlyWarningTip.mPowerSaveModeOn;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1351, this.mState);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeBoolean(this.mPowerSaveModeOn);
    }
}
