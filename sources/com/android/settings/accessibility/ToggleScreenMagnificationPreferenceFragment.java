package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.DialogCreatable;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.accessibility.MagnificationModePreferenceController;
import com.android.settings.utils.LocaleUtils;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ToggleScreenMagnificationPreferenceFragment extends ToggleFeaturePreferenceFragment implements MagnificationModePreferenceController.DialogHelper {
    private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
    private DialogCreatable mDialogDelegate;
    private MagnificationFollowTypingPreferenceController mFollowTypingPreferenceController;
    protected SwitchPreference mFollowingTypingSwitchPreference;
    private CheckBox mHardwareTypeCheckBox;
    private CheckBox mSoftwareTypeCheckBox;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    private CheckBox mTripleTapTypeCheckBox;

    private boolean hasShortcutType(int i, int i2) {
        return (i & i2) == i2;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_magnification;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 7;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    ComponentName getTileComponentName() {
        return null;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    CharSequence getTileName() {
        return null;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.accessibility_screen_magnification_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mPackageName = getString(R.string.accessibility_screen_magnification_title);
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(getPrefContext().getPackageName()).appendPath(String.valueOf((int) R.raw.accessibility_magnification_banner)).build();
        this.mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment$$ExternalSyntheticLambda2
            @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
            public final void onTouchExplorationStateChanged(boolean z) {
                ToggleScreenMagnificationPreferenceFragment.this.lambda$onCreateView$0(z);
            }
        };
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        updateFooterPreference();
        return onCreateView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    private void updateFooterPreference() {
        String string = getPrefContext().getString(R.string.accessibility_screen_magnification_about_title);
        String string2 = getPrefContext().getString(R.string.accessibility_screen_magnification_footer_learn_more_content_description);
        this.mFooterPreferenceController.setIntroductionTitle(string);
        this.mFooterPreferenceController.setupHelpLink(getHelpResource(), string2);
        this.mFooterPreferenceController.displayPreference(getPreferenceScreen());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        super.onPause();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        DialogCreatable dialogCreatable = this.mDialogDelegate;
        if (dialogCreatable != null) {
            Dialog onCreateDialog = dialogCreatable.onCreateDialog(i);
            this.mDialog = onCreateDialog;
            if (onCreateDialog != null) {
                return onCreateDialog;
            }
        }
        if (i == 1001) {
            AlertDialog showEditShortcutDialog = AccessibilityDialogUtils.showEditShortcutDialog(getPrefContext(), WizardManagerHelper.isAnySetupWizard(getIntent()) ? 3 : 2, getPrefContext().getString(R.string.accessibility_shortcut_title, this.mPackageName), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ToggleScreenMagnificationPreferenceFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i2);
                }
            });
            this.mDialog = showEditShortcutDialog;
            setupMagnificationEditShortcutDialog(showEditShortcutDialog);
            return this.mDialog;
        } else if (i != 1007) {
            return super.onCreateDialog(i);
        } else {
            return AccessibilityGestureNavigationTutorial.showAccessibilityGestureTutorialDialog(getPrefContext());
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void initSettingsPreference() {
        if (getContext().getResources().getBoolean(17891693)) {
            Preference preference = new Preference(getPrefContext());
            this.mSettingsPreference = preference;
            preference.setTitle(R.string.accessibility_magnification_mode_title);
            this.mSettingsPreference.setKey("screen_magnification_mode");
            this.mSettingsPreference.setPersistent(false);
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("general_categories");
            preferenceCategory.addPreference(this.mSettingsPreference);
            MagnificationModePreferenceController magnificationModePreferenceController = new MagnificationModePreferenceController(getContext(), "screen_magnification_mode");
            magnificationModePreferenceController.setDialogHelper(this);
            getSettingsLifecycle().addObserver(magnificationModePreferenceController);
            magnificationModePreferenceController.displayPreference(getPreferenceScreen());
            SwitchPreference switchPreference = new SwitchPreference(getPrefContext());
            this.mFollowingTypingSwitchPreference = switchPreference;
            switchPreference.setTitle(R.string.accessibility_screen_magnification_follow_typing_title);
            this.mFollowingTypingSwitchPreference.setSummary(R.string.accessibility_screen_magnification_follow_typing_summary);
            this.mFollowingTypingSwitchPreference.setKey("magnification_follow_typing");
            preferenceCategory.addPreference(this.mFollowingTypingSwitchPreference);
            this.mFollowTypingPreferenceController = new MagnificationFollowTypingPreferenceController(getContext(), "magnification_follow_typing");
            getSettingsLifecycle().addObserver(this.mFollowTypingPreferenceController);
            this.mFollowTypingPreferenceController.displayPreference(getPreferenceScreen());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.MagnificationModePreferenceController.DialogHelper
    public void showDialog(int i) {
        super.showDialog(i);
    }

    @Override // com.android.settings.accessibility.MagnificationModePreferenceController.DialogHelper
    public void setDialogDelegate(DialogCreatable dialogCreatable) {
        this.mDialogDelegate = dialogCreatable;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected int getShortcutTypeCheckBoxValue() {
        CheckBox checkBox = this.mSoftwareTypeCheckBox;
        if (checkBox == null || this.mHardwareTypeCheckBox == null) {
            return -1;
        }
        boolean isChecked = checkBox.isChecked();
        if (this.mHardwareTypeCheckBox.isChecked()) {
            isChecked = (isChecked ? 1 : 0) | true;
        }
        if (!this.mTripleTapTypeCheckBox.isChecked()) {
            int i = isChecked ? 1 : 0;
            int i2 = isChecked ? 1 : 0;
            int i3 = isChecked ? 1 : 0;
            return i;
        }
        int i4 = isChecked ? 1 : 0;
        char c = isChecked ? 1 : 0;
        return i4 | 4;
    }

    @VisibleForTesting
    void setupMagnificationEditShortcutDialog(Dialog dialog) {
        View findViewById = dialog.findViewById(R.id.software_shortcut);
        CheckBox checkBox = (CheckBox) findViewById.findViewById(R.id.checkbox);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = dialog.findViewById(R.id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(R.id.checkbox);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        View findViewById3 = dialog.findViewById(R.id.triple_tap_shortcut);
        CheckBox checkBox3 = (CheckBox) findViewById3.findViewById(R.id.checkbox);
        this.mTripleTapTypeCheckBox = checkBox3;
        setDialogTextAreaClickListener(findViewById3, checkBox3);
        View findViewById4 = dialog.findViewById(R.id.advanced_shortcut);
        if (this.mTripleTapTypeCheckBox.isChecked()) {
            findViewById4.setVisibility(8);
            findViewById3.setVisibility(0);
        }
        updateMagnificationEditShortcutDialogCheckBox();
    }

    private void setDialogTextAreaClickListener(View view, final CheckBox checkBox) {
        view.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                checkBox.toggle();
            }
        });
    }

    private void updateMagnificationEditShortcutDialogCheckBox() {
        int restoreOnConfigChangedValue = restoreOnConfigChangedValue();
        if (restoreOnConfigChangedValue == -1) {
            restoreOnConfigChangedValue = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), "com.android.server.accessibility.MagnificationController", 1);
            if (!this.mShortcutPreference.isChecked()) {
                restoreOnConfigChangedValue = 0;
            }
        }
        this.mSoftwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 1));
        this.mHardwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 2));
        this.mTripleTapTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 4));
    }

    private int restoreOnConfigChangedValue() {
        int i = this.mSavedCheckBoxValue;
        this.mSavedCheckBoxValue = -1;
        return i;
    }

    private static CharSequence getSoftwareShortcutTypeSummary(Context context) {
        boolean isFloatingMenuEnabled = AccessibilityUtil.isFloatingMenuEnabled(context);
        int i = R.string.accessibility_shortcut_edit_summary_software;
        if (!isFloatingMenuEnabled && AccessibilityUtil.isGestureNavigateEnabled(context)) {
            i = R.string.accessibility_shortcut_edit_summary_software_gesture;
        }
        return context.getText(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void registerKeysToObserverCallback(AccessibilitySettingsContentObserver accessibilitySettingsContentObserver) {
        super.registerKeysToObserverCallback(accessibilitySettingsContentObserver);
        ArrayList arrayList = new ArrayList();
        arrayList.add("accessibility_magnification_follow_typing_enabled");
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(arrayList, new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment$$ExternalSyntheticLambda3
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                ToggleScreenMagnificationPreferenceFragment.this.lambda$registerKeysToObserverCallback$2(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerKeysToObserverCallback$2(String str) {
        updateFollowTypingState();
    }

    private void updateFollowTypingState() {
        this.mFollowTypingPreferenceController.updateState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public List<String> getShortcutFeatureSettingsKeys() {
        List<String> shortcutFeatureSettingsKeys = super.getShortcutFeatureSettingsKeys();
        shortcutFeatureSettingsKeys.add("accessibility_display_magnification_enabled");
        return shortcutFeatureSettingsKeys;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public CharSequence getShortcutTypeSummary(Context context) {
        if (!this.mShortcutPreference.isChecked()) {
            return context.getText(R.string.switch_off_text);
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(context, "com.android.server.accessibility.MagnificationController", 1);
        ArrayList arrayList = new ArrayList();
        if (hasShortcutType(retrieveUserShortcutType, 1)) {
            arrayList.add(getSoftwareShortcutTypeSummary(context));
        }
        if (hasShortcutType(retrieveUserShortcutType, 2)) {
            arrayList.add(context.getText(R.string.accessibility_shortcut_hardware_keyword));
        }
        if (hasShortcutType(retrieveUserShortcutType, 4)) {
            arrayList.add(context.getText(R.string.accessibility_shortcut_triple_tap_keyword));
        }
        if (arrayList.isEmpty()) {
            arrayList.add(getSoftwareShortcutTypeSummary(context));
        }
        return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), null, LocaleUtils.getConcatenatedString(arrayList));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        saveNonEmptyUserShortcutType(shortcutTypeCheckBoxValue);
        optInAllMagnificationValuesToSettings(getPrefContext(), shortcutTypeCheckBoxValue);
        optOutAllMagnificationValuesFromSettings(getPrefContext(), ~shortcutTypeCheckBoxValue);
        this.mShortcutPreference.setChecked(shortcutTypeCheckBoxValue != 0);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        int dialogMetricsCategory;
        DialogCreatable dialogCreatable = this.mDialogDelegate;
        if (dialogCreatable != null && (dialogMetricsCategory = dialogCreatable.getDialogMetricsCategory(i)) != 0) {
            return dialogMetricsCategory;
        }
        if (i == 1001) {
            return 1813;
        }
        if (i == 1006) {
            return 1801;
        }
        if (i != 1007) {
            return super.getDialogMetricsCategory(i);
        }
        return 1802;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    int getUserShortcutTypes() {
        return getUserShortcutTypeFromSettings(getPrefContext());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onPreferenceToggled(String str, boolean z) {
        if (z && TextUtils.equals("accessibility_display_magnification_navbar_enabled", str)) {
            showDialog(1008);
        }
        MagnificationPreferenceFragment.setChecked(getContentResolver(), str, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        this.mToggleServiceSwitchPreference.setVisible(false);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), "com.android.server.accessibility.MagnificationController", 1);
        if (shortcutPreference.isChecked()) {
            optInAllMagnificationValuesToSettings(getPrefContext(), retrieveUserShortcutType);
            showDialog(1008);
        } else {
            optOutAllMagnificationValuesFromSettings(getPrefContext(), retrieveUserShortcutType);
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        showDialog(1001);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateShortcutPreferenceData() {
        int userShortcutTypeFromSettings = getUserShortcutTypeFromSettings(getPrefContext());
        if (userShortcutTypeFromSettings != 0) {
            PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut("com.android.server.accessibility.MagnificationController", userShortcutTypeFromSettings));
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void initShortcutPreference() {
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey(getShortcutPreferenceKey());
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle(getString(R.string.accessibility_shortcut_title, this.mPackageName));
        ((PreferenceCategory) findPreference("general_categories")).addPreference(this.mShortcutPreference);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateShortcutTitle(ShortcutPreference shortcutPreference) {
        shortcutPreference.setTitle(R.string.accessibility_screen_magnification_shortcut_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateShortcutPreference() {
        this.mShortcutPreference.setChecked(hasMagnificationValuesInSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), "com.android.server.accessibility.MagnificationController", 1)));
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    @VisibleForTesting
    void saveNonEmptyUserShortcutType(int i) {
        if (i != 0) {
            PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut("com.android.server.accessibility.MagnificationController", i));
        }
    }

    @VisibleForTesting
    static void optInAllMagnificationValuesToSettings(Context context, int i) {
        if ((i & 1) == 1) {
            optInMagnificationValueToSettings(context, 1);
        }
        if ((i & 2) == 2) {
            optInMagnificationValueToSettings(context, 2);
        }
        if ((i & 4) == 4) {
            optInMagnificationValueToSettings(context, 4);
        }
    }

    private static void optInMagnificationValueToSettings(Context context, int i) {
        if (i == 4) {
            Settings.Secure.putInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 1);
        } else if (!hasMagnificationValueInSettings(context, i)) {
            String convertKeyFromSettings = AccessibilityUtil.convertKeyFromSettings(i);
            String string = Settings.Secure.getString(context.getContentResolver(), convertKeyFromSettings);
            StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
            if (!TextUtils.isEmpty(string)) {
                stringJoiner.add(string);
            }
            stringJoiner.add("com.android.server.accessibility.MagnificationController");
            Settings.Secure.putString(context.getContentResolver(), convertKeyFromSettings, stringJoiner.toString());
        }
    }

    @VisibleForTesting
    static void optOutAllMagnificationValuesFromSettings(Context context, int i) {
        if ((i & 1) == 1) {
            optOutMagnificationValueFromSettings(context, 1);
        }
        if ((i & 2) == 2) {
            optOutMagnificationValueFromSettings(context, 2);
        }
        if ((i & 4) == 4) {
            optOutMagnificationValueFromSettings(context, 4);
        }
    }

    private static void optOutMagnificationValueFromSettings(Context context, int i) {
        if (i == 4) {
            Settings.Secure.putInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0);
            return;
        }
        String convertKeyFromSettings = AccessibilityUtil.convertKeyFromSettings(i);
        String string = Settings.Secure.getString(context.getContentResolver(), convertKeyFromSettings);
        if (!TextUtils.isEmpty(string)) {
            StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
            sStringColonSplitter.setString(string);
            while (true) {
                TextUtils.SimpleStringSplitter simpleStringSplitter = sStringColonSplitter;
                if (simpleStringSplitter.hasNext()) {
                    String next = simpleStringSplitter.next();
                    if (!TextUtils.isEmpty(next) && !"com.android.server.accessibility.MagnificationController".equals(next)) {
                        stringJoiner.add(next);
                    }
                } else {
                    Settings.Secure.putString(context.getContentResolver(), convertKeyFromSettings, stringJoiner.toString());
                    return;
                }
            }
        }
    }

    @VisibleForTesting
    static boolean hasMagnificationValuesInSettings(Context context, int i) {
        boolean hasMagnificationValueInSettings = (i & 1) == 1 ? hasMagnificationValueInSettings(context, 1) : false;
        if ((i & 2) == 2) {
            hasMagnificationValueInSettings |= hasMagnificationValueInSettings(context, 2);
        }
        return (i & 4) == 4 ? hasMagnificationValueInSettings | hasMagnificationValueInSettings(context, 4) : hasMagnificationValueInSettings;
    }

    private static boolean hasMagnificationValueInSettings(Context context, int i) {
        TextUtils.SimpleStringSplitter simpleStringSplitter;
        if (i == 4) {
            return Settings.Secure.getInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0) == 1;
        }
        String string = Settings.Secure.getString(context.getContentResolver(), AccessibilityUtil.convertKeyFromSettings(i));
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        sStringColonSplitter.setString(string);
        do {
            simpleStringSplitter = sStringColonSplitter;
            if (!simpleStringSplitter.hasNext()) {
                return false;
            }
        } while (!"com.android.server.accessibility.MagnificationController".equals(simpleStringSplitter.next()));
        return true;
    }

    private static int getUserShortcutTypeFromSettings(Context context) {
        boolean hasMagnificationValuesInSettings = hasMagnificationValuesInSettings(context, 1);
        if (hasMagnificationValuesInSettings(context, 2)) {
            hasMagnificationValuesInSettings = (hasMagnificationValuesInSettings ? 1 : 0) | true;
        }
        if (!hasMagnificationValuesInSettings(context, 4)) {
            int i = hasMagnificationValuesInSettings ? 1 : 0;
            int i2 = hasMagnificationValuesInSettings ? 1 : 0;
            int i3 = hasMagnificationValuesInSettings ? 1 : 0;
            return i;
        }
        int i4 = hasMagnificationValuesInSettings ? 1 : 0;
        char c = hasMagnificationValuesInSettings ? 1 : 0;
        return i4 | 4;
    }

    public static CharSequence getServiceSummary(Context context) {
        if (getUserShortcutTypeFromSettings(context) != 0) {
            return context.getText(R.string.accessibility_summary_shortcut_enabled);
        }
        return context.getText(R.string.accessibility_summary_shortcut_disabled);
    }
}
