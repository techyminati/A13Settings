package com.android.settings.accessibility;

import com.android.settings.accessibility.TextReadingResetController;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class TextReadingResetController$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ TextReadingResetController$$ExternalSyntheticLambda1 INSTANCE = new TextReadingResetController$$ExternalSyntheticLambda1();

    private /* synthetic */ TextReadingResetController$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((TextReadingResetController.ResetStateListener) obj).resetState();
    }
}
