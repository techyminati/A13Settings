package com.android.settings.fuelgauge;

import android.content.ComponentName;
import android.content.Context;
/* loaded from: classes.dex */
public class BatterySettingsFeatureProviderImpl implements BatterySettingsFeatureProvider {
    protected Context mContext;

    @Override // com.android.settings.fuelgauge.BatterySettingsFeatureProvider
    public ComponentName getReplacingActivity(ComponentName componentName) {
        return componentName;
    }

    public BatterySettingsFeatureProviderImpl(Context context) {
        this.mContext = context.getApplicationContext();
    }
}
