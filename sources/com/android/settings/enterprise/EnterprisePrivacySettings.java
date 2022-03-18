package com.android.settings.enterprise;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;
/* loaded from: classes.dex */
public class EnterprisePrivacySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.enterprise.EnterprisePrivacySettings.1
        private PrivacySettingsPreference mPrivacySettingsPreference;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return EnterprisePrivacySettings.isPageEnabled(context);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            PrivacySettingsPreference createPrivacySettingsPreference = PrivacySettingsPreferenceFactory.createPrivacySettingsPreference(context);
            this.mPrivacySettingsPreference = createPrivacySettingsPreference;
            return createPrivacySettingsPreference.getXmlResourcesToIndex();
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            PrivacySettingsPreference createPrivacySettingsPreference = PrivacySettingsPreferenceFactory.createPrivacySettingsPreference(context);
            this.mPrivacySettingsPreference = createPrivacySettingsPreference;
            return createPrivacySettingsPreference.createPreferenceControllers(false);
        }
    };
    @VisibleForTesting
    PrivacySettingsPreference mPrivacySettingsPreference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "EnterprisePrivacySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 628;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mPrivacySettingsPreference = PrivacySettingsPreferenceFactory.createPrivacySettingsPreference(context);
        super.onAttach(context);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mPrivacySettingsPreference = null;
        super.onDetach();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return this.mPrivacySettingsPreference.getPreferenceScreenResId();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return this.mPrivacySettingsPreference.createPreferenceControllers(true);
    }

    public static boolean isPageEnabled(Context context) {
        return FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context).hasDeviceOwner();
    }
}
