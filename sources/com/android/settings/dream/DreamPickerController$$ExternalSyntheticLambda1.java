package com.android.settings.dream;

import com.android.settingslib.dream.DreamBackend;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DreamPickerController$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ DreamPickerController$$ExternalSyntheticLambda1 INSTANCE = new DreamPickerController$$ExternalSyntheticLambda1();

    private /* synthetic */ DreamPickerController$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        boolean z;
        z = ((DreamBackend.DreamInfo) obj).isActive;
        return z;
    }
}
