package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.window.R;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class SmartBatteryTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: com.android.settings.fuelgauge.batterytip.tips.SmartBatteryTip.1
        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new SmartBatteryTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new SmartBatteryTip[i];
        }
    };

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconId() {
        return R.drawable.ic_perm_device_information_red_24dp;
    }

    public SmartBatteryTip(int i) {
        super(0, i, false);
    }

    private SmartBatteryTip(Parcel parcel) {
        super(parcel);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.battery_tip_smart_battery_title);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        return context.getString(R.string.battery_tip_smart_battery_summary);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void updateState(BatteryTip batteryTip) {
        this.mState = batteryTip.mState;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1350, this.mState);
    }
}
