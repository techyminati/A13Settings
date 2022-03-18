package androidx.window.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.core.util.Consumer;
import androidx.window.extensions.layout.WindowLayoutComponent;
import androidx.window.extensions.layout.WindowLayoutInfo;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ExtensionWindowLayoutInfoBackend.kt */
/* loaded from: classes.dex */
public final class ExtensionWindowLayoutInfoBackend implements WindowBackend {
    @NotNull
    private final WindowLayoutComponent component;
    @NotNull
    private final ReentrantLock extensionWindowBackendLock = new ReentrantLock();
    @NotNull
    private final Map<Activity, MulticastConsumer> activityToListeners = new LinkedHashMap();
    @NotNull
    private final Map<Consumer<WindowLayoutInfo>, Activity> listenerToActivity = new LinkedHashMap();

    public ExtensionWindowLayoutInfoBackend(@NotNull WindowLayoutComponent component) {
        Intrinsics.checkNotNullParameter(component, "component");
        this.component = component;
    }

    @Override // androidx.window.layout.WindowBackend
    public void registerLayoutChangeCallback(@NotNull Activity activity, @NotNull Executor executor, @NotNull Consumer<WindowLayoutInfo> callback) {
        Unit unit;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(callback, "callback");
        ReentrantLock reentrantLock = this.extensionWindowBackendLock;
        reentrantLock.lock();
        try {
            MulticastConsumer multicastConsumer = this.activityToListeners.get(activity);
            if (multicastConsumer == null) {
                unit = null;
            } else {
                multicastConsumer.addListener(callback);
                this.listenerToActivity.put(callback, activity);
                unit = Unit.INSTANCE;
            }
            if (unit == null) {
                MulticastConsumer multicastConsumer2 = new MulticastConsumer(activity);
                this.activityToListeners.put(activity, multicastConsumer2);
                this.listenerToActivity.put(callback, activity);
                multicastConsumer2.addListener(callback);
                this.component.addWindowLayoutInfoListener(activity, multicastConsumer2);
            }
            Unit unit2 = Unit.INSTANCE;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override // androidx.window.layout.WindowBackend
    public void unregisterLayoutChangeCallback(@NotNull Consumer<WindowLayoutInfo> callback) {
        Intrinsics.checkNotNullParameter(callback, "callback");
        ReentrantLock reentrantLock = this.extensionWindowBackendLock;
        reentrantLock.lock();
        try {
            Activity activity = this.listenerToActivity.get(callback);
            if (activity != null) {
                MulticastConsumer multicastConsumer = this.activityToListeners.get(activity);
                if (multicastConsumer != null) {
                    multicastConsumer.removeListener(callback);
                    if (multicastConsumer.isEmpty()) {
                        this.component.removeWindowLayoutInfoListener(multicastConsumer);
                    }
                    Unit unit = Unit.INSTANCE;
                }
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    /* compiled from: ExtensionWindowLayoutInfoBackend.kt */
    @SuppressLint({"NewApi"})
    /* loaded from: classes.dex */
    private static final class MulticastConsumer implements java.util.function.Consumer<WindowLayoutInfo> {
        @NotNull
        private final Activity activity;
        @Nullable
        private WindowLayoutInfo lastKnownValue;
        @NotNull
        private final ReentrantLock multicastConsumerLock = new ReentrantLock();
        @NotNull
        private final Set<Consumer<WindowLayoutInfo>> registeredListeners = new LinkedHashSet();

        public MulticastConsumer(@NotNull Activity activity) {
            Intrinsics.checkNotNullParameter(activity, "activity");
            this.activity = activity;
        }

        public void accept(@NotNull WindowLayoutInfo value) {
            Intrinsics.checkNotNullParameter(value, "value");
            ReentrantLock reentrantLock = this.multicastConsumerLock;
            reentrantLock.lock();
            try {
                this.lastKnownValue = ExtensionsWindowLayoutInfoAdapter.INSTANCE.translate$window_release(this.activity, value);
                Iterator<T> it = this.registeredListeners.iterator();
                while (it.hasNext()) {
                    ((Consumer) it.next()).accept(this.lastKnownValue);
                }
                Unit unit = Unit.INSTANCE;
            } finally {
                reentrantLock.unlock();
            }
        }

        public final void addListener(@NotNull Consumer<WindowLayoutInfo> listener) {
            Intrinsics.checkNotNullParameter(listener, "listener");
            ReentrantLock reentrantLock = this.multicastConsumerLock;
            reentrantLock.lock();
            try {
                WindowLayoutInfo windowLayoutInfo = this.lastKnownValue;
                if (windowLayoutInfo != null) {
                    listener.accept(windowLayoutInfo);
                }
                this.registeredListeners.add(listener);
            } finally {
                reentrantLock.unlock();
            }
        }

        public final void removeListener(@NotNull Consumer<WindowLayoutInfo> listener) {
            Intrinsics.checkNotNullParameter(listener, "listener");
            ReentrantLock reentrantLock = this.multicastConsumerLock;
            reentrantLock.lock();
            try {
                this.registeredListeners.remove(listener);
            } finally {
                reentrantLock.unlock();
            }
        }

        public final boolean isEmpty() {
            return this.registeredListeners.isEmpty();
        }
    }
}
