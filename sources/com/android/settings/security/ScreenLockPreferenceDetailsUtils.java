package com.android.settings.security;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import androidx.window.R;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.security.screenlock.ScreenLockSettings;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class ScreenLockPreferenceDetailsUtils {
    private final Context mContext;
    private final LockPatternUtils mLockPatternUtils;
    private final int mProfileChallengeUserId;
    private final int mSourceMetricsCategory;
    private final UserManager mUm;
    private final int mUserId;

    public ScreenLockPreferenceDetailsUtils(Context context, int i) {
        int myUserId = UserHandle.myUserId();
        this.mUserId = myUserId;
        this.mContext = context;
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        this.mUm = userManager;
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
        this.mProfileChallengeUserId = Utils.getManagedProfileId(userManager, myUserId);
        this.mSourceMetricsCategory = i;
    }

    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_unlock_set_or_change);
    }

    public String getSummary(int i) {
        Integer summaryResId = getSummaryResId(i);
        if (summaryResId != null) {
            return this.mContext.getResources().getString(summaryResId.intValue());
        }
        return null;
    }

    public boolean isPasswordQualityManaged(int i, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        return enforcedAdmin != null && ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).getPasswordQuality(enforcedAdmin.component, i) == 524288;
    }

    public boolean shouldShowGearMenu() {
        return this.mLockPatternUtils.isSecure(this.mUserId);
    }

    public void openScreenLockSettings() {
        new SubSettingLauncher(this.mContext).setDestination(ScreenLockSettings.class.getName()).setSourceMetricsCategory(this.mSourceMetricsCategory).launch();
    }

    public boolean openChooseLockGenericFragment() {
        int i = this.mProfileChallengeUserId;
        if (i != -10000 && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i) && StorageManager.isFileEncryptedNativeOnly() && Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileChallengeUserId)) {
            return false;
        }
        new SubSettingLauncher(this.mContext).setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName()).setSourceMetricsCategory(this.mSourceMetricsCategory).setTransitionType(1).launch();
        return true;
    }

    private Integer getSummaryResId(int i) {
        if (this.mLockPatternUtils.isSecure(i)) {
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i);
            if (keyguardStoredPasswordQuality == 65536) {
                return Integer.valueOf((int) R.string.unlock_set_unlock_mode_pattern);
            }
            if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                return Integer.valueOf((int) R.string.unlock_set_unlock_mode_pin);
            }
            if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                return Integer.valueOf((int) R.string.unlock_set_unlock_mode_password);
            }
            return null;
        } else if (i == this.mProfileChallengeUserId || this.mLockPatternUtils.isLockScreenDisabled(i)) {
            return Integer.valueOf((int) R.string.unlock_set_unlock_mode_off);
        } else {
            return Integer.valueOf((int) R.string.unlock_set_unlock_mode_none);
        }
    }
}
