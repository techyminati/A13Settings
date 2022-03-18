package com.android.settings.accessibility;

import com.android.settings.accessibility.TextReadingResetController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class TextReadingPreferenceFragment$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ TextReadingPreferenceFragment$$ExternalSyntheticLambda0 INSTANCE = new TextReadingPreferenceFragment$$ExternalSyntheticLambda0();

    private /* synthetic */ TextReadingPreferenceFragment$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        TextReadingResetController.ResetStateListener lambda$createPreferenceControllers$1;
        lambda$createPreferenceControllers$1 = TextReadingPreferenceFragment.lambda$createPreferenceControllers$1((AbstractPreferenceController) obj);
        return lambda$createPreferenceControllers$1;
    }
}
