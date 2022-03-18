package com.google.android.setupcompat.internal;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class ClockProvider$$ExternalSyntheticLambda1 implements Ticker {
    public static final /* synthetic */ ClockProvider$$ExternalSyntheticLambda1 INSTANCE = new ClockProvider$$ExternalSyntheticLambda1();

    private /* synthetic */ ClockProvider$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.android.setupcompat.internal.Ticker
    public final long read() {
        long nanoTime;
        nanoTime = System.nanoTime();
        return nanoTime;
    }
}
