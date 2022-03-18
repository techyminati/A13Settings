package com.android.settings.deviceinfo.aboutphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.UserManager;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.BluetoothAddressPreferenceController;
import com.android.settings.deviceinfo.BuildNumberPreferenceController;
import com.android.settings.deviceinfo.DeviceNamePreferenceController;
import com.android.settings.deviceinfo.FccEquipmentIdPreferenceController;
import com.android.settings.deviceinfo.FeedbackPreferenceController;
import com.android.settings.deviceinfo.IpAddressPreferenceController;
import com.android.settings.deviceinfo.ManualPreferenceController;
import com.android.settings.deviceinfo.RegulatoryInfoPreferenceController;
import com.android.settings.deviceinfo.SafetyInfoPreferenceController;
import com.android.settings.deviceinfo.UptimePreferenceController;
import com.android.settings.deviceinfo.WifiMacAddressPreferenceController;
import com.android.settings.deviceinfo.imei.ImeiInfoPreferenceController;
import com.android.settings.deviceinfo.simstatus.SimStatusPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class MyDeviceInfoFragment extends DashboardFragment implements DeviceNamePreferenceController.DeviceNamePreferenceHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.my_device_info) { // from class: com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return MyDeviceInfoFragment.buildPreferenceControllers(context, null, null);
        }
    };
    private BuildNumberPreferenceController mBuildNumberPreferenceController;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_about;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MyDeviceInfoFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 40;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.my_device_info;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ImeiInfoPreferenceController) use(ImeiInfoPreferenceController.class)).setHost(this);
        ((DeviceNamePreferenceController) use(DeviceNamePreferenceController.class)).setHost(this);
        BuildNumberPreferenceController buildNumberPreferenceController = (BuildNumberPreferenceController) use(BuildNumberPreferenceController.class);
        this.mBuildNumberPreferenceController = buildNumberPreferenceController;
        buildNumberPreferenceController.setHost(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        initHeader();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, MyDeviceInfoFragment myDeviceInfoFragment, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SimStatusPreferenceController(context, myDeviceInfoFragment));
        arrayList.add(new IpAddressPreferenceController(context, lifecycle));
        arrayList.add(new WifiMacAddressPreferenceController(context, lifecycle));
        arrayList.add(new BluetoothAddressPreferenceController(context, lifecycle));
        arrayList.add(new RegulatoryInfoPreferenceController(context));
        arrayList.add(new SafetyInfoPreferenceController(context));
        arrayList.add(new ManualPreferenceController(context));
        arrayList.add(new FeedbackPreferenceController(myDeviceInfoFragment, context));
        arrayList.add(new FccEquipmentIdPreferenceController(context));
        arrayList.add(new UptimePreferenceController(context, lifecycle));
        return arrayList;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (!this.mBuildNumberPreferenceController.onActivityResult(i, i2, intent)) {
            super.onActivityResult(i, i2, intent);
        }
    }

    private void initHeader() {
        LayoutPreference layoutPreference = (LayoutPreference) getPreferenceScreen().findPreference("my_device_info_header");
        boolean z = getContext().getResources().getBoolean(R.bool.config_show_device_header_in_device_info);
        layoutPreference.setVisible(z);
        if (z) {
            View findViewById = layoutPreference.findViewById(R.id.entity_header);
            FragmentActivity activity = getActivity();
            Bundle arguments = getArguments();
            EntityHeaderController buttonActions = EntityHeaderController.newInstance(activity, this, findViewById).setRecyclerView(getListView(), getSettingsLifecycle()).setButtonActions(0, 0);
            if (arguments.getInt("icon_id", 0) == 0) {
                UserManager userManager = (UserManager) getActivity().getSystemService("user");
                UserInfo existingUser = Utils.getExistingUser(userManager, Process.myUserHandle());
                buttonActions.setLabel(existingUser.name);
                buttonActions.setIcon(com.android.settingslib.Utils.getUserIcon(getActivity(), userManager, existingUser));
            }
            buttonActions.done((Activity) activity, true);
        }
    }

    @Override // com.android.settings.deviceinfo.DeviceNamePreferenceController.DeviceNamePreferenceHost
    public void showDeviceNameWarningDialog(String str) {
        DeviceNameWarningDialog.show(this);
    }

    public void onSetDeviceNameConfirm(boolean z) {
        ((DeviceNamePreferenceController) use(DeviceNamePreferenceController.class)).updateDeviceName(z);
    }
}
