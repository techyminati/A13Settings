package com.google.android.settings.fuelgauge.reversecharging;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IThermalService;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Temperature;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.fuelgauge.BatterySettingsFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.settings.fuelgauge.reversecharging.ReverseChargingManager;
import java.util.HashMap;
/* loaded from: classes2.dex */
public abstract class ReverseChargingBasePreferenceController extends TogglePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop, ReverseChargingManager.ReverseChargingCallback {
    private static final int AUDIO_CLASS = 1;
    private static final long DELAY_TIME = 300;
    protected static final int LOW_BATTERY_LEVEL = 10;
    private static final int MAX_POWER = 100;
    private static final String TAG = "RCBasePrefController";
    protected static HashMap<String, ComponentName> sReplacingActivityMap = new HashMap<>();
    private BatterySettingsFeatureProvider mBatterySettingsFeatureProvider;
    protected boolean mCharging;
    protected final Context mContext;
    final IntentFilter mIntentFilter;
    protected boolean mLevelChanged;
    protected boolean mPluggedIn;
    PowerManager mPowerManager;
    ReverseChargingManager mReverseChargingManager;
    UsbManager mUsbManager;
    Handler mHandler = new Handler(Looper.getMainLooper());
    protected int mLevel = 100;
    protected boolean mIsUsbPlugIn = false;
    final ContentObserver mThresholdObserver = new ContentObserver(this.mHandler) { // from class: com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            ReverseChargingBasePreferenceController reverseChargingBasePreferenceController = ReverseChargingBasePreferenceController.this;
            reverseChargingBasePreferenceController.mHandler.removeCallbacks(reverseChargingBasePreferenceController.mUpdateStateRunnable);
            ReverseChargingBasePreferenceController reverseChargingBasePreferenceController2 = ReverseChargingBasePreferenceController.this;
            reverseChargingBasePreferenceController2.mHandler.postDelayed(reverseChargingBasePreferenceController2.mUpdateStateRunnable, ReverseChargingBasePreferenceController.DELAY_TIME);
        }
    };
    final Runnable mUpdateStateRunnable = new Runnable() { // from class: com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController.2
        @Override // java.lang.Runnable
        public void run() {
            int thresholdLevel = ReverseChargingBasePreferenceController.this.getThresholdLevel();
            ReverseChargingBasePreferenceController reverseChargingBasePreferenceController = ReverseChargingBasePreferenceController.this;
            if (thresholdLevel >= reverseChargingBasePreferenceController.mLevel) {
                reverseChargingBasePreferenceController.mReverseChargingManager.setReverseChargingState(false);
            } else {
                reverseChargingBasePreferenceController.notifyChanged();
            }
        }
    };
    IThermalService mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
    BatteryLevelReceiver mReceiver = new BatteryLevelReceiver();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return false;
    }

    /* renamed from: updateState */
    public abstract void lambda$notifyChanged$0();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ReverseChargingBasePreferenceController(Context context, String str) {
        super(context, str);
        this.mReverseChargingManager = ReverseChargingManager.getInstance(context);
        this.mUsbManager = (UsbManager) context.getSystemService(UsbManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.mBatterySettingsFeatureProvider = FeatureFactory.getFactory(context).getBatterySettingsFeatureProvider(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getThresholdLevel() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "advanced_battery_usage_amount", 2) * 5;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isOverheat() {
        try {
            for (Temperature temperature : this.mThermalService.getCurrentTemperaturesWithType(3)) {
                if (temperature.getStatus() >= 5) {
                    Log.w(TAG, "isOverheat() current skin status = " + temperature.getStatus() + ", temperature = " + temperature.getValue());
                    return true;
                }
            }
        } catch (RemoteException e) {
            Log.w(TAG, "isOverheat() : " + e);
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ReverseChargingUtils.getAvailability(this.mContext, this.mReverseChargingManager);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mReverseChargingManager.registerCallback(this);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("advanced_battery_usage_amount"), false, this.mThresholdObserver);
        updateUsbDevice();
    }

    private void updateUsbDevice() {
        this.mIsUsbPlugIn = false;
        for (UsbDevice usbDevice : this.mUsbManager.getDeviceList().values()) {
            setIsUsbPlugin(usbDevice);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mReverseChargingManager.unregisterCallback(this);
        this.mContext.getContentResolver().unregisterContentObserver(this.mThresholdObserver);
    }

    /* loaded from: classes2.dex */
    class BatteryLevelReceiver extends BroadcastReceiver {
        BatteryLevelReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean z = false;
            if (TextUtils.equals(action, "android.intent.action.BATTERY_CHANGED")) {
                int intExtra = (int) ((intent.getIntExtra("level", 0) * 100.0f) / intent.getIntExtra("scale", 100));
                ReverseChargingBasePreferenceController reverseChargingBasePreferenceController = ReverseChargingBasePreferenceController.this;
                reverseChargingBasePreferenceController.mLevelChanged = reverseChargingBasePreferenceController.mLevel != intExtra;
                reverseChargingBasePreferenceController.mLevel = intExtra;
                reverseChargingBasePreferenceController.mPluggedIn = intent.getIntExtra("plugged", 0) != 0;
                int intExtra2 = intent.getIntExtra("status", 1);
                boolean z2 = intExtra2 == 5;
                ReverseChargingBasePreferenceController reverseChargingBasePreferenceController2 = ReverseChargingBasePreferenceController.this;
                if (z2 || intExtra2 == 2) {
                    z = true;
                }
                reverseChargingBasePreferenceController2.mCharging = z;
                reverseChargingBasePreferenceController2.notifyChanged();
            } else if (TextUtils.equals(action, "android.os.action.POWER_SAVE_MODE_CHANGED")) {
                ReverseChargingBasePreferenceController.this.notifyChanged();
            } else if (TextUtils.equals(action, "android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                ReverseChargingBasePreferenceController.this.setIsUsbPlugin((UsbDevice) intent.getParcelableExtra("device"));
                ReverseChargingBasePreferenceController.this.notifyChanged();
            } else if (TextUtils.equals(action, "android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                ReverseChargingBasePreferenceController reverseChargingBasePreferenceController3 = ReverseChargingBasePreferenceController.this;
                reverseChargingBasePreferenceController3.mIsUsbPlugIn = false;
                reverseChargingBasePreferenceController3.notifyChanged();
            }
        }
    }

    void setIsUsbPlugin(UsbDevice usbDevice) {
        boolean z;
        boolean z2;
        if (usbDevice != null) {
            boolean z3 = false;
            int i = 0;
            while (true) {
                if (i >= usbDevice.getInterfaceCount()) {
                    z = false;
                    break;
                } else if (usbDevice.getInterface(i).getInterfaceClass() == 1) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            int i2 = 0;
            while (true) {
                if (i2 >= usbDevice.getConfigurationCount()) {
                    z2 = false;
                    break;
                } else if (usbDevice.getConfiguration(i2).getMaxPower() < 100) {
                    z2 = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z || !z2) {
                z3 = true;
            }
            this.mIsUsbPlugIn = z3;
        }
    }

    @Override // com.google.android.settings.fuelgauge.reversecharging.ReverseChargingManager.ReverseChargingCallback
    public void onReverseChargingStateChanged() {
        notifyChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged() {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.google.android.settings.fuelgauge.reversecharging.ReverseChargingBasePreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ReverseChargingBasePreferenceController.this.lambda$notifyChanged$0();
            }
        });
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        ComponentName componentName;
        String fragment = preference.getFragment();
        if (fragment == null || fragment.isEmpty()) {
            return super.handlePreferenceTreeClick(preference);
        }
        ComponentName convertClassPathToComponentName = convertClassPathToComponentName(fragment);
        if (convertClassPathToComponentName == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        if (sReplacingActivityMap.containsKey(fragment)) {
            componentName = sReplacingActivityMap.get(fragment);
        } else {
            if (this.mBatterySettingsFeatureProvider == null) {
                Log.i(TAG, "feature provider is null");
            }
            ComponentName replacingActivity = this.mBatterySettingsFeatureProvider.getReplacingActivity(convertClassPathToComponentName);
            sReplacingActivityMap.put(fragment, replacingActivity);
            componentName = replacingActivity;
        }
        if (componentName == null || convertClassPathToComponentName.compareTo(componentName) == 0) {
            return super.handlePreferenceTreeClick(preference);
        }
        Log.i(TAG, "replacingActivity = " + componentName);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        try {
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.i(TAG, "failed " + e.getMessage());
            return true;
        }
    }

    protected static ComponentName convertClassPathToComponentName(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String[] split = str.split("\\.");
        int length = split.length - 1;
        if (length < 0) {
            return null;
        }
        int length2 = (str.length() - split[length].length()) - 1;
        return new ComponentName(length2 > 0 ? str.substring(0, length2) : "", split[length]);
    }
}
