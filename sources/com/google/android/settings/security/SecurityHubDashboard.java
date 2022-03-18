package com.google.android.settings.security;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.safetycenter.SafetyCenterStatusHolder;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.google.android.settings.security.SecurityContentManager;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class SecurityHubDashboard extends DashboardFragment implements SecurityContentManager.UiDataSubscriber {
    public static final String KEY_SECURITY_PRIMARY_WARNING_GROUP = "security_primary_warning_group";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.security_hub_dashboard) { // from class: com.google.android.settings.security.SecurityHubDashboard.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SecurityHubDashboard.buildPreferenceControllers(context, null);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return FeatureFactory.getFactory(context).getSecuritySettingsFeatureProvider().hasAlternativeSecuritySettingsFragment() && !SafetyCenterStatusHolder.get().isEnabled(context);
        }
    };
    private SecurityContentManager mSecurityContentManager;
    private final Object mPreferenceUpdateLock = new Object();
    private final Object mLoadingAnimationLock = new Object();
    private Map<String, Preference> mContentProvidedPreferences = new HashMap();
    private boolean mInitialDataFetched = false;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SecurityHubDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1884;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.security_hub_dashboard;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SecurityHubDashboard securityHubDashboard) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ChangeScreenLockGooglePreferenceController(context, securityHubDashboard));
        arrayList.add(new ShowMoreWarningsPreferenceController(context));
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSecurityContentManager = SecurityContentManager.getInstance(getContext()).subscribe(this);
        getPreferenceManager().setPreferenceComparisonCallback(null);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        synchronized (this.mLoadingAnimationLock) {
            if (!this.mInitialDataFetched) {
                setLoading(true, false);
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((PrimarySecurityWarningPreferenceController) use(PrimarySecurityWarningPreferenceController.class)).init(this);
    }

    @Override // com.google.android.settings.security.SecurityContentManager.UiDataSubscriber
    public void onSecurityHubUiDataChange() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() { // from class: com.google.android.settings.security.SecurityHubDashboard$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SecurityHubDashboard.this.lambda$onSecurityHubUiDataChange$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSecurityHubUiDataChange$0() {
        if (getContext() != null) {
            synchronized (this.mPreferenceUpdateLock) {
                updateSecurityEntries();
                updateSecurityWarnings();
                updatePreferenceStates();
            }
            synchronized (this.mLoadingAnimationLock) {
                if (getView() != null && !this.mInitialDataFetched) {
                    this.mInitialDataFetched = true;
                    setLoading(false, true);
                }
            }
        }
    }

    private void updateSecurityEntries() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        List<SecurityContentManager.Entry> entries = this.mSecurityContentManager.getEntries();
        Map map = (Map) entries.stream().collect(Collectors.toMap(SecurityHubDashboard$$ExternalSyntheticLambda6.INSTANCE, Function.identity()));
        Map<String, Preference> map2 = (Map) entries.stream().map(new Function() { // from class: com.google.android.settings.security.SecurityHubDashboard$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Preference lambda$updateSecurityEntries$1;
                lambda$updateSecurityEntries$1 = SecurityHubDashboard.this.lambda$updateSecurityEntries$1((SecurityContentManager.Entry) obj);
                return lambda$updateSecurityEntries$1;
            }
        }).collect(Collectors.toMap(SecurityHubDashboard$$ExternalSyntheticLambda5.INSTANCE, Function.identity()));
        MapDifference difference = Maps.difference(map2, this.mContentProvidedPreferences);
        Sets.SetView<String> intersection = Sets.intersection(map2.keySet(), this.mContentProvidedPreferences.keySet());
        Collection values = difference.entriesOnlyOnRight().values();
        Collection values2 = difference.entriesOnlyOnLeft().values();
        for (String str : intersection) {
            Preference findPreference = preferenceScreen.findPreference(str);
            if (findPreference != null) {
                updateSecurityPreference(findPreference, (SecurityContentManager.Entry) map.get(str));
            }
        }
        Objects.requireNonNull(preferenceScreen);
        values.forEach(new Consumer() { // from class: com.google.android.settings.security.SecurityHubDashboard$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PreferenceScreen.this.removePreference((Preference) obj);
            }
        });
        values2.forEach(new Consumer() { // from class: com.google.android.settings.security.SecurityHubDashboard$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PreferenceScreen.this.addPreference((Preference) obj);
            }
        });
        this.mContentProvidedPreferences = map2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Preference lambda$updateSecurityEntries$1(SecurityContentManager.Entry entry) {
        return updateSecurityPreference(new Preference(getContext()), entry);
    }

    private void updateSecurityWarnings() {
        SecurityWarning primarySecurityWarning = this.mSecurityContentManager.getPrimarySecurityWarning();
        LogicalPreferenceGroup primarySecurityWarningGroup = getPrimarySecurityWarningGroup();
        if (primarySecurityWarning != null) {
            primarySecurityWarningGroup.setVisible(true);
        } else {
            primarySecurityWarningGroup.setVisible(false);
        }
    }

    private LogicalPreferenceGroup getPrimarySecurityWarningGroup() {
        return (LogicalPreferenceGroup) getPreferenceScreen().findPreference(KEY_SECURITY_PRIMARY_WARNING_GROUP);
    }

    private Preference updateSecurityPreference(Preference preference, SecurityContentManager.Entry entry) {
        preference.setTitle(entry.getTitle());
        preference.setKey(entry.getSecuritySourceId());
        preference.setSummary(entry.getSummary());
        preference.setIcon(entry.getSecurityLevel().getEntryIconResId());
        preference.setOrder(entry.getOrder());
        final Bundle onClickBundle = entry.getOnClickBundle();
        if (onClickBundle != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.google.android.settings.security.SecurityHubDashboard$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    boolean lambda$updateSecurityPreference$2;
                    lambda$updateSecurityPreference$2 = SecurityHubDashboard.this.lambda$updateSecurityPreference$2(onClickBundle, preference2);
                    return lambda$updateSecurityPreference$2;
                }
            });
        }
        return preference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateSecurityPreference$2(Bundle bundle, Preference preference) {
        return this.mSecurityContentManager.handleClick(bundle, getActivity());
    }
}
