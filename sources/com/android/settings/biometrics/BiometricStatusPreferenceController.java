package com.android.settings.biometrics;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
/* loaded from: classes.dex */
public abstract class BiometricStatusPreferenceController extends BasePreferenceController {
    protected final LockPatternUtils mLockPatternUtils;
    protected final int mProfileChallengeUserId;
    protected final UserManager mUm;
    private final int mUserId;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    protected abstract String getEnrollClassName();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected abstract String getSettingsClassName();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    protected abstract String getSummaryTextEnrolled();

    protected abstract String getSummaryTextNoneEnrolled();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    protected abstract boolean hasEnrolledBiometrics();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    protected abstract boolean isDeviceSupported();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    protected boolean isUserSupported() {
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BiometricStatusPreferenceController(Context context, String str) {
        super(context, str);
        int myUserId = UserHandle.myUserId();
        this.mUserId = myUserId;
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUm = userManager;
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
        this.mProfileChallengeUserId = Utils.getManagedProfileId(userManager, myUserId);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!isDeviceSupported()) {
            return 3;
        }
        return isUserSupported() ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (isAvailable()) {
            preference.setVisible(true);
            preference.setSummary(hasEnrolledBiometrics() ? getSummaryTextEnrolled() : getSummaryTextNoneEnrolled());
        } else if (preference != null) {
            preference.setVisible(false);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        Context context = preference.getContext();
        UserManager userManager = UserManager.get(context);
        int userId = getUserId();
        if (Utils.startQuietModeDialogIfNecessary(context, userManager, userId)) {
            return false;
        }
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", hasEnrolledBiometrics() ? getSettingsClassName() : getEnrollClassName());
        if (!preference.getExtras().isEmpty()) {
            intent.putExtras(preference.getExtras());
        }
        intent.putExtra("android.intent.extra.USER_ID", userId);
        intent.putExtra("from_settings_summary", true);
        intent.putExtra("page_transition_type", 1);
        context.startActivity(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getUserId() {
        return this.mUserId;
    }
}
