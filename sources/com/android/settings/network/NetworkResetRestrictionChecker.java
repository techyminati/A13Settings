package com.android.settings.network;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settingslib.RestrictedLockUtilsInternal;
/* loaded from: classes.dex */
public class NetworkResetRestrictionChecker {
    private final Context mContext;
    private final UserManager mUserManager;

    public NetworkResetRestrictionChecker(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    boolean hasUserBaseRestriction() {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_network_reset", UserHandle.myUserId());
    }

    boolean isRestrictionEnforcedByAdmin() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_network_reset", UserHandle.myUserId()) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasUserRestriction() {
        return !this.mUserManager.isAdminUser() || hasUserBaseRestriction();
    }
}
