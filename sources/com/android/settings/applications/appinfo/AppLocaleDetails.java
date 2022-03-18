package com.android.settings.applications.appinfo;

import android.app.LocaleConfig;
import android.app.LocaleManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceGroup;
import androidx.window.R;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.appinfo.AppLocaleDetails;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AppLocaleDetails extends AppInfoBase implements RadioButtonPreference.OnClickListener {
    static final String KEY_SYSTEM_DEFAULT_LOCALE = "system_default_locale";
    AppLocaleDetailsHelper mAppLocaleDetailsHelper;
    private boolean mCreated = false;
    private RadioButtonPreference mDefaultPreference;
    private PreferenceGroup mGroupOfSuggestedLocales;
    private PreferenceGroup mGroupOfSupportedLocales;
    private LayoutPreference mPrefOfDescription;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1911;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.app_locale_details);
        this.mAppLocaleDetailsHelper = new AppLocaleDetailsHelper(getContext(), this.mPackageName);
        this.mGroupOfSuggestedLocales = (PreferenceGroup) getPreferenceScreen().findPreference("category_key_suggested_languages");
        this.mGroupOfSupportedLocales = (PreferenceGroup) getPreferenceScreen().findPreference("category_key_all_languages");
        this.mPrefOfDescription = (LayoutPreference) getPreferenceScreen().findPreference("app_locale_description");
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) getPreferenceScreen().findPreference(KEY_SYSTEM_DEFAULT_LOCALE);
        this.mDefaultPreference = radioButtonPreference;
        radioButtonPreference.setOnClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mPackageInfo == null) {
            return layoutInflater.inflate(R.layout.manage_applications_apps_unsupported, (ViewGroup) null);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        this.mAppLocaleDetailsHelper.handleAllLocalesData();
        super.onResume();
        this.mDefaultPreference.setSummary(Locale.getDefault().getDisplayName(Locale.getDefault()));
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        refreshUiInternal();
        return true;
    }

    void refreshUiInternal() {
        boolean z = true;
        if (this.mAppLocaleDetailsHelper.getSupportedLocales().isEmpty()) {
            Log.d("AppLocaleDetails", "No supported language.");
            this.mGroupOfSuggestedLocales.setVisible(false);
            this.mGroupOfSupportedLocales.setVisible(false);
            this.mPrefOfDescription.setVisible(true);
            ((TextView) this.mPrefOfDescription.findViewById(R.id.description)).setText(getContext().getString(R.string.no_multiple_language_supported, Locale.getDefault().getDisplayName(Locale.getDefault())));
            return;
        }
        resetLocalePreferences();
        Locale appDefaultLocale = AppLocaleDetailsHelper.getAppDefaultLocale(getContext(), this.mPackageName);
        this.mGroupOfSuggestedLocales.addPreference(this.mDefaultPreference);
        RadioButtonPreference radioButtonPreference = this.mDefaultPreference;
        if (appDefaultLocale != null) {
            z = false;
        }
        radioButtonPreference.setChecked(z);
        setLanguagesPreference(this.mGroupOfSuggestedLocales, this.mAppLocaleDetailsHelper.getSuggestedLocales(), appDefaultLocale);
        setLanguagesPreference(this.mGroupOfSupportedLocales, this.mAppLocaleDetailsHelper.getSupportedLocales(), appDefaultLocale);
    }

    private void resetLocalePreferences() {
        this.mGroupOfSuggestedLocales.removeAll();
        this.mGroupOfSupportedLocales.removeAll();
    }

    @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        if (KEY_SYSTEM_DEFAULT_LOCALE.equals(key)) {
            this.mAppLocaleDetailsHelper.setAppDefaultLocale(LocaleList.forLanguageTags(""));
        } else {
            this.mAppLocaleDetailsHelper.setAppDefaultLocale(key);
        }
        refreshUi();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mCreated) {
            Log.w("AppLocaleDetails", "onActivityCreated: ignoring duplicate call");
            return;
        }
        this.mCreated = true;
        if (this.mPackageInfo != null) {
            FragmentActivity activity = getActivity();
            getPreferenceScreen().addPreference(EntityHeaderController.newInstance(activity, this, null).setRecyclerView(getListView(), getSettingsLifecycle()).setIcon(Utils.getBadgedIcon(getContext(), this.mPackageInfo.applicationInfo)).setLabel(this.mPackageInfo.applicationInfo.loadLabel(this.mPm)).setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo)).setPackageName(this.mPackageName).setUid(this.mPackageInfo.applicationInfo.uid).setHasAppInfoLink(true).setButtonActions(0, 0).done(activity, getPrefContext()));
        }
    }

    public static CharSequence getSummary(Context context, String str) {
        Locale appDefaultLocale = AppLocaleDetailsHelper.getAppDefaultLocale(context, str);
        if (appDefaultLocale != null) {
            return appDefaultLocale.getDisplayName(appDefaultLocale);
        }
        Locale locale = Locale.getDefault();
        return context.getString(R.string.preference_of_system_locale_summary, locale.getDisplayName(locale));
    }

    private void setLanguagesPreference(PreferenceGroup preferenceGroup, Collection<Locale> collection, Locale locale) {
        if (collection != null) {
            for (Locale locale2 : collection) {
                if (locale2 != null) {
                    RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getContext());
                    radioButtonPreference.setTitle(locale2.getDisplayName(locale2));
                    radioButtonPreference.setKey(locale2.toLanguageTag());
                    radioButtonPreference.setChecked(locale2.equals(locale));
                    radioButtonPreference.setOnClickListener(this);
                    preferenceGroup.addPreference(radioButtonPreference);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AppLocaleDetailsHelper {
        private Collection<Locale> mAppSupportedLocales;
        private Context mContext;
        private LocaleManager mLocaleManager;
        private String mPackageName;
        private Collection<Locale> mProcessedSuggestedLocales = new ArrayList();
        private Collection<Locale> mProcessedSupportedLocales = new ArrayList();
        private TelephonyManager mTelephonyManager;

        AppLocaleDetailsHelper(Context context, String str) {
            this.mAppSupportedLocales = new ArrayList();
            this.mContext = context;
            this.mPackageName = str;
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            this.mLocaleManager = (LocaleManager) context.getSystemService(LocaleManager.class);
            this.mAppSupportedLocales = getAppSupportedLocales();
        }

        public void handleAllLocalesData() {
            clearLocalesData();
            handleSuggestedLocales();
            handleSupportedLocales();
        }

        public Collection<Locale> getSuggestedLocales() {
            return this.mProcessedSuggestedLocales;
        }

        public Collection<Locale> getSupportedLocales() {
            return this.mProcessedSupportedLocales;
        }

        void handleSuggestedLocales() {
            final Locale appDefaultLocale = getAppDefaultLocale(this.mContext, this.mPackageName);
            Iterator<Locale> it = this.mAppSupportedLocales.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (compareLocale(it.next(), appDefaultLocale)) {
                        this.mProcessedSuggestedLocales.add(appDefaultLocale);
                        break;
                    }
                } else {
                    break;
                }
            }
            String simCountryIso = this.mTelephonyManager.getSimCountryIso();
            Locale locale = Locale.US;
            final String upperCase = simCountryIso.toUpperCase(locale);
            final String upperCase2 = this.mTelephonyManager.getNetworkCountryIso().toUpperCase(locale);
            this.mAppSupportedLocales.forEach(new Consumer() { // from class: com.android.settings.applications.appinfo.AppLocaleDetails$AppLocaleDetailsHelper$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppLocaleDetails.AppLocaleDetailsHelper.this.lambda$handleSuggestedLocales$0(appDefaultLocale, upperCase, upperCase2, (Locale) obj);
                }
            });
            final ArrayList arrayList = new ArrayList();
            getCurrentSystemLocales().forEach(new Consumer() { // from class: com.android.settings.applications.appinfo.AppLocaleDetails$AppLocaleDetailsHelper$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppLocaleDetails.AppLocaleDetailsHelper.this.lambda$handleSuggestedLocales$2(arrayList, (Locale) obj);
                }
            });
            arrayList.removeAll(this.mProcessedSuggestedLocales);
            this.mProcessedSuggestedLocales.addAll(arrayList);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleSuggestedLocales$0(Locale locale, String str, String str2, Locale locale2) {
            String upperCase = locale2.getCountry().toUpperCase(Locale.US);
            if (!compareLocale(locale2, locale) && isCountrySuggestedLocale(upperCase, str, str2)) {
                this.mProcessedSuggestedLocales.add(locale2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleSuggestedLocales$2(final Collection collection, final Locale locale) {
            this.mAppSupportedLocales.forEach(new Consumer() { // from class: com.android.settings.applications.appinfo.AppLocaleDetails$AppLocaleDetailsHelper$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppLocaleDetails.AppLocaleDetailsHelper.lambda$handleSuggestedLocales$1(locale, collection, (Locale) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$handleSuggestedLocales$1(Locale locale, Collection collection, Locale locale2) {
            if (compareLocale(locale, locale2)) {
                collection.add(locale2);
            }
        }

        static boolean compareLocale(Locale locale, Locale locale2) {
            if (locale == null && locale2 == null) {
                return true;
            }
            if (locale == null || locale2 == null) {
                return false;
            }
            return LocaleList.matchesLanguageAndScript(locale, locale2);
        }

        private static boolean isCountrySuggestedLocale(String str, String str2, String str3) {
            return (!str2.isEmpty() && str2.equals(str)) || (!str3.isEmpty() && str3.equals(str));
        }

        void handleSupportedLocales() {
            this.mProcessedSupportedLocales.addAll(this.mAppSupportedLocales);
            Collection<Locale> collection = this.mProcessedSuggestedLocales;
            if (collection != null || !collection.isEmpty()) {
                this.mProcessedSuggestedLocales.retainAll(this.mProcessedSupportedLocales);
                this.mProcessedSupportedLocales.removeAll(this.mProcessedSuggestedLocales);
            }
        }

        private void clearLocalesData() {
            this.mProcessedSuggestedLocales.clear();
            this.mProcessedSupportedLocales.clear();
        }

        private Collection<Locale> getAppSupportedLocales() {
            ArrayList arrayList = new ArrayList();
            LocaleList packageLocales = getPackageLocales();
            int i = 0;
            if (packageLocales == null || packageLocales.size() <= 0) {
                String[] assetLocales = getAssetLocales();
                int length = assetLocales.length;
                while (i < length) {
                    arrayList.add(Locale.forLanguageTag(assetLocales[i]));
                    i++;
                }
            } else {
                while (i < packageLocales.size()) {
                    arrayList.add(packageLocales.get(i));
                    i++;
                }
            }
            return arrayList;
        }

        public static Locale getAppDefaultLocale(Context context, String str) {
            LocaleList applicationLocales;
            LocaleManager localeManager = (LocaleManager) context.getSystemService(LocaleManager.class);
            if (localeManager == null) {
                applicationLocales = null;
            } else {
                try {
                    applicationLocales = localeManager.getApplicationLocales(str);
                } catch (IllegalArgumentException e) {
                    Log.w("AppLocaleDetails", "package name : " + str + " is not correct. " + e);
                    return null;
                }
            }
            if (applicationLocales == null) {
                return null;
            }
            return applicationLocales.get(0);
        }

        public void setAppDefaultLocale(String str) {
            if (str.isEmpty()) {
                Log.w("AppLocaleDetails", "[setAppDefaultLocale] No language tag.");
            } else {
                setAppDefaultLocale(LocaleList.forLanguageTags(str));
            }
        }

        public void setAppDefaultLocale(LocaleList localeList) {
            LocaleManager localeManager = this.mLocaleManager;
            if (localeManager == null) {
                Log.w("AppLocaleDetails", "LocaleManager is null, and cannot set the app locale up.");
            } else {
                localeManager.setApplicationLocales(this.mPackageName, localeList);
            }
        }

        Collection<Locale> getCurrentSystemLocales() {
            LocaleList locales = Resources.getSystem().getConfiguration().getLocales();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < locales.size(); i++) {
                arrayList.add(locales.get(i));
            }
            return arrayList;
        }

        String[] getAssetLocales() {
            try {
                PackageManager packageManager = this.mContext.getPackageManager();
                return packageManager.getResourcesForApplication(packageManager.getPackageInfo(this.mPackageName, 131072).applicationInfo).getAssets().getNonSystemLocales();
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("AppLocaleDetails", "Can not found the package name : " + this.mPackageName + " / " + e);
                return new String[0];
            }
        }

        LocaleList getPackageLocales() {
            try {
                LocaleConfig localeConfig = new LocaleConfig(this.mContext.createPackageContext(this.mPackageName, 0));
                if (localeConfig.getStatus() == 0) {
                    return localeConfig.getSupportedLocales();
                }
                return null;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("AppLocaleDetails", "Can not found the package name : " + this.mPackageName + " / " + e);
                return null;
            }
        }
    }
}
