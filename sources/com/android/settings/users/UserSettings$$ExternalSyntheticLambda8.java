package com.android.settings.users;

import android.content.pm.UserInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class UserSettings$$ExternalSyntheticLambda8 implements Predicate {
    public static final /* synthetic */ UserSettings$$ExternalSyntheticLambda8 INSTANCE = new UserSettings$$ExternalSyntheticLambda8();

    private /* synthetic */ UserSettings$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean lambda$getRealUsersCount$6;
        lambda$getRealUsersCount$6 = UserSettings.lambda$getRealUsersCount$6((UserInfo) obj);
        return lambda$getRealUsersCount$6;
    }
}
