package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.BidiFormatter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.window.R;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public abstract class DeviceListPreferenceFragment extends RestrictedDashboardFragment implements BluetoothCallback {
    BluetoothAdapter mBluetoothAdapter;
    PreferenceGroup mDeviceListGroup;
    LocalBluetoothManager mLocalManager;
    boolean mScanEnabled;
    BluetoothDevice mSelectedDevice;
    boolean mShowDevicesWithoutNames;
    final HashMap<CachedBluetoothDevice, BluetoothDevicePreference> mDevicePreferenceMap = new HashMap<>();
    final List<BluetoothDevice> mSelectedList = new ArrayList();
    private BluetoothDeviceFilter.Filter mFilter = BluetoothDeviceFilter.ALL_FILTER;

    public abstract String getDeviceListKey();

    /* JADX INFO: Access modifiers changed from: protected */
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
    }

    abstract void initPreferencesFromPreferenceScreen();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeviceListPreferenceFragment(String str) {
        super(str);
    }

    final void setFilter(BluetoothDeviceFilter.Filter filter) {
        this.mFilter = filter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setFilter(int i) {
        this.mFilter = BluetoothDeviceFilter.getFilter(i);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("DeviceListPreferenceFragment", "Bluetooth is not supported on this device");
            return;
        }
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mShowDevicesWithoutNames = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", false);
        initPreferencesFromPreferenceScreen();
        this.mDeviceListGroup = (PreferenceCategory) findPreference(getDeviceListKey());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager != null && !isUiRestricted()) {
            this.mLocalManager.setForegroundActivity(getActivity());
            this.mLocalManager.getEventManager().registerCallback(this);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mLocalManager != null && !isUiRestricted()) {
            removeAllDevices();
            this.mLocalManager.setForegroundActivity(null);
            this.mLocalManager.getEventManager().unregisterCallback(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAllDevices() {
        this.mDevicePreferenceMap.clear();
        this.mDeviceListGroup.removeAll();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addCachedDevices() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy()) {
            onDeviceAdded(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("bt_scan".equals(preference.getKey())) {
            startScanning();
            return true;
        } else if (!(preference instanceof BluetoothDevicePreference)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) preference;
            BluetoothDevice device = bluetoothDevicePreference.getCachedDevice().getDevice();
            this.mSelectedDevice = device;
            this.mSelectedList.add(device);
            onDevicePreferenceClick(bluetoothDevicePreference);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        bluetoothDevicePreference.onClicked();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDevicePreferenceMap.get(cachedBluetoothDevice) == null && this.mBluetoothAdapter.getState() == 12 && this.mFilter.matches(cachedBluetoothDevice.getDevice())) {
            createDevicePreference(cachedBluetoothDevice);
        }
    }

    void createDevicePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDeviceListGroup == null) {
            Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
            return;
        }
        String address = cachedBluetoothDevice.getDevice().getAddress();
        BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) getCachedPreference(address);
        if (bluetoothDevicePreference == null) {
            bluetoothDevicePreference = new BluetoothDevicePreference(getPrefContext(), cachedBluetoothDevice, this.mShowDevicesWithoutNames, 2);
            bluetoothDevicePreference.setKey(address);
            bluetoothDevicePreference.hideSecondTarget(true);
            this.mDeviceListGroup.addPreference(bluetoothDevicePreference);
        }
        initDevicePreference(bluetoothDevicePreference);
        this.mDevicePreferenceMap.put(cachedBluetoothDevice, bluetoothDevicePreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFooterPreference(Preference preference) {
        preference.setTitle(getString(R.string.bluetooth_footer_mac_message, BidiFormatter.getInstance().unicodeWrap(this.mBluetoothAdapter.getAddress())));
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevicePreference remove = this.mDevicePreferenceMap.remove(cachedBluetoothDevice);
        if (remove != null) {
            this.mDeviceListGroup.removePreference(remove);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableScanning() {
        if (!this.mScanEnabled) {
            startScanning();
            this.mScanEnabled = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableScanning() {
        if (this.mScanEnabled) {
            stopScanning();
            this.mScanEnabled = false;
        }
    }

    public void onScanningStateChanged(boolean z) {
        if (!z && this.mScanEnabled) {
            startScanning();
        }
    }

    public void addDeviceCategory(PreferenceGroup preferenceGroup, int i, BluetoothDeviceFilter.Filter filter, boolean z) {
        cacheRemoveAllPrefs(preferenceGroup);
        preferenceGroup.setTitle(i);
        this.mDeviceListGroup = preferenceGroup;
        if (z) {
            setFilter(BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
            addCachedDevices();
        }
        setFilter(filter);
        preferenceGroup.setEnabled(true);
        removeCachedPrefs(preferenceGroup);
    }

    void startScanning() {
        if (!this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.startDiscovery();
        }
    }

    void stopScanning() {
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
    }
}
