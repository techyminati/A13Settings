package com.android.settings.network.ims;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
/* loaded from: classes.dex */
class BooleanConsumer extends Semaphore implements Consumer<Boolean> {
    private volatile AtomicBoolean mValue = new AtomicBoolean();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BooleanConsumer() {
        super(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean get(long j) throws InterruptedException {
        tryAcquire(j, TimeUnit.MILLISECONDS);
        return this.mValue.get();
    }

    public void accept(Boolean bool) {
        if (bool != null) {
            this.mValue.set(bool.booleanValue());
        }
        release();
    }
}
