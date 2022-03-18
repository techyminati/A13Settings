package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
/* loaded from: classes.dex */
public class NetworkProviderWifiCallingPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final String PREFERENCE_CATEGORY_KEY = "provider_model_calling_category";
    private static final String TAG = "NetworkProviderWfcController";
    private NetworkProviderWifiCallingGroup mNetworkProviderWifiCallingGroup;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;

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

    public NetworkProviderWifiCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle) {
        this.mNetworkProviderWifiCallingGroup = createWifiCallingControllerForSub(lifecycle);
    }

    protected NetworkProviderWifiCallingGroup createWifiCallingControllerForSub(Lifecycle lifecycle) {
        return new NetworkProviderWifiCallingGroup(this.mContext, lifecycle, PREFERENCE_CATEGORY_KEY);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        NetworkProviderWifiCallingGroup networkProviderWifiCallingGroup = this.mNetworkProviderWifiCallingGroup;
        return (networkProviderWifiCallingGroup == null || !networkProviderWifiCallingGroup.isAvailable()) ? 3 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(PREFERENCE_CATEGORY_KEY);
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setVisible(isAvailable());
        this.mNetworkProviderWifiCallingGroup.displayPreference(preferenceScreen);
    }
}
