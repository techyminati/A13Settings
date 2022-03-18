package com.google.android.libraries.hats20;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public final class NetworkExecutor {
    private static volatile Executor networkExecutor;
    private static final Object networkExecutorLock = new Object();

    public static Executor getNetworkExecutor() {
        if (networkExecutor == null) {
            synchronized (networkExecutorLock) {
                if (networkExecutor == null) {
                    networkExecutor = new ThreadPoolExecutor(1, 3, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() { // from class: com.google.android.libraries.hats20.NetworkExecutor.1
                        private final AtomicInteger threadCount = new AtomicInteger(1);

                        @Override // java.util.concurrent.ThreadFactory
                        public Thread newThread(Runnable runnable) {
                            int andIncrement = this.threadCount.getAndIncrement();
                            StringBuilder sb = new StringBuilder(17);
                            sb.append("HaTS #");
                            sb.append(andIncrement);
                            return new Thread(runnable, sb.toString());
                        }
                    });
                    ((ThreadPoolExecutor) networkExecutor).allowCoreThreadTimeOut(true);
                }
            }
        }
        return networkExecutor;
    }
}
