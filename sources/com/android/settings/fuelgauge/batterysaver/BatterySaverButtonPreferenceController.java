package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Switch;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class BatterySaverButtonPreferenceController extends TogglePreferenceController implements OnMainSwitchChangeListener, LifecycleObserver, OnStart, OnStop, BatterySaverReceiver.BatterySaverListener {
    private static final long SWITCH_ANIMATION_DURATION = 350;
    private final BatterySaverReceiver mBatterySaverReceiver;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final PowerManager mPowerManager;
    private MainSwitchPreference mPreference;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_battery;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BatterySaverButtonPreferenceController(Context context, String str) {
        super(context, str);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mBatterySaverReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public Uri getSliceUri() {
        return new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("battery_saver").build();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mBatterySaverReceiver.setListening(true);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mBatterySaverReceiver.setListening(false);
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = mainSwitchPreference;
        mainSwitchPreference.setTitle(this.mContext.getString(R.string.battery_saver_master_switch_title));
        this.mPreference.addOnSwitchChangeListener(this);
        this.mPreference.updateStatus(isChecked());
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        setChecked(z);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mPowerManager.isPowerSaveMode();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mPreference.updateStatus(isChecked());
        return BatterySaverUtils.setPowerSaveMode(this.mContext, z, false);
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onPowerSaveModeChanged() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.fuelgauge.batterysaver.BatterySaverButtonPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatterySaverButtonPreferenceController.this.lambda$onPowerSaveModeChanged$0();
            }
        }, SWITCH_ANIMATION_DURATION);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onPowerSaveModeChangedInternal */
    public void lambda$onPowerSaveModeChanged$0() {
        boolean isChecked = isChecked();
        MainSwitchPreference mainSwitchPreference = this.mPreference;
        if (mainSwitchPreference != null && mainSwitchPreference.isChecked() != isChecked) {
            this.mPreference.setChecked(isChecked);
        }
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onBatteryChanged(boolean z) {
        MainSwitchPreference mainSwitchPreference = this.mPreference;
        if (mainSwitchPreference != null) {
            mainSwitchPreference.setEnabled(!z);
        }
    }
}
