package com.android.settings.network.telephony;

import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class AbstractMobileNetworkSettings$$ExternalSyntheticLambda4 implements Predicate {
    public static final /* synthetic */ AbstractMobileNetworkSettings$$ExternalSyntheticLambda4 INSTANCE = new AbstractMobileNetworkSettings$$ExternalSyntheticLambda4();

    private /* synthetic */ AbstractMobileNetworkSettings$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean isAvailable;
        isAvailable = ((AbstractPreferenceController) obj).isAvailable();
        return isAvailable;
    }
}
