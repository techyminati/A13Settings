package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricStatusPreferenceController;
import com.android.settings.biometrics.ParentalControlsUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class FaceStatusPreferenceController extends BiometricStatusPreferenceController implements LifecycleObserver {
    public static final String KEY_FACE_SETTINGS = "face_settings";
    protected final FaceManager mFaceManager;
    @VisibleForTesting
    RestrictedPreference mPreference;

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceStatusPreferenceController(Context context) {
        this(context, KEY_FACE_SETTINGS, null);
    }

    public FaceStatusPreferenceController(Context context, String str) {
        this(context, str, null);
    }

    public FaceStatusPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, KEY_FACE_SETTINGS, lifecycle);
    }

    public FaceStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updateStateInternal();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(this.mPreferenceKey);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected boolean isDeviceSupported() {
        return !Utils.isMultipleBiometricsSupported(this.mContext) && Utils.hasFaceHardware(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public boolean hasEnrolledBiometrics() {
        return this.mFaceManager.hasEnrolledTemplates(getUserId());
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateStateInternal();
    }

    private void updateStateInternal() {
        updateStateInternal(ParentalControlsUtils.parentConsentRequired(this.mContext, 8));
    }

    @VisibleForTesting
    void updateStateInternal(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null) {
            restrictedPreference.setDisabledByAdmin(enforcedAdmin);
        }
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSummaryTextEnrolled() {
        return this.mContext.getResources().getString(R.string.security_settings_face_preference_summary);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSummaryTextNoneEnrolled() {
        return this.mContext.getResources().getString(R.string.security_settings_face_preference_summary_none);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSettingsClassName() {
        return Settings.FaceSettingsActivity.class.getName();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getEnrollClassName() {
        return FaceEnrollIntroduction.class.getName();
    }
}
