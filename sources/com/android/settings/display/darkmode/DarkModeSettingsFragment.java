package com.android.settings.display.darkmode;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DarkModeSettingsFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.dark_mode_settings) { // from class: com.android.settings.display.darkmode.DarkModeSettingsFragment.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !((PowerManager) context.getSystemService(PowerManager.class)).isPowerSaveMode();
        }
    };
    private Runnable mCallback = new Runnable() { // from class: com.android.settings.display.darkmode.DarkModeSettingsFragment$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            DarkModeSettingsFragment.this.lambda$new$0();
        }
    };
    private DarkModeObserver mContentObserver;
    private DarkModeCustomPreferenceController mCustomEndController;
    private DarkModeCustomPreferenceController mCustomStartController;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 1826;
        }
        return 1825;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_dark_theme;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DarkModeSettingsFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1698;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.dark_mode_settings;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updatePreferenceStates();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContentObserver = new DarkModeObserver(getContext());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mContentObserver.subscribe(this.mCallback);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList(2);
        this.mCustomStartController = new DarkModeCustomPreferenceController(getContext(), "dark_theme_start_time", this);
        this.mCustomEndController = new DarkModeCustomPreferenceController(getContext(), "dark_theme_end_time", this);
        arrayList.add(this.mCustomStartController);
        arrayList.add(this.mCustomEndController);
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mContentObserver.unsubscribe();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("dark_theme_end_time".equals(preference.getKey())) {
            showDialog(1);
            return true;
        } else if (!"dark_theme_start_time".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        } else {
            showDialog(0);
            return true;
        }
    }

    public void refresh() {
        updatePreferenceStates();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 0 && i != 1) {
            return super.onCreateDialog(i);
        }
        if (i == 0) {
            return this.mCustomStartController.getDialog();
        }
        return this.mCustomEndController.getDialog();
    }
}
