package com.android.settings.connecteddevice;

import android.content.Context;
import android.net.Uri;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Log;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.password.PasswordUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.slices.SlicePreferenceController;
/* loaded from: classes.dex */
public class ConnectedDeviceDashboardFragment extends DashboardFragment {
    static final String KEY_AVAILABLE_DEVICES = "available_device_list";
    static final String KEY_CONNECTED_DEVICES = "connected_device_list";
    private static final boolean DEBUG = Log.isLoggable("ConnectedDeviceFrag", 3);
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.connected_devices);

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_connected_devices;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConnectedDeviceFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 747;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.connected_devices;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        boolean z = true;
        boolean z2 = DeviceConfig.getBoolean("settings_ui", "bt_near_by_suggestion_enabled", true);
        String callingAppPackageName = PasswordUtils.getCallingAppPackageName(getActivity().getActivityToken());
        if (DEBUG) {
            Log.d("ConnectedDeviceFrag", "onAttach() calling package name is : " + callingAppPackageName);
        }
        ((AvailableMediaDeviceGroupController) use(AvailableMediaDeviceGroupController.class)).init(this);
        ((ConnectedDeviceGroupController) use(ConnectedDeviceGroupController.class)).init(this);
        ((PreviouslyConnectedDevicePreferenceController) use(PreviouslyConnectedDevicePreferenceController.class)).init(this);
        ((SlicePreferenceController) use(SlicePreferenceController.class)).setSliceUri(z2 ? Uri.parse(getString(R.string.config_nearby_devices_slice_uri)) : null);
        DiscoverableFooterPreferenceController discoverableFooterPreferenceController = (DiscoverableFooterPreferenceController) use(DiscoverableFooterPreferenceController.class);
        if (!TextUtils.equals("com.android.settings", callingAppPackageName) && !TextUtils.equals("com.android.systemui", callingAppPackageName)) {
            z = false;
        }
        discoverableFooterPreferenceController.setAlwaysDiscoverable(z);
    }
}
