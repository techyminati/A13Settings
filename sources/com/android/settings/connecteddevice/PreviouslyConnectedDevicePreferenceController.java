package com.android.settings.connecteddevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.bluetooth.BluetoothDevicePreference;
import com.android.settings.bluetooth.BluetoothDeviceUpdater;
import com.android.settings.bluetooth.SavedBluetoothDeviceUpdater;
import com.android.settings.connecteddevice.dock.DockUpdater;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PreviouslyConnectedDevicePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, DevicePreferenceCallback {
    private static final int DOCK_DEVICE_INDEX = 9;
    private static final String KEY_SEE_ALL = "previously_connected_devices_see_all";
    private static final int MAX_DEVICE_NUM = 3;
    private BluetoothDeviceUpdater mBluetoothDeviceUpdater;
    private PreferenceGroup mPreferenceGroup;
    private DockUpdater mSavedDockUpdater;
    Preference mSeeAllPreference;
    private static final String TAG = "PreviouslyDevicePreController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final List<Preference> mDevicesList = new ArrayList();
    private final List<Preference> mDockDevicesList = new ArrayList();
    BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.connecteddevice.PreviouslyConnectedDevicePreferenceController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PreviouslyConnectedDevicePreferenceController.this.updatePreferenceVisibility();
        }
    };
    IntentFilter mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    public PreviouslyConnectedDevicePreferenceController(Context context, String str) {
        super(context, str);
        this.mSavedDockUpdater = FeatureFactory.getFactory(context).getDockUpdaterFeatureProvider().getSavedDockUpdater(context, this);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth") || this.mSavedDockUpdater != null) ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceGroup = preferenceGroup;
        this.mSeeAllPreference = preferenceGroup.findPreference(KEY_SEE_ALL);
        updatePreferenceVisibility();
        if (isAvailable()) {
            Context context = preferenceScreen.getContext();
            this.mBluetoothDeviceUpdater.setPrefContext(context);
            this.mSavedDockUpdater.setPreferenceContext(context);
            this.mBluetoothDeviceUpdater.forceUpdate();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mBluetoothDeviceUpdater.registerCallback();
        this.mSavedDockUpdater.registerCallback();
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter, 2);
        this.mBluetoothDeviceUpdater.refreshPreference();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mBluetoothDeviceUpdater.unregisterCallback();
        this.mSavedDockUpdater.unregisterCallback();
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void init(DashboardFragment dashboardFragment) {
        this.mBluetoothDeviceUpdater = new SavedBluetoothDeviceUpdater(dashboardFragment.getContext(), dashboardFragment, this);
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceAdded(Preference preference) {
        List mostRecentlyConnectedDevices = this.mBluetoothAdapter.getMostRecentlyConnectedDevices();
        int indexOf = preference instanceof BluetoothDevicePreference ? mostRecentlyConnectedDevices.indexOf(((BluetoothDevicePreference) preference).getBluetoothDevice().getDevice()) : 9;
        if (DEBUG) {
            Log.d(TAG, "onDeviceAdded() " + ((Object) preference.getTitle()) + ", index of : " + indexOf);
            Iterator it = mostRecentlyConnectedDevices.iterator();
            while (it.hasNext()) {
                Log.d(TAG, "onDeviceAdded() most recently device : " + ((BluetoothDevice) it.next()).getName());
            }
        }
        addPreference(indexOf, preference);
        updatePreferenceVisibility();
    }

    private void addPreference(int i, Preference preference) {
        if (!(preference instanceof BluetoothDevicePreference)) {
            this.mDockDevicesList.add(preference);
        } else if (i < 0 || this.mDevicesList.size() < i) {
            this.mDevicesList.add(preference);
        } else {
            this.mDevicesList.add(i, preference);
        }
        addPreference();
    }

    private void addPreference() {
        this.mPreferenceGroup.removeAll();
        this.mPreferenceGroup.addPreference(this.mSeeAllPreference);
        int deviceListSize = getDeviceListSize();
        for (int i = 0; i < deviceListSize; i++) {
            if (DEBUG) {
                Log.d(TAG, "addPreference() add device : " + ((Object) this.mDevicesList.get(i).getTitle()));
            }
            this.mDevicesList.get(i).setOrder(i);
            this.mPreferenceGroup.addPreference(this.mDevicesList.get(i));
        }
        if (this.mDockDevicesList.size() > 0) {
            for (int i2 = 0; i2 < getDockDeviceListSize(3 - deviceListSize); i2++) {
                if (DEBUG) {
                    Log.d(TAG, "addPreference() add dock device : " + ((Object) this.mDockDevicesList.get(i2).getTitle()));
                }
                this.mDockDevicesList.get(i2).setOrder(9);
                this.mPreferenceGroup.addPreference(this.mDockDevicesList.get(i2));
            }
        }
    }

    private int getDeviceListSize() {
        if (this.mDevicesList.size() >= 3) {
            return 3;
        }
        return this.mDevicesList.size();
    }

    private int getDockDeviceListSize(int i) {
        return this.mDockDevicesList.size() >= i ? i : this.mDockDevicesList.size();
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceRemoved(Preference preference) {
        if (preference instanceof BluetoothDevicePreference) {
            this.mDevicesList.remove(preference);
        } else {
            this.mDockDevicesList.remove(preference);
        }
        addPreference();
        updatePreferenceVisibility();
    }

    void setBluetoothDeviceUpdater(BluetoothDeviceUpdater bluetoothDeviceUpdater) {
        this.mBluetoothDeviceUpdater = bluetoothDeviceUpdater;
    }

    void setSavedDockUpdater(DockUpdater dockUpdater) {
        this.mSavedDockUpdater = dockUpdater;
    }

    void setPreferenceGroup(PreferenceGroup preferenceGroup) {
        this.mPreferenceGroup = preferenceGroup;
    }

    void updatePreferenceVisibility() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            this.mSeeAllPreference.setSummary(this.mContext.getString(R.string.connected_device_see_all_summary));
        } else {
            this.mSeeAllPreference.setSummary("");
        }
    }
}
