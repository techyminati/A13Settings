package com.android.settings.network;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.MobilePlanPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class NetworkDashboardFragment extends DashboardFragment implements MobilePlanPreferenceController.MobilePlanPreferenceHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.network_provider_internet) { // from class: com.android.settings.network.NetworkDashboardFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return NetworkDashboardFragment.buildPreferenceControllers(context, null, null, null, null);
        }
    };

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 1 == i ? 609 : 0;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_network_dashboard;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 746;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.network_provider_internet;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AirplaneModePreferenceController) use(AirplaneModePreferenceController.class)).setFragment(this);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(AllInOneTetherPreferenceController.class));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        ((AllInOneTetherPreferenceController) use(AllInOneTetherPreferenceController.class)).initEnabler(getSettingsLifecycle());
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this.mMetricsFeatureProvider, this, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, MetricsFeatureProvider metricsFeatureProvider, Fragment fragment, MobilePlanPreferenceController.MobilePlanPreferenceHost mobilePlanPreferenceHost) {
        MobilePlanPreferenceController mobilePlanPreferenceController = new MobilePlanPreferenceController(context, mobilePlanPreferenceHost);
        InternetPreferenceController internetPreferenceController = new InternetPreferenceController(context, lifecycle);
        VpnPreferenceController vpnPreferenceController = new VpnPreferenceController(context);
        PrivateDnsPreferenceController privateDnsPreferenceController = new PrivateDnsPreferenceController(context);
        if (lifecycle != null) {
            lifecycle.addObserver(mobilePlanPreferenceController);
            lifecycle.addObserver(vpnPreferenceController);
            lifecycle.addObserver(privateDnsPreferenceController);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MobileNetworkSummaryController(context, lifecycle));
        arrayList.add(new TetherPreferenceController(context, lifecycle));
        arrayList.add(vpnPreferenceController);
        arrayList.add(new ProxyPreferenceController(context));
        arrayList.add(mobilePlanPreferenceController);
        arrayList.add(internetPreferenceController);
        arrayList.add(privateDnsPreferenceController);
        arrayList.add(new NetworkProviderCallsSmsController(context, lifecycle));
        return arrayList;
    }

    @Override // com.android.settings.network.MobilePlanPreferenceController.MobilePlanPreferenceHost
    public void showMobilePlanMessageDialog() {
        showDialog(1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        Log.d("NetworkDashboardFrag", "onCreateDialog: dialogId=" + i);
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        final MobilePlanPreferenceController mobilePlanPreferenceController = (MobilePlanPreferenceController) use(MobilePlanPreferenceController.class);
        return new AlertDialog.Builder(getActivity()).setMessage(mobilePlanPreferenceController.getMobilePlanDialogMessage()).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                MobilePlanPreferenceController.this.setMobilePlanDialogMessage(null);
            }
        }).create();
    }
}
