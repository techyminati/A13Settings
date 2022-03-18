package com.android.settings.nearby;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.Objects;
/* loaded from: classes.dex */
public class FastPairSettingsFragment extends SettingsPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.fast_pair_settings);

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1910;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.fast_pair_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) findPreference("fast_pair_scan_switch");
        Objects.requireNonNull(mainSwitchPreference);
        mainSwitchPreference.addOnSwitchChangeListener(new OnMainSwitchChangeListener() { // from class: com.android.settings.nearby.FastPairSettingsFragment$$ExternalSyntheticLambda1
            @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                FastPairSettingsFragment.this.lambda$onCreate$0(r1, z);
            }
        });
        mainSwitchPreference.setChecked(isFastPairScanAvailable());
        Preference findPreference = findPreference("saved_devices");
        Objects.requireNonNull(findPreference);
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.nearby.FastPairSettingsFragment$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$onCreate$1;
                lambda$onCreate$1 = FastPairSettingsFragment.this.lambda$onCreate$1(preference);
                return lambda$onCreate$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(Switch r1, boolean z) {
        Settings.Secure.putInt(getContentResolver(), "fast_pair_scan_enabled", z ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$1(Preference preference) {
        Intent savedDevicesIntent = getSavedDevicesIntent();
        if (savedDevicesIntent == null || getActivity() == null) {
            return true;
        }
        getActivity().startActivity(savedDevicesIntent);
        return true;
    }

    private boolean isFastPairScanAvailable() {
        return Settings.Secure.getInt(getContentResolver(), "fast_pair_scan_enabled", 1) != 0;
    }

    private ComponentName getSavedDevicesComponent() {
        String string = Settings.Secure.getString(getContentResolver(), "nearby_fast_pair_settings_devices_component");
        if (TextUtils.isEmpty(string)) {
            string = getString(17039917);
        }
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    private Intent getSavedDevicesIntent() {
        ComponentName savedDevicesComponent = getSavedDevicesComponent();
        if (savedDevicesComponent == null) {
            return null;
        }
        PackageManager packageManager = getPackageManager();
        Intent intent = getIntent();
        intent.setAction("android.intent.action.VIEW");
        intent.setComponent(savedDevicesComponent);
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 128);
        if (resolveActivity != null && resolveActivity.activityInfo != null) {
            return intent;
        }
        Log.e("FastPairSettingsFrag", "Device-specified fast pair component (" + savedDevicesComponent + ") not available");
        return null;
    }
}
