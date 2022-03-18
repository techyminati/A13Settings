package com.android.settings.accessibility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.bluetooth.BluetoothDeviceDetailsFragment;
import com.android.settings.bluetooth.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
/* loaded from: classes.dex */
public class AccessibilityHearingAidPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "AccessibilityHearingAidPreferenceController";
    private FragmentManager mFragmentManager;
    private Preference mHearingAidPreference;
    private final BroadcastReceiver mHearingAidChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.accessibility.AccessibilityHearingAidPreferenceController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) == 2) {
                    AccessibilityHearingAidPreferenceController accessibilityHearingAidPreferenceController = AccessibilityHearingAidPreferenceController.this;
                    accessibilityHearingAidPreferenceController.updateState(accessibilityHearingAidPreferenceController.mHearingAidPreference);
                    return;
                }
                AccessibilityHearingAidPreferenceController.this.mHearingAidPreference.setSummary(R.string.accessibility_hearingaid_not_connected_summary);
            } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction()) && intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) != 12) {
                AccessibilityHearingAidPreferenceController.this.mHearingAidPreference.setSummary(R.string.accessibility_hearingaid_not_connected_summary);
            }
        }
    };
    private final LocalBluetoothManager mLocalBluetoothManager = getLocalBluetoothManager();
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean mHearingAidProfileSupported = isHearingAidProfileSupported();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AccessibilityHearingAidPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mHearingAidPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mHearingAidProfileSupported ? 0 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mHearingAidProfileSupported) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            this.mContext.registerReceiver(this.mHearingAidChangedReceiver, intentFilter);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mHearingAidProfileSupported) {
            this.mContext.unregisterReceiver(this.mHearingAidChangedReceiver);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        CachedBluetoothDevice connectedHearingAidDevice = getConnectedHearingAidDevice();
        if (connectedHearingAidDevice == null) {
            launchHearingAidInstructionDialog();
            return true;
        }
        launchBluetoothDeviceDetailSetting(connectedHearingAidDevice);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        CachedBluetoothDevice connectedHearingAidDevice = getConnectedHearingAidDevice();
        if (connectedHearingAidDevice == null) {
            return this.mContext.getText(R.string.accessibility_hearingaid_not_connected_summary);
        }
        return connectedHearingAidDevice.getName();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    CachedBluetoothDevice getConnectedHearingAidDevice() {
        BluetoothAdapter bluetoothAdapter;
        if (this.mHearingAidProfileSupported && (bluetoothAdapter = this.mBluetoothAdapter) != null && bluetoothAdapter.isEnabled()) {
            for (BluetoothDevice bluetoothDevice : this.mLocalBluetoothManager.getProfileManager().getHearingAidProfile().getConnectedDevices()) {
                if (!this.mLocalBluetoothManager.getCachedDeviceManager().isSubDevice(bluetoothDevice)) {
                    return this.mLocalBluetoothManager.getCachedDeviceManager().findDevice(bluetoothDevice);
                }
            }
        }
        return null;
    }

    private boolean isHearingAidProfileSupported() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled() && this.mBluetoothAdapter.getSupportedProfiles().contains(21);
    }

    private LocalBluetoothManager getLocalBluetoothManager() {
        FutureTask futureTask = new FutureTask(new Callable() { // from class: com.android.settings.accessibility.AccessibilityHearingAidPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                LocalBluetoothManager lambda$getLocalBluetoothManager$0;
                lambda$getLocalBluetoothManager$0 = AccessibilityHearingAidPreferenceController.this.lambda$getLocalBluetoothManager$0();
                return lambda$getLocalBluetoothManager$0;
            }
        });
        try {
            futureTask.run();
            return (LocalBluetoothManager) futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.w(TAG, "Error getting LocalBluetoothManager.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ LocalBluetoothManager lambda$getLocalBluetoothManager$0() throws Exception {
        return Utils.getLocalBtManager(this.mContext);
    }

    void setPreference(Preference preference) {
        this.mHearingAidPreference = preference;
    }

    void launchBluetoothDeviceDetailSetting(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice != null) {
            Bundle bundle = new Bundle();
            bundle.putString("device_address", cachedBluetoothDevice.getDevice().getAddress());
            new SubSettingLauncher(this.mContext).setDestination(BluetoothDeviceDetailsFragment.class.getName()).setArguments(bundle).setTitleRes(R.string.device_details_title).setSourceMetricsCategory(2).launch();
        }
    }

    void launchHearingAidInstructionDialog() {
        HearingAidDialogFragment.newInstance().show(this.mFragmentManager, HearingAidDialogFragment.class.toString());
    }
}
