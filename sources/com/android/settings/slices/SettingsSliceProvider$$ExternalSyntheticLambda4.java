package com.android.settings.slices;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SettingsSliceProvider$$ExternalSyntheticLambda4 implements Runnable {
    public static final /* synthetic */ SettingsSliceProvider$$ExternalSyntheticLambda4 INSTANCE = new SettingsSliceProvider$$ExternalSyntheticLambda4();

    private /* synthetic */ SettingsSliceProvider$$ExternalSyntheticLambda4() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        SliceBackgroundWorker.shutdown();
    }
}
