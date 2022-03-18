package com.android.settingslib.enterprise;

import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda0 implements ActionDisabledLearnMoreButtonLauncher.ResolveActivityChecker {
    public static final /* synthetic */ ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda0 INSTANCE = new ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda0();

    private /* synthetic */ ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda0() {
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher.ResolveActivityChecker
    public final boolean canResolveActivityAsUser(PackageManager packageManager, String str, UserHandle userHandle) {
        boolean lambda$static$0;
        lambda$static$0 = ActionDisabledLearnMoreButtonLauncher.lambda$static$0(packageManager, str, userHandle);
        return lambda$static$0;
    }
}
