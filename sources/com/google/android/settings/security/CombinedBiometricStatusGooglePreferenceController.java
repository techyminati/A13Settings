package com.google.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import com.android.settings.Utils;
import com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController;
/* loaded from: classes2.dex */
public class CombinedBiometricStatusGooglePreferenceController extends CombinedBiometricStatusPreferenceController {
    FaceManager mFaceManager;
    FingerprintManager mFingerprintManager;
    private final SecurityContentManager mSecurityContentManager;

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CombinedBiometricStatusGooglePreferenceController(Context context, String str) {
        this(context, str, null);
    }

    public CombinedBiometricStatusGooglePreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.mSecurityContentManager = SecurityContentManager.getInstance(this.mContext);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    @Override // com.android.settings.biometrics.combination.CombinedBiometricStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        boolean z = true;
        boolean z2 = fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints(getUserId());
        FaceManager faceManager = this.mFaceManager;
        boolean z3 = faceManager != null && faceManager.hasEnrolledTemplates(getUserId());
        if (!z2 && !z3) {
            z = false;
        }
        preference.setIcon(this.mSecurityContentManager.getBiometricSecurityLevel(z).getEntryIconResId());
        preference.setOrder(this.mSecurityContentManager.getBiometricOrder());
    }
}
