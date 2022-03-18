package com.google.android.settings.fuelgauge;

import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;
import androidx.window.R;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.fuelgauge.BatteryPreferenceController;
import com.android.settings.fuelgauge.BatteryStatusFeatureProviderImpl;
import com.android.settings.fuelgauge.BatteryUtils;
import com.google.android.settings.fuelgauge.reversecharging.ReverseChargingManager;
import com.google.android.systemui.adaptivecharging.AdaptiveChargingManager;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class BatteryStatusFeatureProviderGoogleImpl extends BatteryStatusFeatureProviderImpl {
    private boolean mAdaptiveChargingEnabledInSettings;
    private AdaptiveChargingManager mAdaptiveChargingManager;
    private ReverseChargingManager mReverseChargingManager;

    public BatteryStatusFeatureProviderGoogleImpl(Context context) {
        super(context);
        this.mAdaptiveChargingManager = new AdaptiveChargingManager(context);
        this.mReverseChargingManager = ReverseChargingManager.getInstance(context);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("adaptive_charging_enabled"), false, new ContentObserver(null) { // from class: com.google.android.settings.fuelgauge.BatteryStatusFeatureProviderGoogleImpl.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                BatteryStatusFeatureProviderGoogleImpl.this.refreshAdaptiveChargingEnabled();
            }
        });
        refreshAdaptiveChargingEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshAdaptiveChargingEnabled() {
        this.mAdaptiveChargingEnabledInSettings = this.mAdaptiveChargingManager.isAvailable() && this.mAdaptiveChargingManager.isEnabled();
    }

    @Override // com.android.settings.fuelgauge.BatteryStatusFeatureProviderImpl, com.android.settings.fuelgauge.BatteryStatusFeatureProvider
    public boolean triggerBatteryStatusUpdate(final BatteryPreferenceController batteryPreferenceController, final BatteryInfo batteryInfo) {
        if (this.mReverseChargingManager.isReverseChargingOn() && !BatteryUtils.isBatteryDefenderOn(batteryInfo)) {
            String string = this.mContext.getString(batteryInfo.discharging ? R.string.reverse_charging_is_on_and_discharging_summary : R.string.reverse_charging_is_on_and_charging_summary);
            batteryInfo.statusLabel = string;
            CharSequence charSequence = batteryInfo.remainingLabel;
            if (charSequence != null) {
                string = this.mContext.getString(R.string.battery_state_and_duration, string, charSequence);
            }
            batteryPreferenceController.updateBatteryStatus(string, batteryInfo);
            return true;
        } else if (batteryInfo.discharging || BatteryUtils.isBatteryDefenderOn(batteryInfo) || !this.mAdaptiveChargingEnabledInSettings) {
            return false;
        } else {
            this.mAdaptiveChargingManager.queryStatus(new AdaptiveChargingManager.AdaptiveChargingStatusReceiver() { // from class: com.google.android.settings.fuelgauge.BatteryStatusFeatureProviderGoogleImpl.2
                private boolean mSetStatus;

                @Override // com.google.android.systemui.adaptivecharging.AdaptiveChargingManager.AdaptiveChargingStatusReceiver
                public void onReceiveStatus(String str, int i) {
                    if (AdaptiveChargingManager.isActive(str, i)) {
                        batteryPreferenceController.updateBatteryStatus(((BatteryStatusFeatureProviderImpl) BatteryStatusFeatureProviderGoogleImpl.this).mContext.getResources().getString(R.string.adaptive_charging_time_estimate, BatteryStatusFeatureProviderGoogleImpl.this.mAdaptiveChargingManager.formatTimeToFull(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(i + 29))), batteryInfo);
                        this.mSetStatus = true;
                    }
                }

                @Override // com.google.android.systemui.adaptivecharging.AdaptiveChargingManager.AdaptiveChargingStatusReceiver
                public void onDestroyInterface() {
                    if (!this.mSetStatus) {
                        batteryPreferenceController.updateBatteryStatus(null, batteryInfo);
                    }
                }
            });
            return true;
        }
    }
}
