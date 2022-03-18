package com.google.android.setupcompat.internal;

import com.google.android.setupcompat.internal.ClockProvider;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class ClockProvider {
    private static final Ticker SYSTEM_TICKER;
    private static Ticker ticker;

    /* loaded from: classes2.dex */
    public interface Supplier<T> {
        T get();
    }

    public static long timeInNanos() {
        return ticker.read();
    }

    public static long timeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(timeInNanos());
    }

    public static void resetInstance() {
        ticker = SYSTEM_TICKER;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ long lambda$setInstance$0(Supplier supplier) {
        return ((Long) supplier.get()).longValue();
    }

    public static void setInstance(final Supplier<Long> supplier) {
        ticker = new Ticker() { // from class: com.google.android.setupcompat.internal.ClockProvider$$ExternalSyntheticLambda0
            @Override // com.google.android.setupcompat.internal.Ticker
            public final long read() {
                long lambda$setInstance$0;
                lambda$setInstance$0 = ClockProvider.lambda$setInstance$0(ClockProvider.Supplier.this);
                return lambda$setInstance$0;
            }
        };
    }

    static {
        ClockProvider$$ExternalSyntheticLambda1 clockProvider$$ExternalSyntheticLambda1 = ClockProvider$$ExternalSyntheticLambda1.INSTANCE;
        SYSTEM_TICKER = clockProvider$$ExternalSyntheticLambda1;
        ticker = clockProvider$$ExternalSyntheticLambda1;
    }
}
