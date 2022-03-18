package com.android.settings.wifi.calling;

import androidx.preference.Preference;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class WifiCallingSettingsForSub$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ WifiCallingSettingsForSub$$ExternalSyntheticLambda1 INSTANCE = new WifiCallingSettingsForSub$$ExternalSyntheticLambda1();

    private /* synthetic */ WifiCallingSettingsForSub$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((Preference) obj).isVisible();
    }
}
