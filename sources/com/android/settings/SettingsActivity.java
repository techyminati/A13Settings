package com.android.settings;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.window.R;
import com.android.internal.util.ArrayUtils;
import com.android.settings.Settings;
import com.android.settings.applications.appinfo.AppButtonsPreferenceController;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.gateway.SettingsGateway;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.homepage.SliceDeepLinkHomepageActivity;
import com.android.settings.homepage.TopLevelSettings;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wfd.WifiDisplaySettings;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.activityembedding.ActivityEmbeddingUtils;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.SharedPreferencesLogger;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.drawer.DashboardCategory;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SettingsActivity extends SettingsBaseActivity implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, ButtonBarHandler, FragmentManager.OnBackStackChangedListener {
    private DashboardFeatureProvider mDashboardFeatureProvider;
    private BroadcastReceiver mDevelopmentSettingsListener;
    private String mFragmentClass;
    private String mHighlightMenuKey;
    private CharSequence mInitialTitle;
    private int mInitialTitleResId;
    private SettingsMainSwitchBar mMainSwitch;
    private Button mNextButton;
    private boolean mBatteryPresent = true;
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.SettingsActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean isBatteryPresent;
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction()) && SettingsActivity.this.mBatteryPresent != (isBatteryPresent = Utils.isBatteryPresent(intent))) {
                SettingsActivity.this.mBatteryPresent = isBatteryPresent;
                SettingsActivity.this.updateTilesList();
            }
        }
    };
    private ArrayList<DashboardCategory> mCategories = new ArrayList<>();

    @Override // androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return false;
    }

    public SettingsMainSwitchBar getSwitchBar() {
        return this.mMainSwitch;
    }

    @Override // androidx.preference.PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        new SubSettingLauncher(this).setDestination(preference.getFragment()).setArguments(preference.getExtras()).setSourceMetricsCategory(preferenceFragmentCompat instanceof Instrumentable ? ((Instrumentable) preferenceFragmentCompat).getMetricsCategory() : 0).setTitleRes(-1).launch();
        return true;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        if (str.equals(getPackageName() + "_preferences")) {
            return new SharedPreferencesLogger(this, getMetricsTag(), FeatureFactory.getFactory(this).getMetricsFeatureProvider());
        }
        return super.getSharedPreferences(str, i);
    }

    private String getMetricsTag() {
        String initialFragmentName = (getIntent() == null || !getIntent().hasExtra(":settings:show_fragment")) ? null : getInitialFragmentName(getIntent());
        if (TextUtils.isEmpty(initialFragmentName)) {
            Log.w("SettingsActivity", "MetricsTag is invalid " + initialFragmentName);
            initialFragmentName = getClass().getName();
        }
        return initialFragmentName.startsWith("com.android.settings.") ? initialFragmentName.replace("com.android.settings.", "") : initialFragmentName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        View findViewById;
        getMetaData();
        Intent intent = getIntent();
        if (shouldShowTwoPaneDeepLink(intent)) {
            launchHomepageForTwoPaneDeepLink(intent);
            finishAndRemoveTask();
            super.onCreate(bundle);
            return;
        }
        super.onCreate(bundle);
        Log.d("SettingsActivity", "Starting onCreate");
        System.currentTimeMillis();
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(this).getDashboardFeatureProvider(this);
        if (intent.hasExtra("settings:ui_options")) {
            getWindow().setUiOptions(intent.getIntExtra("settings:ui_options", 0));
        }
        String initialFragmentName = getInitialFragmentName(intent);
        if (isSubSettings(intent) && !WizardManagerHelper.isAnySetupWizard(getIntent())) {
            setTheme(R.style.Theme_SubSettings);
        }
        setContentView(R.layout.settings_main_prefs);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (bundle != null) {
            setTitleFromIntent(intent);
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(":settings:categories");
            if (parcelableArrayList != null) {
                this.mCategories.clear();
                this.mCategories.addAll(parcelableArrayList);
                setTitleFromBackStack();
            }
        } else {
            launchSettingFragment(initialFragmentName, intent);
        }
        boolean isActionBarButtonEnabled = isActionBarButtonEnabled(intent);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(isActionBarButtonEnabled);
            actionBar.setHomeButtonEnabled(isActionBarButtonEnabled);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        SettingsMainSwitchBar settingsMainSwitchBar = (SettingsMainSwitchBar) findViewById(R.id.switch_bar);
        this.mMainSwitch = settingsMainSwitchBar;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setMetricsTag(getMetricsTag());
            this.mMainSwitch.setTranslationZ(findViewById(R.id.main_content).getTranslationZ() + 1.0f);
        }
        if (intent.getBooleanExtra("extra_prefs_show_button_bar", false) && (findViewById = findViewById(R.id.button_bar)) != null) {
            findViewById.setVisibility(0);
            Button button = (Button) findViewById(R.id.back_button);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$0(view);
                }
            });
            Button button2 = (Button) findViewById(R.id.skip_button);
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$1(view);
                }
            });
            Button button3 = (Button) findViewById(R.id.next_button);
            this.mNextButton = button3;
            button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$2(view);
                }
            });
            if (intent.hasExtra("extra_prefs_set_next_text")) {
                String stringExtra = intent.getStringExtra("extra_prefs_set_next_text");
                if (TextUtils.isEmpty(stringExtra)) {
                    this.mNextButton.setVisibility(8);
                } else {
                    this.mNextButton.setText(stringExtra);
                }
            }
            if (intent.hasExtra("extra_prefs_set_back_text")) {
                String stringExtra2 = intent.getStringExtra("extra_prefs_set_back_text");
                if (TextUtils.isEmpty(stringExtra2)) {
                    button.setVisibility(8);
                } else {
                    button.setText(stringExtra2);
                }
            }
            if (intent.getBooleanExtra("extra_prefs_show_skip", false)) {
                button2.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        setResult(0, null);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        setResult(-1, null);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2(View view) {
        setResult(-1, null);
        finish();
    }

    private boolean isActionBarButtonEnabled(Intent intent) {
        if (WizardManagerHelper.isAnySetupWizard(intent)) {
            return false;
        }
        return !ActivityEmbeddingUtils.shouldHideBackButton(this, intent.getBooleanExtra(":settings:is_secondary_layer_page", false));
    }

    private boolean isSubSettings(Intent intent) {
        return (this instanceof SubSettings) || intent.getBooleanExtra(":settings:show_fragment_as_subsetting", false);
    }

    public static Intent getTrampolineIntent(Intent intent, String str) {
        Intent intent2 = new Intent(intent);
        Intent replaceExtras = new Intent("android.settings.SETTINGS_EMBED_DEEP_LINK_ACTIVITY").setPackage("com.android.settings").replaceExtras(intent2);
        replaceExtras.putExtra("settings_large_screen_deep_link_intent_data", intent2.getData());
        intent2.setData(null);
        replaceExtras.putExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI", intent2.toUri(1));
        replaceExtras.putExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_HIGHLIGHT_MENU_KEY", str);
        replaceExtras.addFlags(33554432);
        return replaceExtras;
    }

    private void launchHomepageForTwoPaneDeepLink(Intent intent) {
        Intent intent2;
        if (intent.getBooleanExtra("is_from_slice", false)) {
            String stringExtra = intent.getStringExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_HIGHLIGHT_MENU_KEY");
            if (!TextUtils.isEmpty(stringExtra)) {
                this.mHighlightMenuKey = stringExtra;
            }
            intent2 = getTrampolineIntent(intent, this.mHighlightMenuKey);
            intent2.setClass(this, SliceDeepLinkHomepageActivity.class);
        } else {
            intent2 = getTrampolineIntent(intent, this.mHighlightMenuKey);
        }
        startActivity(intent2);
    }

    private boolean shouldShowTwoPaneDeepLink(Intent intent) {
        if (!com.android.settings.activityembedding.ActivityEmbeddingUtils.isEmbeddingActivityEnabled(this) || !isTaskRoot() || intent.getAction() == null) {
            return false;
        }
        if (intent.getBooleanExtra("is_from_slice", false)) {
            return true;
        }
        return !isSubSettings(intent) && !intent.getBooleanExtra("is_from_settings_homepage", false) && !TextUtils.equals(intent.getAction(), "android.intent.action.CREATE_SHORTCUT");
    }

    public String getInitialFragmentName(Intent intent) {
        return intent.getStringExtra(":settings:show_fragment");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity, android.view.ContextThemeWrapper
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(R.style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof OnActivityResultListener) {
                    fragment.onActivityResult(i, i2, intent);
                }
            }
        }
    }

    void launchSettingFragment(String str, Intent intent) {
        if (str != null) {
            setTitleFromIntent(intent);
            switchToFragment(str, intent.getBundleExtra(":settings:show_fragment_args"), true, this.mInitialTitleResId, this.mInitialTitle);
            return;
        }
        this.mInitialTitleResId = R.string.dashboard_title;
        switchToFragment(TopLevelSettings.class.getName(), null, false, this.mInitialTitleResId, this.mInitialTitle);
    }

    private void setTitleFromIntent(Intent intent) {
        Log.d("SettingsActivity", "Starting to set activity title");
        int intExtra = intent.getIntExtra(":settings:show_fragment_title_resid", -1);
        if (intExtra > 0) {
            this.mInitialTitle = null;
            this.mInitialTitleResId = intExtra;
            String stringExtra = intent.getStringExtra(":settings:show_fragment_title_res_package_name");
            if (stringExtra != null) {
                try {
                    CharSequence text = createPackageContextAsUser(stringExtra, 0, new UserHandle(UserHandle.myUserId())).getResources().getText(this.mInitialTitleResId);
                    this.mInitialTitle = text;
                    setTitle(text);
                    this.mInitialTitleResId = -1;
                    return;
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.w("SettingsActivity", "Could not find package" + stringExtra);
                } catch (Resources.NotFoundException unused2) {
                    Log.w("SettingsActivity", "Could not find title resource in " + stringExtra);
                }
            } else {
                setTitle(this.mInitialTitleResId);
            }
        } else {
            this.mInitialTitleResId = -1;
            CharSequence stringExtra2 = intent.getStringExtra(":settings:show_fragment_title");
            if (stringExtra2 == null) {
                stringExtra2 = getTitle();
            }
            this.mInitialTitle = stringExtra2;
            setTitle(stringExtra2);
        }
        Log.d("SettingsActivity", "Done setting title");
    }

    @Override // androidx.fragment.app.FragmentManager.OnBackStackChangedListener
    public void onBackStackChanged() {
        setTitleFromBackStack();
    }

    private void setTitleFromBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            int i = this.mInitialTitleResId;
            if (i > 0) {
                setTitle(i);
            } else {
                setTitle(this.mInitialTitle);
            }
        } else {
            setTitleFromBackStackEntry(getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1));
        }
    }

    private void setTitleFromBackStackEntry(FragmentManager.BackStackEntry backStackEntry) {
        CharSequence charSequence;
        int breadCrumbTitleRes = backStackEntry.getBreadCrumbTitleRes();
        if (breadCrumbTitleRes > 0) {
            charSequence = getText(breadCrumbTitleRes);
        } else {
            charSequence = backStackEntry.getBreadCrumbTitle();
        }
        if (charSequence != null) {
            setTitle(charSequence);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveState(bundle);
    }

    void saveState(Bundle bundle) {
        if (this.mCategories.size() > 0) {
            bundle.putParcelableArrayList(":settings:categories", this.mCategories);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mDevelopmentSettingsListener = new BroadcastReceiver() { // from class: com.android.settings.SettingsActivity.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                SettingsActivity.this.updateTilesList();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mDevelopmentSettingsListener, new IntentFilter("com.android.settingslib.development.DevelopmentSettingsEnabler.SETTINGS_CHANGED"));
        registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        updateTilesList();
    }

    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mDevelopmentSettingsListener);
        this.mDevelopmentSettingsListener = null;
        unregisterReceiver(this.mBatteryInfoReceiver);
    }

    @Override // android.app.Activity
    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        taskDescription.setIcon(Icon.createWithResource(this, (int) R.drawable.ic_launcher_settings));
        super.setTaskDescription(taskDescription);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        int i = 0;
        while (true) {
            String[] strArr = SettingsGateway.ENTRY_FRAGMENTS;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i].equals(str)) {
                return true;
            }
            i++;
        }
    }

    @Override // android.app.Activity
    public Intent getIntent() {
        Bundle bundle;
        Intent intent = super.getIntent();
        String startingFragmentClass = getStartingFragmentClass(intent);
        if (startingFragmentClass == null) {
            return intent;
        }
        Intent intent2 = new Intent(intent);
        intent2.putExtra(":settings:show_fragment", startingFragmentClass);
        Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
        if (bundleExtra != null) {
            bundle = new Bundle(bundleExtra);
        } else {
            bundle = new Bundle();
        }
        bundle.putParcelable("intent", intent);
        intent2.putExtra(":settings:show_fragment_args", bundle);
        return intent2;
    }

    private String getStartingFragmentClass(Intent intent) {
        String str = this.mFragmentClass;
        if (str != null) {
            return str;
        }
        String className = intent.getComponent().getClassName();
        if (className.equals(getClass().getName())) {
            return null;
        }
        return ("com.android.settings.RunningServices".equals(className) || "com.android.settings.applications.StorageUse".equals(className)) ? ManageApplications.class.getName() : className;
    }

    public void finishPreferencePanel(int i, Intent intent) {
        setResult(i, intent);
        if (intent == null || !intent.getBooleanExtra(AppButtonsPreferenceController.KEY_REMOVE_TASK_WHEN_FINISHING, false)) {
            finish();
        } else {
            finishAndRemoveTask();
        }
    }

    private void switchToFragment(String str, Bundle bundle, boolean z, int i, CharSequence charSequence) {
        Log.d("SettingsActivity", "Switching to fragment " + str);
        if (!z || isValidFragment(str)) {
            Fragment targetFragment = Utils.getTargetFragment(this, str, bundle);
            if (targetFragment != null) {
                FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
                beginTransaction.replace(R.id.main_content, targetFragment);
                if (i > 0) {
                    beginTransaction.setBreadCrumbTitle(i);
                } else if (charSequence != null) {
                    beginTransaction.setBreadCrumbTitle(charSequence);
                }
                beginTransaction.commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();
                Log.d("SettingsActivity", "Executed frag manager pendingTransactions");
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Invalid fragment for this activity: " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTilesList() {
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.this.lambda$updateTilesList$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: doUpdateTilesList */
    public void lambda$updateTilesList$3() {
        PackageManager packageManager = getPackageManager();
        boolean isAdminUser = UserManager.get(this).isAdminUser();
        String packageName = getPackageName();
        StringBuilder sb = new StringBuilder();
        boolean z = setTileEnabled(sb, new ComponentName(packageName, Settings.WifiDisplaySettingsActivity.class.getName()), WifiDisplaySettings.isAvailable(this), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DevelopmentSettingsDashboardActivity.class.getName()), DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this) && !Utils.isMonkeyRunning(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.UserSettingsActivity.class.getName()), UserManager.supportsMultipleUsers() && !Utils.isMonkeyRunning(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.PowerUsageSummaryActivity.class.getName()), this.mBatteryPresent, isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.ConnectedDeviceDashboardActivity.class.getName()), UserManager.isDeviceInDemoMode(this) ^ true, isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.BluetoothSettingsActivity.class.getName()), packageManager.hasSystemFeature("android.hardware.bluetooth"), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.WifiSettingsActivity.class.getName()), packageManager.hasSystemFeature("android.hardware.wifi"), isAdminUser)))))))));
        if (!isAdminUser) {
            List<DashboardCategory> allCategories = this.mDashboardFeatureProvider.getAllCategories();
            synchronized (allCategories) {
                for (DashboardCategory dashboardCategory : allCategories) {
                    int tilesCount = dashboardCategory.getTilesCount();
                    for (int i = 0; i < tilesCount; i++) {
                        ComponentName component = dashboardCategory.getTile(i).getIntent().getComponent();
                        boolean contains = ArrayUtils.contains(SettingsGateway.SETTINGS_FOR_RESTRICTED, component.getClassName());
                        if (packageName.equals(component.getPackageName()) && !contains) {
                            if (!setTileEnabled(sb, component, false, isAdminUser) && !z) {
                                z = false;
                            }
                            z = true;
                        }
                    }
                }
            }
        }
        if (z) {
            Log.d("SettingsActivity", "Enabled state changed for some tiles, reloading all categories " + sb.toString());
            this.mCategoryMixin.updateCategories();
            return;
        }
        Log.d("SettingsActivity", "No enabled state changed, skipping updateCategory call");
    }

    private boolean setTileEnabled(StringBuilder sb, ComponentName componentName, boolean z, boolean z2) {
        if (!z2 && getPackageName().equals(componentName.getPackageName()) && !ArrayUtils.contains(SettingsGateway.SETTINGS_FOR_RESTRICTED, componentName.getClassName())) {
            z = false;
        }
        boolean tileEnabled = setTileEnabled(componentName, z);
        if (tileEnabled) {
            sb.append(componentName.toShortString());
            sb.append(",");
        }
        return tileEnabled;
    }

    private void getMetaData() {
        Bundle bundle;
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), 128);
            if (!(activityInfo == null || (bundle = activityInfo.metaData) == null)) {
                this.mFragmentClass = bundle.getString("com.android.settings.FRAGMENT_CLASS");
                this.mHighlightMenuKey = activityInfo.metaData.getString("com.android.settings.HIGHLIGHT_MENU_KEY");
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("SettingsActivity", "Cannot get Metadata for: " + getComponentName().toString());
        }
    }

    @Override // com.android.settings.ButtonBarHandler
    public boolean hasNextButton() {
        return this.mNextButton != null;
    }

    @Override // com.android.settings.ButtonBarHandler
    public Button getNextButton() {
        return this.mNextButton;
    }
}
