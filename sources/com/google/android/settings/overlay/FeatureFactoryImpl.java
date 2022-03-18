package com.google.android.settings.overlay;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;
import com.android.settings.accessibility.AccessibilityMetricsFeatureProvider;
import com.android.settings.accessibility.AccessibilitySearchFeatureProvider;
import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.GameSettingsFeatureProvider;
import com.android.settings.applications.appinfo.ExtraAppInfoFeatureProvider;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.biometrics.face.FaceFeatureProvider;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProvider;
import com.android.settings.fuelgauge.BatterySettingsFeatureProvider;
import com.android.settings.fuelgauge.BatteryStatusFeatureProvider;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.overlay.DockUpdaterFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;
import com.android.settings.overlay.SurveyFeatureProvider;
import com.android.settings.search.SearchFeatureProvider;
import com.android.settings.security.SecuritySettingsFeatureProvider;
import com.android.settings.wifi.WifiTrackerLibProvider;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.google.android.settings.accessibility.AccessibilityMetricsFeatureProviderGoogleImpl;
import com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl;
import com.google.android.settings.accounts.AccountFeatureProviderGoogleImpl;
import com.google.android.settings.applications.ApplicationFeatureProviderGoogleImpl;
import com.google.android.settings.aware.AwareFeatureProviderGoogleImpl;
import com.google.android.settings.biometrics.face.FaceFeatureProviderGoogleImpl;
import com.google.android.settings.connecteddevice.dock.DockUpdaterFeatureProviderGoogleImpl;
import com.google.android.settings.core.instrumentation.SettingsGoogleMetricsFeatureProvider;
import com.google.android.settings.dashboard.suggestions.SuggestionFeatureProviderGoogleImpl;
import com.google.android.settings.experiments.GServicesProxy;
import com.google.android.settings.fuelgauge.BatterySettingsFeatureProviderGoogleImpl;
import com.google.android.settings.fuelgauge.BatteryStatusFeatureProviderGoogleImpl;
import com.google.android.settings.fuelgauge.PowerUsageFeatureProviderGoogleImpl;
import com.google.android.settings.gamemode.GameModeFeatureProviderGoogleImpl;
import com.google.android.settings.games.GameSettingsFeatureProviderGoogleImpl;
import com.google.android.settings.gestures.assist.AssistGestureFeatureProviderGoogleImpl;
import com.google.android.settings.search.SearchFeatureProviderGoogleImpl;
import com.google.android.settings.security.SecuritySettingsFeatureProviderGoogleImpl;
import com.google.android.settings.support.SupportFeatureProviderImpl;
import com.google.android.settings.survey.SurveyFeatureProviderImpl;
import com.google.android.settings.wifi.WifiTrackerLibProviderGoogleImpl;
/* loaded from: classes2.dex */
public final class FeatureFactoryImpl extends com.android.settings.overlay.FeatureFactoryImpl {
    private AccessibilityMetricsFeatureProvider mAccessibilityMetricsFeatureProvider;
    private AccessibilitySearchFeatureProvider mAccessibilitySearchFeatureProvider;
    private AccountFeatureProvider mAccountFeatureProvider;
    private ApplicationFeatureProvider mApplicationFeatureProvider;
    private AssistGestureFeatureProvider mAssistGestureFeatureProvider;
    private AwareFeatureProvider mAwareFeatureProvider;
    private BatterySettingsFeatureProvider mBatterySettingsFeatureProvider;
    private BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;
    private DockUpdaterFeatureProvider mDockUpdaterFeatureProvider;
    private ExtraAppInfoFeatureProvider mExtraAppInfoFeatureProvider;
    private FaceFeatureProvider mFaceFeatureProvider;
    private GameSettingsFeatureProvider mGameSettingsFeatureProvider;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private PowerUsageFeatureProvider mPowerUsageProvider;
    private SearchFeatureProvider mSearchFeatureProvider;
    private SecuritySettingsFeatureProvider mSecuritySettingsFeatureProvider;
    private SuggestionFeatureProvider mSuggestionFeatureProvider;
    private SupportFeatureProvider mSupportProvider;
    private SurveyFeatureProvider mSurveyFeatureProvider;
    private WifiTrackerLibProvider mWifiTrackerLibProvider;

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public ApplicationFeatureProvider getApplicationFeatureProvider(Context context) {
        if (this.mApplicationFeatureProvider == null) {
            Context applicationContext = context.getApplicationContext();
            this.mApplicationFeatureProvider = new ApplicationFeatureProviderGoogleImpl(applicationContext, applicationContext.getPackageManager(), AppGlobals.getPackageManager(), (DevicePolicyManager) applicationContext.getSystemService("device_policy"));
        }
        return this.mApplicationFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public MetricsFeatureProvider getMetricsFeatureProvider() {
        if (this.mMetricsFeatureProvider == null) {
            this.mMetricsFeatureProvider = new SettingsGoogleMetricsFeatureProvider();
        }
        return this.mMetricsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public SupportFeatureProvider getSupportFeatureProvider(Context context) {
        if (this.mSupportProvider == null) {
            this.mSupportProvider = new SupportFeatureProviderImpl(context.getApplicationContext());
        }
        return this.mSupportProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public BatteryStatusFeatureProvider getBatteryStatusFeatureProvider(Context context) {
        if (this.mBatteryStatusFeatureProvider == null) {
            this.mBatteryStatusFeatureProvider = new BatteryStatusFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return this.mBatteryStatusFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public BatterySettingsFeatureProvider getBatterySettingsFeatureProvider(Context context) {
        if (this.mBatterySettingsFeatureProvider == null) {
            this.mBatterySettingsFeatureProvider = new BatterySettingsFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return this.mBatterySettingsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public PowerUsageFeatureProvider getPowerUsageFeatureProvider(Context context) {
        if (this.mPowerUsageProvider == null) {
            this.mPowerUsageProvider = new PowerUsageFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return this.mPowerUsageProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public DockUpdaterFeatureProvider getDockUpdaterFeatureProvider() {
        if (this.mDockUpdaterFeatureProvider == null) {
            this.mDockUpdaterFeatureProvider = new DockUpdaterFeatureProviderGoogleImpl();
        }
        return this.mDockUpdaterFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public SearchFeatureProvider getSearchFeatureProvider() {
        if (this.mSearchFeatureProvider == null) {
            this.mSearchFeatureProvider = new SearchFeatureProviderGoogleImpl();
        }
        return this.mSearchFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public SurveyFeatureProvider getSurveyFeatureProvider(Context context) {
        boolean z = false;
        try {
            z = GServicesProxy.getBoolean(context.getContentResolver(), "settingsgoogle:surveys_enabled", false);
        } catch (SecurityException e) {
            Log.w("FeatureFactoryImpl", "Error reading survey feature enabled state", e);
        }
        if (!z) {
            return null;
        }
        if (this.mSurveyFeatureProvider == null) {
            this.mSurveyFeatureProvider = new SurveyFeatureProviderImpl(context);
        }
        return this.mSurveyFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public SuggestionFeatureProvider getSuggestionFeatureProvider(Context context) {
        if (this.mSuggestionFeatureProvider == null) {
            this.mSuggestionFeatureProvider = new SuggestionFeatureProviderGoogleImpl(context.getApplicationContext());
        }
        return this.mSuggestionFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public AssistGestureFeatureProvider getAssistGestureFeatureProvider() {
        if (this.mAssistGestureFeatureProvider == null) {
            this.mAssistGestureFeatureProvider = new AssistGestureFeatureProviderGoogleImpl();
        }
        return this.mAssistGestureFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public AccountFeatureProvider getAccountFeatureProvider() {
        if (this.mAccountFeatureProvider == null) {
            this.mAccountFeatureProvider = new AccountFeatureProviderGoogleImpl();
        }
        return this.mAccountFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public AwareFeatureProvider getAwareFeatureProvider() {
        if (this.mAwareFeatureProvider == null) {
            this.mAwareFeatureProvider = new AwareFeatureProviderGoogleImpl();
        }
        return this.mAwareFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public FaceFeatureProvider getFaceFeatureProvider() {
        if (this.mFaceFeatureProvider == null) {
            this.mFaceFeatureProvider = new FaceFeatureProviderGoogleImpl();
        }
        return this.mFaceFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public WifiTrackerLibProvider getWifiTrackerLibProvider() {
        if (this.mWifiTrackerLibProvider == null) {
            this.mWifiTrackerLibProvider = new WifiTrackerLibProviderGoogleImpl();
        }
        return this.mWifiTrackerLibProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public ExtraAppInfoFeatureProvider getExtraAppInfoFeatureProvider() {
        if (this.mExtraAppInfoFeatureProvider == null) {
            this.mExtraAppInfoFeatureProvider = new GameModeFeatureProviderGoogleImpl();
        }
        return this.mExtraAppInfoFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public SecuritySettingsFeatureProvider getSecuritySettingsFeatureProvider() {
        if (this.mSecuritySettingsFeatureProvider == null) {
            this.mSecuritySettingsFeatureProvider = new SecuritySettingsFeatureProviderGoogleImpl(FeatureFactory.getAppContext());
        }
        return this.mSecuritySettingsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public GameSettingsFeatureProvider getGameSettingsFeatureProvider() {
        if (this.mGameSettingsFeatureProvider == null) {
            this.mGameSettingsFeatureProvider = new GameSettingsFeatureProviderGoogleImpl();
        }
        return this.mGameSettingsFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public AccessibilitySearchFeatureProvider getAccessibilitySearchFeatureProvider() {
        if (this.mAccessibilitySearchFeatureProvider == null) {
            this.mAccessibilitySearchFeatureProvider = new AccessibilitySearchFeatureProviderGoogleImpl();
        }
        return this.mAccessibilitySearchFeatureProvider;
    }

    @Override // com.android.settings.overlay.FeatureFactoryImpl, com.android.settings.overlay.FeatureFactory
    public AccessibilityMetricsFeatureProvider getAccessibilityMetricsFeatureProvider() {
        if (this.mAccessibilityMetricsFeatureProvider == null) {
            this.mAccessibilityMetricsFeatureProvider = new AccessibilityMetricsFeatureProviderGoogleImpl();
        }
        return this.mAccessibilityMetricsFeatureProvider;
    }
}
