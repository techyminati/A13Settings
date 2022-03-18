package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.homepage.contextualcards.ContextualCardManager$$ExternalSyntheticLambda8;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class ColumbusAppListPreferenceController extends BasePreferenceController implements SelectorWithWidgetPreference.OnClickListener, LifecycleObserver, OnStart {
    static final String COLUMBUS_LAUNCH_APP_SECURE_KEY = "columbus_launch_app";
    private static final String TAG = "ColumbusAppListPrefCtrl";
    private int mCurrentUser;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private PreferenceCategory mPreferenceCategory;
    private final LauncherApps mLauncherApps = (LauncherApps) this.mContext.getSystemService(LauncherApps.class);
    private final String mOpenAppValue = this.mContext.getString(R.string.columbus_setting_action_launch_value);

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ColumbusAppListPreferenceController(Context context, String str) {
        super(context, str);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ColumbusPreferenceController.isColumbusSupported(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mCurrentUser = ActivityManager.getCurrentUser();
            this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
            updateAppList();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int preferenceCount = this.mPreferenceCategory.getPreferenceCount();
        if (preferenceCount != 0) {
            String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), COLUMBUS_LAUNCH_APP_SECURE_KEY, this.mCurrentUser);
            for (int i = 0; i < preferenceCount; i++) {
                Preference preference2 = this.mPreferenceCategory.getPreference(i);
                if (preference2 instanceof ColumbusRadioButtonPreference) {
                    ColumbusRadioButtonPreference columbusRadioButtonPreference = (ColumbusRadioButtonPreference) preference2;
                    columbusRadioButtonPreference.setChecked(TextUtils.equals(stringForUser, columbusRadioButtonPreference.getKey()));
                }
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        updateAppList();
    }

    @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
    public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        if (selectorWithWidgetPreference instanceof ColumbusRadioButtonPreference) {
            ColumbusRadioButtonPreference columbusRadioButtonPreference = (ColumbusRadioButtonPreference) selectorWithWidgetPreference;
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "columbus_action", this.mOpenAppValue, this.mCurrentUser);
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), COLUMBUS_LAUNCH_APP_SECURE_KEY, columbusRadioButtonPreference.getKey(), this.mCurrentUser);
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "columbus_launch_app_shortcut", columbusRadioButtonPreference.getKey(), this.mCurrentUser);
            this.mMetricsFeatureProvider.action(this.mContext, 1757, columbusRadioButtonPreference.getKey());
            updateState(this.mPreferenceCategory);
        }
    }

    private void updateAppList() {
        PreferenceCategory preferenceCategory = this.mPreferenceCategory;
        if (preferenceCategory != null) {
            preferenceCategory.removeAll();
            List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList(null, UserHandle.of(this.mCurrentUser));
            activityList.sort(Comparator.comparing(ColumbusAppListPreferenceController$$ExternalSyntheticLambda1.INSTANCE));
            List<ShortcutInfo> queryForShortcuts = queryForShortcuts();
            for (final LauncherActivityInfo launcherActivityInfo : activityList) {
                ArrayList<? extends Parcelable> arrayList = (ArrayList) queryForShortcuts.stream().filter(new Predicate() { // from class: com.google.android.settings.gestures.columbus.ColumbusAppListPreferenceController$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$updateAppList$1;
                        lambda$updateAppList$1 = ColumbusAppListPreferenceController.lambda$updateAppList$1(launcherActivityInfo, (ShortcutInfo) obj);
                        return lambda$updateAppList$1;
                    }
                }).collect(Collectors.toCollection(ContextualCardManager$$ExternalSyntheticLambda8.INSTANCE));
                final Bundle bundle = new Bundle();
                bundle.putParcelable(COLUMBUS_LAUNCH_APP_SECURE_KEY, launcherActivityInfo.getComponentName());
                bundle.putParcelableArrayList("columbus_app_shortcuts", arrayList);
                makeRadioPreference(launcherActivityInfo.getComponentName().flattenToString(), launcherActivityInfo.getLabel(), launcherActivityInfo.getIcon(DisplayMetrics.DENSITY_DEVICE_STABLE), arrayList.isEmpty() ? null : new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusAppListPreferenceController$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ColumbusAppListPreferenceController.this.lambda$updateAppList$2(bundle, view);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$updateAppList$0(LauncherActivityInfo launcherActivityInfo) {
        return launcherActivityInfo.getLabel().toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateAppList$1(LauncherActivityInfo launcherActivityInfo, ShortcutInfo shortcutInfo) {
        return shortcutInfo.getPackage().equals(launcherActivityInfo.getComponentName().getPackageName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAppList$2(Bundle bundle, View view) {
        new SubSettingLauncher(this.mContext).setDestination(ColumbusGestureLaunchAppShortcutSettingsFragment.class.getName()).setSourceMetricsCategory(1871).setExtras(bundle).launch();
    }

    private List<ShortcutInfo> queryForShortcuts() {
        List<ShortcutInfo> list;
        LauncherApps.ShortcutQuery shortcutQuery = new LauncherApps.ShortcutQuery();
        shortcutQuery.setQueryFlags(9);
        try {
            list = this.mLauncherApps.getShortcuts(shortcutQuery, UserHandle.of(this.mCurrentUser));
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to query for shortcuts", e);
            list = null;
        }
        return list == null ? new ArrayList() : list;
    }

    private void makeRadioPreference(String str, CharSequence charSequence, Drawable drawable, View.OnClickListener onClickListener) {
        ColumbusRadioButtonPreference columbusRadioButtonPreference = new ColumbusRadioButtonPreference(this.mPreferenceCategory.getContext());
        columbusRadioButtonPreference.setKey(str);
        columbusRadioButtonPreference.setTitle(charSequence);
        columbusRadioButtonPreference.setIcon(drawable);
        columbusRadioButtonPreference.setOnClickListener(this);
        columbusRadioButtonPreference.setExtraWidgetOnClickListener(onClickListener);
        this.mPreferenceCategory.addPreference(columbusRadioButtonPreference);
    }
}
