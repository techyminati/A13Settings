package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
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
public class CombinedBiometricStatusPreferenceController extends BiometricStatusPreferenceController implements LifecycleObserver {
    private static final String KEY_BIOMETRIC_SETTINGS = "biometric_settings";
    FaceManager mFaceManager;
    FingerprintManager mFingerprintManager;
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

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected boolean hasEnrolledBiometrics() {
        return false;
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

    public CombinedBiometricStatusPreferenceController(Context context) {
        this(context, KEY_BIOMETRIC_SETTINGS, null);
    }

    public CombinedBiometricStatusPreferenceController(Context context, String str) {
        this(context, str, null);
    }

    public CombinedBiometricStatusPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, KEY_BIOMETRIC_SETTINGS, lifecycle);
    }

    public CombinedBiometricStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
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
        return Utils.hasFingerprintHardware(this.mContext) && Utils.hasFaceHardware(this.mContext);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateStateInternal();
    }

    private void updateStateInternal() {
        RestrictedLockUtils.EnforcedAdmin parentConsentRequired = ParentalControlsUtils.parentConsentRequired(this.mContext, 8);
        RestrictedLockUtils.EnforcedAdmin parentConsentRequired2 = ParentalControlsUtils.parentConsentRequired(this.mContext, 2);
        boolean z = true;
        boolean z2 = parentConsentRequired != null;
        if (parentConsentRequired2 == null) {
            z = false;
        }
        if (parentConsentRequired == null) {
            parentConsentRequired = parentConsentRequired2;
        }
        updateStateInternal(parentConsentRequired, z2, z);
    }

    @VisibleForTesting
    void updateStateInternal(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, boolean z, boolean z2) {
        if (!(z && z2)) {
            enforcedAdmin = null;
        }
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null) {
            restrictedPreference.setDisabledByAdmin(enforcedAdmin);
        }
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSummaryTextEnrolled() {
        return this.mContext.getString(R.string.security_settings_biometric_preference_summary_none_enrolled);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSummaryTextNoneEnrolled() {
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        int size = fingerprintManager != null ? fingerprintManager.getEnrolledFingerprints(getUserId()).size() : 0;
        FaceManager faceManager = this.mFaceManager;
        boolean z = faceManager != null && faceManager.hasEnrolledTemplates(getUserId());
        if (z && size > 1) {
            return this.mContext.getString(R.string.security_settings_biometric_preference_summary_both_fp_multiple);
        }
        if (z && size == 1) {
            return this.mContext.getString(R.string.security_settings_biometric_preference_summary_both_fp_single);
        }
        if (z) {
            return this.mContext.getString(R.string.security_settings_face_preference_summary);
        }
        if (size > 0) {
            return this.mContext.getResources().getQuantityString(R.plurals.security_settings_fingerprint_preference_summary, size, Integer.valueOf(size));
        }
        return this.mContext.getString(R.string.security_settings_biometric_preference_summary_none_enrolled);
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getSettingsClassName() {
        return Settings.CombinedBiometricSettingsActivity.class.getName();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    protected String getEnrollClassName() {
        return Settings.CombinedBiometricSettingsActivity.class.getName();
    }
}
