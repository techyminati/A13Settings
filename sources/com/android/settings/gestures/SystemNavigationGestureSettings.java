package com.android.settings.gestures;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.accessibility.AccessibilityGestureNavigationTutorial;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.utils.CandidateInfoExtra;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SystemNavigationGestureSettings extends RadioButtonPickerFragment implements HelpResourceProvider {
    static final String KEY_SYSTEM_NAV_2BUTTONS = "system_nav_2buttons";
    static final String KEY_SYSTEM_NAV_3BUTTONS = "system_nav_3buttons";
    static final String KEY_SYSTEM_NAV_GESTURAL = "system_nav_gestural";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.system_navigation_gesture_settings) { // from class: com.android.settings.gestures.SystemNavigationGestureSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return SystemNavigationPreferenceController.isGestureAvailable(context);
        }
    };
    private boolean mA11yTutorialDialogShown = false;
    private IOverlayManager mOverlayManager;
    private IllustrationPreference mVideoPreference;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_default;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1374;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.system_navigation_gesture_settings;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            boolean z = bundle.getBoolean("show_a11y_tutorial_dialog_bool", false);
            this.mA11yTutorialDialogShown = z;
            if (z) {
                AccessibilityGestureNavigationTutorial.showGestureNavigationTutorialDialog(getContext(), new DialogInterface.OnDismissListener() { // from class: com.android.settings.gestures.SystemNavigationGestureSettings$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        SystemNavigationGestureSettings.this.lambda$onCreate$0(dialogInterface);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(DialogInterface dialogInterface) {
        this.mA11yTutorialDialogShown = false;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("show_a11y_tutorial_dialog_bool", this.mA11yTutorialDialogShown);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context).edit().putBoolean("pref_system_navigation_suggestion_complete", true).apply();
        this.mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        IllustrationPreference illustrationPreference = new IllustrationPreference(context);
        this.mVideoPreference = illustrationPreference;
        setIllustrationVideo(illustrationPreference, getDefaultKey());
        migrateOverlaySensitivityToSettings(context, this.mOverlayManager);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void updateCandidates() {
        String defaultKey = getDefaultKey();
        String systemDefaultKey = getSystemDefaultKey();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        preferenceScreen.addPreference(this.mVideoPreference);
        List<? extends CandidateInfo> candidates = getCandidates();
        if (candidates != null) {
            for (CandidateInfo candidateInfo : candidates) {
                SelectorWithWidgetPreference selectorWithWidgetPreference = new SelectorWithWidgetPreference(getPrefContext());
                bindPreference(selectorWithWidgetPreference, candidateInfo.getKey(), candidateInfo, defaultKey);
                bindPreferenceExtra(selectorWithWidgetPreference, candidateInfo.getKey(), candidateInfo, defaultKey, systemDefaultKey);
                preferenceScreen.addPreference(selectorWithWidgetPreference);
            }
            mayCheckOnlyRadioButton();
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(SelectorWithWidgetPreference selectorWithWidgetPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if (candidateInfo instanceof CandidateInfoExtra) {
            selectorWithWidgetPreference.setSummary(((CandidateInfoExtra) candidateInfo).loadSummary());
            if (KEY_SYSTEM_NAV_GESTURAL.equals(candidateInfo.getKey())) {
                selectorWithWidgetPreference.setExtraWidgetOnClickListener(new View.OnClickListener() { // from class: com.android.settings.gestures.SystemNavigationGestureSettings$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SystemNavigationGestureSettings.this.lambda$bindPreferenceExtra$1(view);
                    }
                });
            }
            if (KEY_SYSTEM_NAV_2BUTTONS.equals(candidateInfo.getKey()) || KEY_SYSTEM_NAV_3BUTTONS.equals(candidateInfo.getKey())) {
                selectorWithWidgetPreference.setExtraWidgetOnClickListener(new View.OnClickListener() { // from class: com.android.settings.gestures.SystemNavigationGestureSettings$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SystemNavigationGestureSettings.this.lambda$bindPreferenceExtra$2(view);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindPreferenceExtra$1(View view) {
        startActivity(new Intent("com.android.settings.GESTURE_NAVIGATION_SETTINGS"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindPreferenceExtra$2(View view) {
        new SubSettingLauncher(getContext()).setDestination(ButtonNavigationSettingsFragment.class.getName()).setSourceMetricsCategory(1374).launch();
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        ArrayList arrayList = new ArrayList();
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.gestural")) {
            arrayList.add(new CandidateInfoExtra(context.getText(R.string.edge_to_edge_navigation_title), context.getText(R.string.edge_to_edge_navigation_summary), KEY_SYSTEM_NAV_GESTURAL, true));
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.twobutton")) {
            arrayList.add(new CandidateInfoExtra(context.getText(R.string.swipe_up_to_switch_apps_title), context.getText(R.string.swipe_up_to_switch_apps_summary), KEY_SYSTEM_NAV_2BUTTONS, true));
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.threebutton")) {
            arrayList.add(new CandidateInfoExtra(context.getText(R.string.legacy_navigation_title), context.getText(R.string.legacy_navigation_summary), KEY_SYSTEM_NAV_3BUTTONS, true));
        }
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return getCurrentSystemNavigationMode(getContext());
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        setCurrentSystemNavigationMode(this.mOverlayManager, str);
        setIllustrationVideo(this.mVideoPreference, str);
        setGestureNavigationTutorialDialog(str);
        return true;
    }

    static void migrateOverlaySensitivityToSettings(Context context, IOverlayManager iOverlayManager) {
        if (SystemNavigationPreferenceController.isGestureNavigationEnabled(context)) {
            OverlayInfo overlayInfo = null;
            try {
                overlayInfo = iOverlayManager.getOverlayInfo("com.android.internal.systemui.navbar.gestural", -2);
            } catch (RemoteException unused) {
            }
            if (overlayInfo != null && !overlayInfo.isEnabled()) {
                setCurrentSystemNavigationMode(iOverlayManager, KEY_SYSTEM_NAV_GESTURAL);
                Settings.Secure.putFloat(context.getContentResolver(), "back_gesture_inset_scale_left", 1.0f);
                Settings.Secure.putFloat(context.getContentResolver(), "back_gesture_inset_scale_right", 1.0f);
            }
        }
    }

    static String getCurrentSystemNavigationMode(Context context) {
        return SystemNavigationPreferenceController.isGestureNavigationEnabled(context) ? KEY_SYSTEM_NAV_GESTURAL : SystemNavigationPreferenceController.is2ButtonNavigationEnabled(context) ? KEY_SYSTEM_NAV_2BUTTONS : KEY_SYSTEM_NAV_3BUTTONS;
    }

    static void setCurrentSystemNavigationMode(IOverlayManager iOverlayManager, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1860313413:
                if (str.equals(KEY_SYSTEM_NAV_2BUTTONS)) {
                    c = 0;
                    break;
                }
                break;
            case -1375361165:
                if (str.equals(KEY_SYSTEM_NAV_GESTURAL)) {
                    c = 1;
                    break;
                }
                break;
            case -117503078:
                if (str.equals(KEY_SYSTEM_NAV_3BUTTONS)) {
                    c = 2;
                    break;
                }
                break;
        }
        String str2 = "com.android.internal.systemui.navbar.gestural";
        switch (c) {
            case 0:
                str2 = "com.android.internal.systemui.navbar.twobutton";
                break;
            case 2:
                str2 = "com.android.internal.systemui.navbar.threebutton";
                break;
        }
        try {
            iOverlayManager.setEnabledExclusiveInCategory(str2, -2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static void setIllustrationVideo(IllustrationPreference illustrationPreference, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1860313413:
                if (str.equals(KEY_SYSTEM_NAV_2BUTTONS)) {
                    c = 0;
                    break;
                }
                break;
            case -1375361165:
                if (str.equals(KEY_SYSTEM_NAV_GESTURAL)) {
                    c = 1;
                    break;
                }
                break;
            case -117503078:
                if (str.equals(KEY_SYSTEM_NAV_3BUTTONS)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                illustrationPreference.setLottieAnimationResId(R.raw.lottie_system_nav_2_button);
                return;
            case 1:
                illustrationPreference.setLottieAnimationResId(R.raw.lottie_system_nav_fully_gestural);
                return;
            case 2:
                illustrationPreference.setLottieAnimationResId(R.raw.lottie_system_nav_3_button);
                return;
            default:
                return;
        }
    }

    private void setGestureNavigationTutorialDialog(String str) {
        if (!TextUtils.equals(KEY_SYSTEM_NAV_GESTURAL, str) || isAccessibilityFloatingMenuEnabled() || (!isAnyServiceSupportAccessibilityButton() && !isNavBarMagnificationEnabled())) {
            this.mA11yTutorialDialogShown = false;
            return;
        }
        this.mA11yTutorialDialogShown = true;
        AccessibilityGestureNavigationTutorial.showGestureNavigationTutorialDialog(getContext(), new DialogInterface.OnDismissListener() { // from class: com.android.settings.gestures.SystemNavigationGestureSettings$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                SystemNavigationGestureSettings.this.lambda$setGestureNavigationTutorialDialog$3(dialogInterface);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setGestureNavigationTutorialDialog$3(DialogInterface dialogInterface) {
        this.mA11yTutorialDialogShown = false;
    }

    private boolean isAnyServiceSupportAccessibilityButton() {
        return !((AccessibilityManager) getContext().getSystemService(AccessibilityManager.class)).getAccessibilityShortcutTargets(0).isEmpty();
    }

    private boolean isNavBarMagnificationEnabled() {
        return Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_display_magnification_navbar_enabled", 0) == 1;
    }

    private boolean isAccessibilityFloatingMenuEnabled() {
        return Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_button_mode", -1) == 1;
    }
}
