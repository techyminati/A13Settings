package com.google.android.settings.aware;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.window.R;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.security.SecurityFeatureProvider;
import com.android.settings.security.trustagent.TrustAgentManager;
/* loaded from: classes2.dex */
public class AwareLockPreferenceController extends AwareTogglePreferenceController {
    private final LockPatternUtils mLockPatternUtils;
    private final TrustAgentManager mTrustAgentManager;

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AwareLockPreferenceController(Context context, String str) {
        super(context, str);
        SecurityFeatureProvider securityFeatureProvider = FeatureFactory.getFactory(context).getSecurityFeatureProvider();
        this.mLockPatternUtils = securityFeatureProvider.getLockPatternUtils(context);
        this.mTrustAgentManager = securityFeatureProvider.getTrustAgentManager();
    }

    @Override // com.google.android.settings.aware.AwareTogglePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mLockPatternUtils.isSecure(UserHandle.myUserId()) || !this.mHelper.isGestureConfigurable()) ? 5 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "aware_lock_enabled", 1) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mHelper.writeFeatureEnabled("aware_lock_enabled", z);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "aware_lock_enabled", z ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        CharSequence activeTrustAgentLabel = this.mTrustAgentManager.getActiveTrustAgentLabel(this.mContext, this.mLockPatternUtils);
        return !TextUtils.isEmpty(activeTrustAgentLabel) ? this.mContext.getString(R.string.lockpattern_settings_power_button_instantly_locks_summary, activeTrustAgentLabel) : this.mContext.getString(R.string.summary_placeholder);
    }
}
